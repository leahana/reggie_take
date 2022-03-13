package com.itheima.config;


import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * 配置Mp的分页插件
 */
public class MybatisPlusConfig {

    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mpI = new MybatisPlusInterceptor();
        mpI.addInnerInterceptor(new PaginationInnerInterceptor());
        return mpI;
    }
}
