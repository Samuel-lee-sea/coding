package com.coding.tinder.common;

import lombok.Data;

public enum ErrorCode {

    /**
     * common result enum
     */
    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40001,"参数错误",""),
    NULL_ERROR(40002,"请求数据为空",""),
    NOT_LOGIN(40003,"未登录",""),
    NO_AUTH(40004,"未授权",""),
    SYSTEM_ERROR(50000,"系统错误",""),
    ;


    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    private final int code;

    private final String message;

    private final String description;


    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
