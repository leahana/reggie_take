package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);


    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     *
     * @param ids
     */
    void removeWithDish(List<Long> ids);


    /**
     * 修改套餐,同时需要修改套餐和菜品的关联数据
     */
    boolean updateWithDish(SetmealDto setmealDto);


    /**
     * 修改套餐,同时需要修改套餐和菜品的关联数据v2
     */
    boolean updateWithDishV2(SetmealDto setmealDto);


    /**
     * 查询套餐,同时需要查询套餐和菜品关联数据
     *
     * @param id
     * @return
     */
    SetmealDto queryWithDish(Long id);


    boolean updateStatusByIds(Integer status, List<Long> ids);
}
