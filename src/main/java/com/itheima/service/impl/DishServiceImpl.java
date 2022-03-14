package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Dish;
import com.itheima.entity.DishDto;
import com.itheima.entity.DishFlavor;
import com.itheima.mapper.DishMapper;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {

    //菜品口味service
    @Autowired
    private DishFlavorService dishFlavorService;

    /*
①. 保存菜品基本信息 ;
②. 获取保存的菜品ID ;
③. 获取菜品口味列表，遍历列表，为菜品口味对象属性dishId赋值;
④. 批量保存菜品口味列表;
     */

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //①. 保存菜品基本信息 ;
        this.save(dishDto);

        //菜品id
        Long dishDtoId = dishDto.getId();

        //菜品口味集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        /*
        "flavors":[
        {
                "name":"辣度",
                "value":"[\"不辣\",\"微辣\",\"中辣\",\"重辣\"]",
                "showOption":false
        },
        {
                "name":"忌口",
                "value":"[\"不要葱\",\"不要蒜\",\"不要香菜\",\"不要辣\"]",
                "showOption":false
        }*/


        flavors.stream().map((item) -> {
            item.setDishId(dishDtoId);
            return item;
        }).collect(Collectors.toList());


        //保存菜品口味数据到菜品口味表dish_flavors
        dishFlavorService.saveBatch(flavors);

    }
}
