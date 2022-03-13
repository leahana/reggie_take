package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.Setmeal;
import com.itheima.mapper.CategoryMapper;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除重写方法
     * 根据id删除分类，删除之前需要进行判断
     *
     * @param id
     */
    @Override
    public void remove(Long id) {
        //添加查询条件，根据分类id进行查询菜品数据
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        //设定查询条件是id
        lqw.eq(Dish::getCategoryId, id);

        //统计是否有数据
        int count = dishService.count(lqw);

        //如果已经关联菜品，抛出一个业务异常
        if (count > 0) {
            throw new CustomException("当前分类下关联了菜品,无法删除");
        }

        //添加查询条件,根据id查询套餐数据
        LambdaQueryWrapper<Setmeal> slqw = new LambdaQueryWrapper<>();

        //设定查询条件是id
        slqw.eq(Setmeal::getCategoryId, id);

        //统计是否有数据
        int count2 = setmealService.count(slqw);

        if (count2 > 0) {
            throw new CustomException("当前分类下关联了套餐,无法删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}
