package com.coding.tinder.exception;

import com.coding.tinder.common.BaseResponse;
import com.coding.tinder.common.ErrorCode;
import com.coding.tinder.common.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author samuel
 * @version 1.0
 * @project user-center
 * @description global exception
 * @ClassName GlobalExceptionHandler
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<Object> businessException(BusinessException e){
        log.error("businessException:" +e.getMessage(),e);
        return ResultUtil.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<Object> runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException",e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }
}
