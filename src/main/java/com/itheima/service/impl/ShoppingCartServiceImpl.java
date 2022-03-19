package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.BaseContext;
import com.itheima.entity.ShoppingCart;
import com.itheima.mapper.ShoppingCartMapper;
import com.itheima.service.ShoppingCartService;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {

    @Override
    public boolean deleteDishOrSetmeal(Map<String, Long> map) {

        Long userId = BaseContext.getCurrentId();
        Long dishId = map.get("dishId");
        //根据用户id获取id下的购物车对象
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        //条件更新器
        LambdaUpdateWrapper<ShoppingCart> luw = new LambdaUpdateWrapper<>();
        int num;
        lqw.eq(ShoppingCart::getUserId, userId);

        if (dishId != null) {
            //添加的是菜品
            //先获取库存,之后再利用redis缓存处理库存
            lqw.eq(ShoppingCart::getDishId, dishId);
            ShoppingCart one = this.getOne(lqw);
            num = one.getNumber() - 1;
            if (num < 0) return false;
            luw.eq(ShoppingCart::getDishId, dishId).set(ShoppingCart::getNumber, num);
        } else {
            Long setmealId = map.get("setmealId");
            //添加的是套餐
            lqw.eq(ShoppingCart::getSetmealId, setmealId);
            ShoppingCart one = this.getOne(lqw);
            num = one.getNumber() - 1;
            if (num < 0) return false;
            luw.eq(ShoppingCart::getSetmealId, setmealId).set(ShoppingCart::getNumber, num);
        }


        boolean flag = this.update(luw);

        return flag;

    }
}
