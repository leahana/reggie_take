package com.itheima.controller;

import com.itheima.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.*;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    //菜品类型service
    @Autowired
    private DishService dishService;

    //菜品口味service
    @Autowired
    private DishFlavorService dishFlavorService;

    //分类service
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        //记录日志
        log.info("新增菜品:{}", dishDto.toString());

        //新增菜品，同时插入菜品对应的口味数据
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");

    }

    /**
     * 菜品分页查询
     *
     * @return
     */
    @GetMapping("/page")
    public R<Page> getAll(int page, int pageSize, String name) {
        //记录日志
        log.info("page={},pageSize={},name={}", page, pageSize, name);


        //构造分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);
        ///对象拷贝分页构造器
        Page<DishDto> dishDtoPage = new Page<>();

        //新建查询条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        //指定查询条件
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);

        //添加排序条件 降序
        lqw.orderByDesc(Dish::getUpdateTime);

        //查询
        dishService.page(dishPage, lqw);


        //4). 遍历分页查询列表数据，根据分类ID查询分类信息，从而获取该菜品的分类名称
        //对象拷贝
        // protected List<T> records; page中的集合属性 用于查询到的对象集合
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //单独取出dishPage中的list进行处理
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records
                .stream()
                .map((item) -> {
                    //新建DishDto用于封装categoryName属性
                    DishDto dishDto = new DishDto();
                    Long id = item.getCategoryId();//分类id
                    //根据id查询分类对象
                    Category category = categoryService.getById(id);
                    String categoryName = category.getName();

                    dishDto.setCategoryName(categoryName);

                    //拷贝其他属性
                    BeanUtils.copyProperties(item, dishDto);

                    //返回dishDto对象
                    return dishDto;

                    //收集dishDto对象封装成集合
                }).collect(Collectors.toList());
        //将处理完的集合封装到dishDtoPage中
        dishDtoPage.setRecords(list);

        //返回结果
        return R.success(dishDtoPage);
    }


}
