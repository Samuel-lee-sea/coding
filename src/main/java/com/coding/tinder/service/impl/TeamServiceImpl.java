package com.coding.tinder.service.impl;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.coding.tinder.common.ErrorCode;
import com.coding.tinder.exception.BusinessException;
import com.coding.tinder.mapper.TeamMapper;
import com.coding.tinder.model.domain.Team;
import com.coding.tinder.model.domain.User;
import com.coding.tinder.model.domain.UserTeam;
import com.coding.tinder.model.request.QueryTeamPageRequest;
import com.coding.tinder.model.vo.UserTeamVo;
import com.coding.tinder.service.TeamService;
import com.coding.tinder.service.UserService;
import com.coding.tinder.service.UserTeamService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static com.coding.tinder.constant.TeamConstant.*;
import static com.coding.tinder.model.enums.TeamStatusEnum.*;


/**
 * @author samuel
 * @description team Service impl
 * @createDate 2024-09-11 10:53:16
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
        implements TeamService {
    @Resource
    private UserTeamService userTeamService;

    @Resource
    private UserService userService;

    @Resource
    private TeamMapper teamMapper;

    @Override
    public List<UserTeamVo> recommendTeam(QueryTeamPageRequest queryTeamPageRequest, User currentUser) {
        String name = queryTeamPageRequest.getName();
        Integer status = queryTeamPageRequest.getStatus();
        String searchText = queryTeamPageRequest.getSearchText();
        int pageNumber = queryTeamPageRequest.getPageNumber();
        int pageSize = queryTeamPageRequest.getPageSize();
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();

        if (StrUtil.isNotBlank(name)) {
            teamQueryWrapper.like("name", name);
        }
        if (status != null) {
            teamQueryWrapper.eq("status", status);
        }
        if (searchText != null){
            teamQueryWrapper.like("name",searchText).or().like("description",searchText);
        }
        Page<Team> page = new Page<>(pageNumber, pageSize);
        Page<Team> teamPage = this.page(page, teamQueryWrapper);
        List<Team> teamList = teamPage.getRecords();
        Long id = currentUser == null ? null : currentUser.getId();
        List<UserTeamVo> userTeamVos = getUserTeamVos(id, teamList);
        //过滤掉当前用户已加入的队伍
        if (id != null){
            userTeamVos = userTeamVos.stream().filter(userTeamVo -> !userTeamVo.getIsSaveTeam()).collect(Collectors.toList());
        }

        return userTeamVos;
    }


    private List<UserTeamVo> getUserTeamVos(Long id, List<Team> teamList) {
        if (teamList.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        List<UserTeamVo> userTeamVoList = teamList.stream().map(team -> BeanUtil.toBean(team, UserTeamVo.class)).collect(Collectors.toList());
        userTeamVoList.forEach(userTeamVo -> {
            userTeamQueryWrapper.clear();
            userTeamQueryWrapper.eq("teamId",userTeamVo.getId());
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            for (UserTeam userTeam : userTeamList) {
                if ( id == null  || userTeam.getUserId().equals(id) ){
                    userTeamVo.setIsSaveTeam(true);
                    break;
                }
            }
            userTeamVo.setHasJoinNumber(userTeamList.size());
        });
        return userTeamVoList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createTeam(Team team, User user) {
        Long userId = user.getId();
        String name = team.getName();
        String description = team.getDescription();
        Integer maxUser = team.getMaxUser();
        Integer status = team.getStatus();
        String password = team.getPassword();
        Date expirationTime = team.getExpirationTime();
        if (ObjectUtil.isEmpty(name) || ObjectUtil.isNull(name)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍名称不能为空");
        }
        if (description.length() >= MAX_DESCRIPTION) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述最多不能超过512个字符");
        }
        if (ObjectUtil.isNull(maxUser) || maxUser < MIN_USER || maxUser > MAX_USER) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不符合要求");
        }
        if (ObjectUtil.isNull(status)) {
            status = PUBLIC_STATUS.getStatusId();
        }

        if (status == ENCRYPTION_STATUS.getStatusId()) {
            if (StrUtil.isEmpty(password) || StrUtil.isNotBlank(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍如需加密，密码不能为空");
            }
        }

        if (new Date().after(expirationTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间不能小于当前时间");
        }
        synchronized (userId.toString().intern()) {
            QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
            teamQueryWrapper.eq("userId", userId);
            long count = this.count(teamQueryWrapper);
            if (count >= MAX_TEAM_NUMBER) {
                throw new BusinessException(ErrorCode.NO_AUTH, "创建队伍失败，个人队伍数量最多为5个");
            }
            team.setId(null);
            team.setUserId(userId);
            team.setCreateUserId(userId);
            boolean result = this.save(team);
            Long teamId = team.getId();
            if (!result || teamId == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败");
            }
            UserTeam userTeam = new UserTeam();
            userTeam.setUserId(userId);
            userTeam.setTeamId(team.getId());
            userTeam.setJoinTime(new Date());
            boolean res = userTeamService.save(userTeam);
            if (!res) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "船舰队伍失败");
            }
        }
        return team.getId();
    }

    @Override
    public long joinTeam(Long teamId, User currentUser, String password) {
        Long userId = currentUser.getId();
        if (ObjectUtil.isNull(teamId) || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id不能为空");
        }
        Team team = this.getById(teamId);
        if (ObjectUtil.isNull(team)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        Integer status = team.getStatus();
        if (status == PRIVATE_STATUS.getStatusId()) {
            throw new BusinessException(ErrorCode.NO_AUTH, "私密的队伍不允许加入");
        }
        if (status == ENCRYPTION_STATUS.getStatusId() && StrUtil.isBlank(password)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "加密的队伍需要传递密码");
        }
        if (status == ENCRYPTION_STATUS.getStatusId() && StrUtil.isNotBlank(password)) {
            if (!team.getPassword().equals(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
        Integer maxUser = team.getMaxUser();
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        synchronized (teamId.toString().intern()) {
            long count = userTeamService.count(userTeamQueryWrapper);
            if (maxUser <= count) {
                throw new BusinessException(ErrorCode.NULL_ERROR, "队伍已满");
            }
            userTeamQueryWrapper.clear();
            userTeamQueryWrapper.eq("userId", userId);
            synchronized (userId.toString().intern()) {
            long countUserHasTeam = userTeamService.count(userTeamQueryWrapper);
            if (countUserHasTeam >= MAX_TEAM_NUMBER) {
                throw new BusinessException(ErrorCode.NO_AUTH, "用户最多创建或加入5个队伍");
            }
            userTeamQueryWrapper.eq("teamId", teamId);
            long countUser = userTeamService.count(userTeamQueryWrapper);
            if (countUser > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "同一用户不能重复加入同一个队伍");
            }
            UserTeam userTeam = new UserTeam();
            userTeam.setUserId(userId);
            userTeam.setTeamId(teamId);
            userTeam.setJoinTime(new Date());
            userTeamService.save(userTeam);
            }
        }
        return teamId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean dissolveTeam(Long teamId, User currentUser) {
        if (ObjectUtil.isNull(teamId) || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id不能为空");
        }
        Team team = this.getById(teamId);
        Long userId = team.getUserId();
        if (ObjectUtil.isNull(team)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        boolean admin = userService.isAdmin(currentUser);
        if (!currentUser.getId().equals(userId) && !admin) {
            throw new BusinessException(ErrorCode.NO_AUTH, "只有队长才能解散队伍");
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean remove = userTeamService.remove(queryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "解散队伍失败");
        }
        return this.removeById(teamId);
    }

    @Override
    public boolean outTeam(Long teamId, User currentUser) {
        if (ObjectUtil.isNull(teamId) || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id不能为空");
        }
        Team team = this.getById(teamId);
        if (ObjectUtil.isNull(team)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "队伍不存在");
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId).eq("userId", currentUser.getId());
        UserTeam userTeam = userTeamService.getOne(queryWrapper);
        if (ObjectUtil.isNull(userTeam)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "您已退出该队伍");
        }
        //队长退出，职位传递给最早加入队伍的人
        Long userId = team.getUserId();
        if (currentUser.getId().equals(userId)) {
            queryWrapper.clear();
            queryWrapper.eq("teamId", teamId).orderByAsc("joinTime");
            List<UserTeam> list = userTeamService.list(queryWrapper);
            queryWrapper.clear();
            queryWrapper.eq("teamId", teamId).eq("userId", currentUser.getId());
            if (list.isEmpty()) {
                //解散队伍
                boolean remove = userTeamService.remove(queryWrapper);
                if (remove){
                  return this.removeById(teamId);
                }
                return false;
            }else {
                //传递队长
                UserTeam newUser = list.get(0);
                team.setUserId(newUser.getUserId());
                this.updateById(team);
            }
        }
        return userTeamService.remove(queryWrapper);
    }

    @Override
    public boolean editTeam(Team team, User currentUser) {
        Long teamId = team.getId();
        String description = team.getDescription();
        Integer status = team.getStatus();
        String password = team.getPassword();
        Date expirationTime = team.getExpirationTime();
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍id不能为空");
        }
        if (StrUtil.isNotBlank(description) && description.length() > MAX_DESCRIPTION) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍描述不能超过512个字符");
        }
        if (status == ENCRYPTION_STATUS.getStatusId() && StrUtil.isBlank(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍设置为加密，必须传递密码");
        }
        if (new Date().after(expirationTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "过期时间不能小于当前时间");
        }
        return this.updateById(team);
    }

    @Override
    public Team queryById(Long id) {
        if (id == null || id < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return teamMapper.selectById(id);
    }

    @Override
    public List<UserTeamVo> queryMyCreateTeam(Long userId) {
        if (userId == null || userId < 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId",userId);
        List<Team> list = list(teamQueryWrapper);

        return this.getUserTeamVos(userId,list);
    }

    @Override
    public List<UserTeamVo> queryMyJoinTeam(Long id) {
        //select * from user_team a join team b on a.teamId = b.id where b.userId = #{id}
        QueryWrapper<UserTeam> teamQueryWrapper = new QueryWrapper<>();
        teamQueryWrapper.eq("userId",id);
        List<UserTeam> list = userTeamService.list(teamQueryWrapper);
        List<Long> teamIds = list.stream().map((UserTeam::getTeamId)).collect(Collectors.toList());
        List<Team> teams = this.listByIds(teamIds);
        return this.getUserTeamVos(id,teams);
    }


}




