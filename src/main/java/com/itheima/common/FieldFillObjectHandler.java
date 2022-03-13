package com.itheima.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元数据对象处理器
 */

@Component
@Slf4j
public class FieldFillObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]..." + metaObject.toString());

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", 1L);
        metaObject.setValue("updateUser", 1L);


    }

    /**
     * 更新操作，自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        log.info("公共字段自动填充[update]..." + metaObject.toString());


        long id = Thread.currentThread().getId();
        log.info("FieldFillObjectHandler中updateFill"+"线程id为：{}",id);


        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", 1L);

    }
}
