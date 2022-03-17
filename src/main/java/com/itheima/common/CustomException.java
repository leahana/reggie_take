package com.itheima.common;


import lombok.extern.slf4j.Slf4j;

/**
 * 自定义业务异常类
 */

@Slf4j
//补货controller层的异常
//@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class CustomException extends RuntimeException {

    public CustomException(String message) {
        super(message);
    }
}
