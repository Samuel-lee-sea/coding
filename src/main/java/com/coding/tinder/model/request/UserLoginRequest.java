package com.coding.tinder.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description 封装用户登录请求
 * @ClassName UserRegisterRequest
 */
@Data
public class UserLoginRequest implements Serializable {

    /**
     * 账户
     */
    private String userAccount;

    /**
     * 密码
     */
    private String password;

    private static final long serialVersionUID = -9143110731983822136L;
}
