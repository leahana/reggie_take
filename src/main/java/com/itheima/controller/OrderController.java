package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Orders;
import com.itheima.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;



    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        //记录日志
        log.info("订单数据：{}",orders);

        //用户下单逻辑较为复杂
        orderService.submit(orders);

        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page<Orders>> queryOrders(int page, int pageSize){


        //分页构造器
        Page<Orders> iPage = new Page<>(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Orders> lqw = new LambdaQueryWrapper<>();

        //根据排序条件, 根据sort 排序 升序
        lqw.orderByAsc(Orders::getOrderTime);

        //分页查询
        orderService.page(iPage, lqw);

        return R.success(iPage);

    }

}
