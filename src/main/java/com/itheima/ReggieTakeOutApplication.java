package com.itheima;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
/*
在SpringBoot项目中, 在引导类/配置类上加了该注解后,
会自动扫描项目中(当前包及其子包下)的
@WebServlet , @WebFilter , @WebListener
注解, 自动注册Servlet的相关组件 ;
 */
@ServletComponentScan//扫描@WebFilter的包
@EnableTransactionManagement //开启对事物管理的支持
@EnableCaching//开启Spring Cache注解方式缓存功能
public class ReggieTakeOutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeOutApplication.class, args);
        log.info("项目启动...");
        //log.error("link...");
    }
}








































