package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 套餐分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
