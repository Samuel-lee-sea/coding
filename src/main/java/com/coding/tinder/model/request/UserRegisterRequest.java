package com.coding.tinder.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description 封装用户注册请求
 * @ClassName UserRegisterRequest
 */
@Data
public class UserRegisterRequest implements Serializable {

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String checkPassword;

    /**
     * 星球编号
     */
    private String planetCode;

    private static final long serialVersionUID = 1449811621772410599L;
}
