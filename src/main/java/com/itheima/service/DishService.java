package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Dish;
import com.itheima.entity.DishDto;

import java.util.List;


public interface DishService extends IService<Dish> {



    //新增菜品，同时插入菜品对应的口味数据，需要操作两张表：dish、dish_flavor
    void saveWithFlavor(DishDto dishDto);



    //根据id查询菜品信息和对应的口味信息
    DishDto getByIdWithFlavor(Long id);



    //更新菜品信息，同时更新对应的口味信息
    void updateWithFlavor(DishDto dishDto);



    //删除菜品信息, 同时删除对应的口味信息
    boolean deleteWithFlavor(List<Long> ids);



    //批量起售停售
    boolean updateStatus(Integer status, List<Long> ids);
}
