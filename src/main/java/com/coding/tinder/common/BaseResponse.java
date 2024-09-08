package com.coding.tinder.common;

import lombok.Data;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description common result
 * @ClassName BaseResponse
 */
@Data
public class BaseResponse<T>  {

    private final int code;

    private final String message;

    private final T data;

    private final String description;

    public BaseResponse(int code, String message, T data,String description) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.description = description;
    }

    public BaseResponse(int code, T data) {
        this(code,"",data,"");
    }

    public BaseResponse(int code){
        this(code,"",null,"");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),errorCode.getMessage(),null,errorCode.getDescription());
    }

    public BaseResponse(ErrorCode errorCode,String description){
        this(errorCode.getCode(),errorCode.getMessage(),null,description);
    }
}
