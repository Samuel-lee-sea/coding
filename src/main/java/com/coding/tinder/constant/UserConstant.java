package com.coding.tinder.constant;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @ClassName UserConstant
 */
public interface UserConstant {

    /**
     * user login key
     */
    String USER_LOGIN_STATUS = "userLoginStatus";

    /**
     * username length
     */
    int USER_ACCOUNT = 4;

    /**
     * psw length
     */
    int USER_PASSWORD = 8;

    /**
     * check special char
     */
    String VALID_PATTERN = "^[a-zA-Z][a-zA-Z0-9_]{4,15}$";

    /**
     * salt
     */
    String SALT = "samuel2024";

//权限校验
    /**
     * user
     */
    int DEFAULT_ROLE = 0;

    /**
     * admin
     */
    int ADMIN_ROLE = 1;

    /**
     *
     */
    int PLANET_CODE_LENGTH = 5;

    String RECOMMEND_USER = "user:recommend:";
}
