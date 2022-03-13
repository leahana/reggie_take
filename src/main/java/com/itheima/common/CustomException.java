package com.itheima.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

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
