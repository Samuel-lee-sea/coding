package com.coding.tinder.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.coding.tinder.model.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author samuel
* @description user Service
* @createDate 2024-09-05 22:52:18
*/
public interface UserService extends IService<User> {

    /**
     * 获取推荐用户
     * @return 用户列表
     * @param pageNum 页数
     * @param pageSize 条数
     * @param request 请求头
     */
    IPage recommend(int pageNum, int pageSize, HttpServletRequest request);

    /**
     * 修改个人信息
     * @param user 编辑对象
     * @param loginUser 当前登录用户
     * @return
     */
    long editUser(User user,User loginUser);

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param password    用户密码
     * @param checkPassword 校验码("确认密码")
     * @param planetCode 星球编号
     * @return 用户id
     */
    long userRegister(String userAccount,String password,String checkPassword,String planetCode);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param password 用户密码
     * @param request 请求连接
     * @return 脱敏用户数据
     */
    User userLogin(String userAccount, String password, HttpServletRequest request);

    /**
     * 用户注销
     * @param request 请求连接
     * @return 注册状态
     */
    int userLogout(HttpServletRequest request);


    /**
     * 查询用户，可根据账号查询
     *
     *
     * @param pageNumber
     * @param pageSize
     * @param userAccount 账号
     * @return 用户集合
     */
    Page<User> searchUser(int pageNumber, int pageSize , String userAccount);


    /**
     *  根据标签查询用户
     * @param tagList 标签列表
     * @return 用户集合
     */
    List<User> searchUserByTag(List<String> tagList);

    /**
     * 用户信息脱敏
     *
     * @param user 用户对象
     * @return 脱敏后的用户信息
     */
    User getSafetyUser(User user);

    /**
     * 获取当前登录用户
     * @param request 获取请求体
     * @return 登录用户,为空时抛出异常
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 获取当前登录用户
     * @param request 获取请求体
     * @return 登录用户，为空时返回NULL
     */
    User getCurrentUserIsNull(HttpServletRequest request);

    /**
     * 鉴权判断 是管理员未true 不是为false
     * @param request request获取用户信息
     * @return boolean
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     *
     * @param longinUser 用户
     * @return boolean
     */
    boolean isAdmin(User longinUser);

    /**
     *  匹配用户
     * @param num 匹配人数
     * @param currentUser 当前登录用户
     * @return 用户列表
     */
    List<User> matchUser(Integer num, User currentUser);
}
