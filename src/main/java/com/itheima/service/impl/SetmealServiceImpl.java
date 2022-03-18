package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.entity.SetmealDto;
import com.itheima.mapper.SetmealMapper;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 添加 同时添加菜品和套餐
     *
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal库
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
//======================================================================================//
        setmealDishes = setmealDishes.stream().peek((item) -> {
            //setmeal_dish的SetmealId关联了套餐表主键id
            //获取list集合中每一个setmealDto对象的id属性
            //绑定菜品id

            item.setSetmealId(setmealDto.getId());

            // return item;

        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);

    }


    /**
     * 删除 同事删除套餐和菜品关联数据
     *
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态 确定是否可以删除
        //初始化条件查询器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        //添加id条件
        lqw.in(Setmeal::getId, ids);

        //添加状态条件
        lqw.eq(Setmeal::getStatus, 1);

        //统计计数
        int count = this.count(lqw);

        //如果count>0 说明有菜品关联 不能删除 抛出异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中不能删除");
        }
        //如果可以删除,先删除表 setmeal 中的数据

        this.removeByIds(ids);

        //新建条件查询器准备删除SetmealDish数据
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();

        //添加条件reggie
        qw.in(SetmealDish::getId, ids);

        //删除关系 setmeal_dish表中的数据
        setmealDishService.remove(qw);
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {

    }

    @Override
    public SetmealDto getWithDish(Long id) {

        //setmealid
        //本类getById(从ServiceImpl继承来的
        Setmeal setmeal = this.getById(id);

        //初始化条件查询器
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();

        //添加id条件
        lqw.eq(SetmealDish::getId, id);

        //套餐包含的菜品
        List<SetmealDish> list = setmealDishService.list(lqw);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        setmealDto.setSetmealDishes(list);
        Long categoryId = setmeal.getCategoryId();

        Category byId = categoryService.getById(categoryId);

        setmealDto.setCategoryName(byId.getName());

        return setmealDto;
    }
}
