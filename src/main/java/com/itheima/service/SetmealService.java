package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);


    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
     void removeWithDish(List<Long> ids);


    /**
     * 修改套餐,同时需要修改套餐和菜品的关联数据
     */
    void updateWithDish(SetmealDto setmealDto);

    /**
     * 查询套餐,同时需要查询套餐和菜品关联数据
     * @param id
     * @return
     */
    SetmealDto getWithDish(Long id);
}
