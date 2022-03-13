package com.itheima.serviceTest;


import com.itheima.common.BaseContext;
import com.itheima.entity.Category;
import com.itheima.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class CategoryServiceTest {

/*
新增分类的代码编写完毕之后, 我们需要重新启动项目，进入管理系统访问分类管理,
然后进行新增分类测试，需要将所有情况都覆盖全，例如：
1). 输入的分类名称不存在
2). 输入已存在的分类名称
3). 新增菜品分类
4). 新增套餐分类*/

    @Autowired
    private CategoryService categoryService;

    @Test
    void testAdd() {
        Category category = new Category();
        category.setType(1);
        //Duplicate entry '小菜' for key 'idx_category_name'
        category.setName("小菜");
        category.setSort(22);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        BaseContext.setCurrentId(1L);
        System.out.println(category);
       // categoryService.save(category);
    }
}
