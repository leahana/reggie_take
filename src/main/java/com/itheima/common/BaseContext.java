package com.itheima.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return id
     */
    public static Long getCurrentId(){
        //public T get() : 返回当前线程所对应的线程局部变量的值
        return threadLocal.get();
    }
}