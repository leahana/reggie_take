package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.DishFlavor;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface DishFlavorService extends IService<DishFlavor> {
}
