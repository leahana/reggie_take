package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.ShoppingCart;

import java.util.Map;

public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 减少购物车
     * @param
     * @param
     */
    boolean deleteDishOrSetmeal(Map<String,Long> map);
}
