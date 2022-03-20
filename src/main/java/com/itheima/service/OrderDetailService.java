package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.OrderDetail;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderDetailService extends IService<OrderDetail> {
}
