package com.coding.tinder.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.coding.tinder.common.BaseResponse;
import com.coding.tinder.common.ErrorCode;
import com.coding.tinder.common.ResultUtil;
import com.coding.tinder.exception.BusinessException;
import com.coding.tinder.model.domain.User;
import com.coding.tinder.model.request.DeleteRequest;
import com.coding.tinder.model.request.SearchRequest;
import com.coding.tinder.model.request.UserLoginRequest;
import com.coding.tinder.model.request.UserRegisterRequest;
import com.coding.tinder.service.UserService;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description user interface
 * @ClassName UserController
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("recommend")
    public BaseResponse<IPage> recommend(int pageNum, int pageSize, HttpServletRequest request){
        return ResultUtil.success(userService.recommend(pageNum, pageSize, request));
    }

    @GetMapping("searchByTag")
    public BaseResponse<List<User>> getUserByTag(@RequestParam(required = false) List<String> tags) {
        if (tags.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "tag can not be empty");
        }
        List<User> users = userService.searchUserByTag(tags);
        return ResultUtil.success(users);
    }

    @PostMapping("/editUser")
    public BaseResponse<Object> updateByUserinfo(@RequestBody User user, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(user)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "obj can not be empty");
        }
        User currentUser = userService.getCurrentUser(request);
        return ResultUtil.success(userService.editUser(user, currentUser));
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        return ResultUtil.success(userService.getCurrentUser(request));

    }

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAllBlank(userAccount, password, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long userId = userService.userRegister(userAccount, password, checkPassword, planetCode);
        return ResultUtil.success(userId);
    }

    @PostMapping(value = "/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "param not exist");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAllBlank(userAccount, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "pwd can not be empty");
        }
        User user = userService.userLogin(userAccount, password, httpServletRequest);
        return ResultUtil.success(user);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int i = userService.userLogout(httpServletRequest);
        return ResultUtil.success(i);
    }


    @GetMapping("/search")
    public BaseResponse<Page<User>> searchUser(SearchRequest searchRequest, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (searchRequest == null){
          searchRequest =  new SearchRequest();
          searchRequest.setUserAccount("");
        }
        String userAccount = searchRequest.getUserAccount();
        int pageNumber = searchRequest.getPageNumber();
        int pageSize = searchRequest.getPageSize();
        Page<User> usersPage = userService.searchUser(pageNumber,pageSize,userAccount);
        List<User> userList = usersPage.getRecords();
        userList = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        System.out.println(userList);
        usersPage.setRecords(userList);
        return ResultUtil.success(usersPage);

    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteById(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id can not be empty");
        }
        Long id = deleteRequest.getId();
        if (ObjectUtils.isEmpty(id) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id can not be empty or you are not admin");
        }
        boolean result = userService.removeById(id);
        return ResultUtil.success(result);
    }


    @GetMapping("/match")
    public BaseResponse<List<User>> matchUser(Integer num,HttpServletRequest request){
        if (num == null || num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);

        return ResultUtil.success(userService.matchUser(num,currentUser));
    }

}
