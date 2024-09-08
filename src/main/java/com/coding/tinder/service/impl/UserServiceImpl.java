package com.coding.tinder.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coding.tinder.common.ErrorCode;
import com.coding.tinder.exception.BusinessException;
import com.coding.tinder.mapper.TagMapper;
import com.coding.tinder.mapper.UserMapper;
import com.coding.tinder.model.domain.User;
import com.coding.tinder.service.UserService;
import com.coding.tinder.service.util.AlgorithmUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import javafx.util.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.coding.tinder.constant.UserConstant.*;


/**
 * @author samuel
 * @description user Service impl
 * @createDate 2024-09-05 22:52:18
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final TagMapper tagMapper;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public IPage<User> recommend(int pageNum, int pageSize, HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        Long id = currentUser.getId();
        String recommendKey = String.format(RECOMMEND_USER + "%s", id);
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        String userPageStr = (String) valueOperations.get(recommendKey);
        if (StrUtil.isNotBlank(userPageStr)) {
            return JSONUtil.toBean(userPageStr, Page.class);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        try {
            userPageStr = JSONUtil.toJsonStr(userPage);
            valueOperations.set(recommendKey, userPageStr, 1, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("set redis error{}", e.getMessage());
        }
        return userPage;
    }

    @Override
    public long editUser(User user, User loginUser) {
        long userId = user.getId();
        //id不能为空
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //修改信息为当前登录角色或管理员才可以修改
        if (userId != loginUser.getId() && !isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "权限不足");
        }

        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "用户不存在");
        }
        return userMapper.updateById(user);
    }

    @Override
    public long userRegister(String userAccount, String password, String checkPassword, String planetCode) {
        //1.校验
        boolean loginParams = StringUtils.isAllBlank(userAccount, password, checkPassword, planetCode);
        if (loginParams) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数不能为空");
        }
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        if (userAccount.length() < USER_ACCOUNT || password.length() < USER_PASSWORD) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码或账号过短");
        }

        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }
        if (planetCode.length() > PLANET_CODE_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号不能大于5位");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account", userAccount);
        Long aLong = userMapper.selectCount(userQueryWrapper);
        if (aLong > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该账号已被注册");
        }
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("planet_code", planetCode);
        aLong = userMapper.selectCount(userQueryWrapper);
        if (aLong > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已被注册");
        }
        //2.加密
        String encryptionPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptionPassword);
        user.setPlanetCode(planetCode);
        //3.添加
        boolean save = super.save(user);
        if (!save) {
            throw new BusinessException("添加失败", 40005, "注册失败");
        }
        //4.返回用户id
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String password, HttpServletRequest request) {
        //1.校验
        boolean allBlank = StringUtils.isAllBlank(userAccount, password);
        if (allBlank) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号密码不能为空");
        }
        if (userAccount.length() < USER_ACCOUNT || password.length() < USER_PASSWORD) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码长度不足");
        }
        Matcher matcher = Pattern.compile(VALID_PATTERN).matcher(userAccount);
        if (!matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号不能包含特殊字符");
        }

        //2.加密
        String encryptionPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes(StandardCharsets.UTF_8));
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_account", userAccount);
        userQueryWrapper.eq("user_password", encryptionPassword);
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptionPassword);

        //3.校验用户是否存在
        User userInfo = userMapper.selectOne(userQueryWrapper);
        if (userInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        //4.用户信息脱敏z
        User safetyUser = getSafetyUser(userInfo);
        request.getSession().setAttribute(USER_LOGIN_STATUS, safetyUser);
        //5.返回用户
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }

    @Override
    public Page<User> searchUser(int pageNumber, int pageSize, String userAccount) {
        Page<User> userPage = new Page<>(pageNumber, pageSize);
        if (StrUtil.isBlank(userAccount)) {
            return userMapper.selectPage(userPage, new QueryWrapper<>());
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.like("userAccount", userAccount);
        return userMapper.selectPage(userPage, userQueryWrapper);
    }

    @Override
    public List<User> searchUserByTag(List<String> tagList) {
        if (tagList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未传入标签");
        }
        List<User> users = userMapper.selectList(null);
        Gson gson = new Gson();
        List<User> collect = users.stream()
                .filter(user -> {
                    String tags = user.getTags();
                    Set<String> templateTags = gson.fromJson(tags, new TypeToken<Set<String>>() {
                    }.getType());
                    templateTags = Optional.ofNullable(templateTags).orElse(new HashSet<>());
                    for (String tagName : tagList) {
                        if (!templateTags.contains(tagName)) {
                            return false;
                        }
                    }
                    return true;
                })
                .map(this::getSafetyUser)
                .collect(Collectors.toList());
        return collect;
    }


    @Deprecated
    private List<User> searchUserByTagBySql(List<String> tagList) {
        if (tagList.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未传入标签");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        userMapper.selectCount(queryWrapper);
        for (String tag : tagList) {
            queryWrapper = queryWrapper.like("tags", tag);
        }
        List<User> users = userMapper.selectList(queryWrapper);
        List<User> safetyUserList = users.stream().map(this::getSafetyUser).collect(Collectors.toList());
        return safetyUserList;
    }

    @Override
    public User getSafetyUser(User user) {
        if (user == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setProfile(user.getProfile());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setTags(user.getTags());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUpdateTime(user.getUpdateTime());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setPlanetCode(user.getPlanetCode());
        return safetyUser;
    }

    @Override
    public User getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "未登录，请先登录！");
        }
        User currentUser = getById(user.getId());
        if (currentUser == null) {
            log.error("id为:{}", user.getId());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取当前用户错误");
        }
        return getSafetyUser(currentUser);
    }

    @Override
    public User getCurrentUserIsNull(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (user == null) {
            return null;
        }
        User currentUser = getById(user.getId());
        if (currentUser == null) {
            log.error("id为:{}", user.getId());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "获取当前用户错误");
        }
        return getSafetyUser(currentUser);
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATUS);
        if (user == null || user.getUserRole() != ADMIN_ROLE) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isAdmin(User longinUser) {
        return longinUser != null && longinUser.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public List<User> matchUser(Integer num, User currentUser) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.select("id", "tags");
        userQueryWrapper.isNotNull("tags").or().ne("tags", "");
        List<User> userList = this.list(userQueryWrapper);
        List<Pair<Integer, Long>> indexDistance = new ArrayList<>();
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        List<String> loginUserTags = gson.fromJson(currentUser.getTags(), type);
        for (int i = 0; i < userList.size(); i++) {
            Long id = userList.get(i).getId();
            String userTagsStr = userList.get(i).getTags();
            if (StrUtil.isBlank(userTagsStr) || currentUser.getId().equals(id)) {
                continue;
            }
            List<String> userTags = gson.fromJson(userTagsStr, type);
            long distance = AlgorithmUtil.minDistanceTags(loginUserTags, userTags);
            indexDistance.add(new Pair<>(i, distance));
        }
        List<Integer> indexList = indexDistance.stream().sorted((pair1, pair2) -> (int) (pair1.getValue() - pair2.getValue()))
                .limit(num)
                .map(Pair::getKey).collect(Collectors.toList());
        List<Long> userIdList = indexList.stream().map(index -> userList.get(index).getId()).collect(Collectors.toList());
        List<User> users = userMapper.selectBatchIds(userIdList);
        Map<Long, List<User>> userIdUserListMap = users.stream().map(this::getSafetyUser).collect(Collectors.groupingBy(User::getId));
        ArrayList<User> finalUserList = new ArrayList<>();
        for (Long userId : userIdList) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }
}




