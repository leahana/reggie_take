package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.BaseContext;
import com.itheima.common.CustomException;
import com.itheima.entity.*;
import com.itheima.mapper.OrderMapper;
import com.itheima.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders>
        implements OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;


    /**
     * 用户下单
     *
     * @param orders 需要:user表,购物车表,地址表,下单详情表
     */
    @Override
    public void submit(Orders orders) {
//        A. 获得当前用户id, 查询当前用户的购物车数据
//        B. 根据当前登录用户id, 查询用户数据
//        C. 根据地址ID, 查询地址数据
//        D. 组装订单明细数据, 批量保存订单明细
//        E. 组装订单数据, 批量保存订单数据
//        F. 删除当前用户的购物车列表数据

        //取出线程中的userId
        Long currentId = BaseContext.getCurrentId();

        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> lqwSc = new LambdaQueryWrapper<>();
        //设定购物车查询条件--用户id;
        lqwSc.eq(ShoppingCart::getUserId, currentId);

        //查询购物车集合
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(lqwSc);

        //判断购物车是否为空,为空抛异常
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            throw new CustomException("购物车为空，无法下单,请稍后再试");
        }

        //查询用户
        User user = userService.getById(currentId);

        //查询地址
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);

        //判断是否地址为空,为几百个抛异常
        if (addressBook == null) {
            throw new CustomException("地址有误,无法下单,请稍后再试");
        }

        //获取订单号#####分布式数据库ID生成
        long orderId = IdWorker.getId();

        //AtomicInteger，一个提供原子操作的Integer的类。
//        public final int get() //获取当前的值
//        public final int getAndSet(int newValue)//获取当前的值，并设置新的值
//        public final int getAndIncrement()//获取当前的值，并自增
//        public final int getAndDecrement() //获取当前的值，并自减
//        public final int getAndAdd(int delta) //获取当前的值，并加上预期的值
        //使用AtomicInteger之后，不需要加锁，也可以实现线程安全
        AtomicInteger amount = new AtomicInteger(0);

        //组装订单明细信息
        List<OrderDetail> orderDetails = shoppingCartList.stream().map((temp) -> {

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(temp.getNumber());
            orderDetail.setDishFlavor(temp.getDishFlavor());
            orderDetail.setDishId(temp.getDishId());
            orderDetail.setSetmealId(temp.getSetmealId());
            orderDetail.setName(temp.getName());
            orderDetail.setImage(temp.getImage());
            orderDetail.setAmount(temp.getAmount());

            amount.addAndGet(temp.getAmount()
                    //java精确除法运算（BigDecimal）
                    .multiply(new BigDecimal(temp.getNumber())).intValue());

            return orderDetail;


        }).collect(Collectors.toList());

        //组装订单信息

        //组装订单数据
        orders.setId(orderId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(currentId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));

        //向订单表插入数据，一条数据
        this.save(orders);

        //向订单明细表插入数据，多条数据
        orderDetailService.saveBatch(orderDetails);

        //清空购物车数据
        shoppingCartService.remove(lqwSc);
    }
}
