package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Category;
import com.itheima.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {


    @Autowired
    CategoryService categoryService;

    /**
     * 新增分类
     *
     * @param category
     * @return R<String>
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {
        //记录日志
        log.info("category:{}", category);

        categoryService.save(category);

        return R.success("新增分类成功");

    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        //分页构造器
        Page<Category> iPage = new Page<>(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        //根据排序条件, 根据sort 排序 升序
        lqw.orderByAsc(Category::getSort);

        //分页查询
        categoryService.page(iPage, lqw);

        return R.success(iPage);

    }


    /**
     * 根据id删除
     *
     * @param id
     * @return R<String>
     */
    @DeleteMapping
    public R<String> deleteById(Long id) {
        //记录日志
        log.info("删除分类,id为:{}", id);

        //categoryService.removeById(id);
        //改造方法,使用Service中重写的remove方法
        categoryService.remove(id);

        return R.success("分类删除成功");
    }


    /**
     * 修改信息
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        //记录日志
        log.info("修改分类信息:{}", category);

        categoryService.updateById(category);

        return R.success("分类信息修改成功");
    }


    /**
     * 根据条件查询分类数据
     *
     * @param
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> getList(Integer type) {
        //条件构造器
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();

        //添加条件
        //三个参数,第一个判断是否为空 在进行正常的条件查询
        lqw.eq(type != null, Category::getType, type);

        //添加排序条件
        //根据sort升序,再根据更新时间降序
        lqw.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        //查询集合(条件)
        List<Category> list = categoryService.list(lqw);

        //System.out.println(list);

        return R.success(list);
    }
}
