package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Dish;
import com.itheima.entity.DishDto;
import com.itheima.entity.DishFlavor;
import com.itheima.mapper.DishMapper;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import org.springframework.beans.BeanUtils;
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


        //把口味缺少的dishId set进去
        flavors.stream().peek((item) -> {
            item.setDishId(dishDtoId);
        }).collect(Collectors.toList());


        //保存菜品口味数据到菜品口味表dish_flavors
        dishFlavorService.saveBatch(flavors);

    }


    /**
     * 根据id查询数据菜品(包含口味
     *
     * @param id
     */
    @Override
    public DishDto queryWithFlavorById(Long id) {
        //本类getById(从ServiceImpl继承来的
        Dish dish = this.getById(id);

        //创建DishDto容器 准备存放数据
        DishDto dishDto = new DishDto();

        //拷贝数据
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品口味信息(从dish_flavor表查
        //新建查询条件
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();

        //设定查询条件
        lqw.eq(dish.getId() != null, DishFlavor::getId, dish.getId()).eq(DishFlavor::getIsDeleted, 0);

        List<DishFlavor> list = dishFlavorService.list(lqw);
        dishDto.setFlavors(list);

        return dishDto;

    }


    /**
     * 更新菜品包括口味id
     *
     * @param dishDto
     */
    @Override
    public void updateWithFlavor(DishDto dishDto) {

        //更新 dish表的基本信息 调用本类update方法
        this.updateById(dishDto);

        //获取DishId
        Long dishId = dishDto.getId();

        //清理当前菜品口味数据--删除dish_flavor表的数据
        //设置删除条件器
        LambdaQueryWrapper<DishFlavor> lqw = new LambdaQueryWrapper<>();
        lqw.eq(dishId != null, DishFlavor::getDishId, dishId);

        //根据条件删除dish_flavor表的口味数据
        dishFlavorService.remove(lqw);

        //添加提交过来的Flavor数据
        List<DishFlavor> list = dishDto.getFlavors();

        //菜品口味表:dish_flavor中 dish_id关联了菜品id
        list = list.stream().peek((item) -> {
            //获取list集合中每一个DishFlavor对象的id属性
            //给这个口味绑定这个菜品的Id(
            item.setDishId(dishId);
        }).collect(Collectors.toList());

        //dish_flavor表的insert操作
        dishFlavorService.saveBatch(list);
    }


    /**
     * 更新菜品状态/批量更新菜品状态
     *
     * @param status
     * @param ids
     * @return
     */
    @Override
    public boolean updateStatusByIds(Integer status, List<Long> ids) {

        //条件查询器
        LambdaUpdateWrapper<Dish> luw = new LambdaUpdateWrapper();
        luw.in(Dish::getId, ids).set(Dish::getStatus, status);

        boolean flag = this.update(luw);

        return flag;

    }


    /**
     * 删除菜品/批量删除菜品(逻辑删除
     *
     * @param ids
     * @return
     */
    @Override
    public boolean deleteWithFlavorByIds(List<Long> ids) {

        //根据条件删除dish_flavor表的口味数据
        //初始化条件更新器(逻辑删除菜品口味
        LambdaUpdateWrapper<DishFlavor> luwDF = new LambdaUpdateWrapper();

        //设置更新条件--in (ids).set(逻辑删除字段,1)
        luwDF.in(DishFlavor::getDishId, ids).set(DishFlavor::getIsDeleted, 1);

        //逻辑删除菜品口味
        dishFlavorService.update(luwDF);

        //逻辑删除菜品
        LambdaUpdateWrapper<Dish> luwD = new LambdaUpdateWrapper();

        //设置更新条件--in (ids).set(逻辑删除字段,1)
        luwD.in(Dish::getId, ids).set(Dish::getIsDeleted, 1);

        //逻辑删除菜品
        boolean flag = this.update(luwD);

        //添加提交过来的Flavor数据
        return flag;
    }

}
