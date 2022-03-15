package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDto;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import javax.rmi.CORBA.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 套餐管理
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    //套餐service
    @Autowired
    private SetmealService setmealService;

    //套餐菜品service
    @Autowired
    private SetmealDishService setmealDishService;


    //分类service
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        //记录日志
        log.info("套餐信息:{}", setmealDto);

        setmealService.saveWithDIsh(setmealDto);

        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //初始化分页构造器对象
        Page<Setmeal> iPage = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        //初始化条件查询器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        //添加模糊条件根据name模糊查询
        lqw.like(name != null, Setmeal::getName, name);

        //添加分页排序条件 根据更新时间降序
        lqw.orderByDesc(Setmeal::getUpdateTime);

        setmealService.page(iPage, lqw);

        //对象拷贝 排除records属性, records属性单独处理
        BeanUtils.copyProperties(iPage, dtoPage, "records");
        List<Setmeal> records = iPage.getRecords();

        List<SetmealDto> collect = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(item, setmealDto);

            //分类id
            Long id = item.getCategoryId();

            //根据分类id查询分类对象
            Category category = categoryService.getById(id);

            //判断查询的返回值是否为空
            if (category != null) {
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        //封装处理完的collect集合
        dtoPage.setRecords(collect);


        return R.success(dtoPage);
    }


    /**
     * 批量删除
     * @param ids

     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        //记录日志
        log.info("准备删除 id:{}",ids);

        setmealService.removeWithDish(ids);

        return R.success("套餐数据删除成功");
    }

}