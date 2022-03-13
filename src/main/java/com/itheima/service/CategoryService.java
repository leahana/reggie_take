package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Category;


public interface CategoryService extends IService<Category> {

    /**
     * 根据id删除分类的扩展方法
     * @param id
     */
    public void remove(Long id);
}
