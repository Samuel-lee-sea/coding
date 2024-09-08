package com.coding.tinder.common;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description resp util
 * @ClassName ResultUtil
 */
public class ResultUtil{

    /**
     * success
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<T>(0,"ok",data,"");
    }

    /**
     * error
     * @param errorCode
     * @return error msg
     */
    public static  BaseResponse<Object> error(ErrorCode errorCode){
        return new BaseResponse<Object>(errorCode);
    }

    /**
     * error
     * @param errorCode
     * @return error msg
     */
    public static  BaseResponse<Object> error(ErrorCode errorCode,String description){
        return new BaseResponse<Object>(errorCode,description);
    }

    /**
     * error
     * @return error msg
     */
    public static  BaseResponse<Object> error(int code,String message,String description){
        return new BaseResponse<Object>(code,message,null,description);
    }

}
