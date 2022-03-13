package com.itheima.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;


/*
	上述的全局异常处理器上使用了的两个注解 @ControllerAdvice , @ResponseBody , 他们的作用分别为:
	@ControllerAdvice : 指定拦截那些类型的控制器;
	@ResponseBody: 将方法的返回值 R 对象转换为json格式的数据, 响应给页面;
	上述使用的两个注解, 也可以合并成为一个注解 @RestControllerAdvice
 */
//@ControllerAdvice(annotations = {RestController.class, Controller.class})
//@ResponseBody
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
/**
 * 全局异常处理
 */
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * @return
     */

    /**
     * - 指定捕获的异常类型为 SQLIntegrityConstraintViolationException
     * - 解析异常的提示信息, 获取出是那个值违背了唯一约束
     *
     * @return R.error(" ")
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException e) {
        //记录日志
        log.error(e.getMessage());
        //Duplicate entry:主键已经存在，不能重复添加

        if (e.getMessage().contains("Duplicate entry")) {
            //onstraintViolationException: Duplicate entry 'zhangsan' for key 'idx_username'
            //切割字符串
            String[] split = e.getMessage().split(" ");

            String msg = split[2] + "已存在";

            return R.error(msg);
        }
        return R.error("未知错误");
    }

}
