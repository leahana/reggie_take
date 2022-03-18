package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.entity.ShoppingCart;
import com.itheima.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 购物车controller
 */
@Transactional
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;


    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
//        菜品数据:dishId
//        {"amount":118,"dishFlavor":"不要蒜,微辣","dishId":"1397851099502260226",
//        "name":"全家福","image":"a53a4e6a-3b83-4044-87f9-9d49b30a8fdc.jpg"}
//        套餐数据:setmealId
//        {"amount":38,"setmealId":"1423329486060957698","name":"营养超值工作餐",
//        "image":"9cd7a80a-da54-4f46-bf33-af3576514cec.jpg"}

//        A. 获取当前登录用户，为购物车对象赋值
//        B. 根据当前登录用户ID 及 本次添加的菜品ID/套餐ID，查询购物车数据是否存在
//        C. 如果已经存在，就在原来数量基础上加1
//        D. 如果不存在，则添加到购物车，数量默认就是1

        //记录日志
        log.info("购物车数据:{}", shoppingCart.toString());

        //用户id,指定当前用户购物车
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //获取菜品id
        Long dishId = shoppingCart.getDishId();

        //条件查询器
        LambdaQueryWrapper<ShoppingCart> lqwSc = new LambdaQueryWrapper<>();

        //设置查询条件是userid
        lqwSc.eq(ShoppingCart::getUserId, currentId);


        if (dishId != null) {
            //添加的是菜品
            lqwSc.eq(ShoppingCart::getDishId, dishId);
        } else {
            //添加的是套餐
            lqwSc.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //查询当前菜品或者套餐是否在购物车内
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        ShoppingCart cartOne = shoppingCartService.getOne(lqwSc);

        if (cartOne != null) {
            //如果购物车已有,数量+1
            Integer number = cartOne.getNumber();
            cartOne.setNumber(number + 1);
            shoppingCartService.updateById(cartOne);
        } else {
            //如果购物车不存在,添加购物车
            shoppingCart.setNumber(1);
            //这里已经经过过滤器,所以不能自动填充
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartOne = shoppingCart;
        }
        return R.success(cartOne);
    }


    /**
     * 查询所有
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        //记录日志
        log.info("查看购物车");

        //条件查询器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        //添加查询条件--用户id
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        //添加排序条件--创建时间降序
        lqw.orderByDesc(ShoppingCart::getCreateTime);

        //查询集合
        List<ShoppingCart> list = shoppingCartService.list(lqw);

        return R.success(list);
    }


    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        //条件查询器
        LambdaQueryWrapper<ShoppingCart> lqw = new LambdaQueryWrapper<>();

        //添加条件--用户id
        lqw.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());

        //SQL:delete from shopping_cart where user_id = ?
        shoppingCartService.remove(lqw);

        return R.success("清空购物车成功");
    }


    @PostMapping("/sub")
    public R<String> sub(@RequestBody Map<String, Long> map) {


        //log.error(map.toString());


        shoppingCartService.deleteDishOrSetmeal(map);


        return R.success("移除成功");
    }

}

























