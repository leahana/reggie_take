package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单内容mapper
 */
@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
