package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

/**
 * 购物车mapper
 */
@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
}
