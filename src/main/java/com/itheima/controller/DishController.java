package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Category;
import com.itheima.entity.Dish;
import com.itheima.entity.DishDto;
import com.itheima.entity.DishFlavor;
import com.itheima.service.CategoryService;
import com.itheima.service.DishFlavorService;
import com.itheima.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
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

    //操作redis数据库工具对象
    @Autowired
    private RedisTemplate redisTemplate;



    /*网页端
     @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        //初始化条件查询器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        //设置条件
        lqw.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());


        //添加条件，查询状态为1（起售状态）的菜品
        lqw.eq(Dish::getStatus, 1);


        //设置查询条件排序, 升序,        更新时间降序
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);


        //查询
        List<Dish> list = dishService.list(lqw);


        //返回查询结果
        return R.success(list);
    }*/
    /*
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);



        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();


            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }*/

    /**
     * 根据条件查询响应的菜品数据
     * 适配网页端和移动端
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(@RequestParam Long categoryId) {

//        1). 改造DishController的list方法，先从Redis中获取分类对应的菜品数据，如果有则直接返回，无需查询数据库;
//            如果没有则查询数据库，并将查询到的菜品数据存入Redis。
//        2). 改造DishController的save和update方法，加入清理缓存的逻辑。


        //在list方法中,查询数据库之前,先查询缓存, 缓存中有数据, 直接返回

        List<DishDto> dishDtoList = null;
        //动态构造key  dish_菜品品种id_菜品状态 dish_1397844391040167938_1
        String key = "dish_" + categoryId + "_" + 1;

        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get("key");

        //判断缓存中是否有数据
        if (dishDtoList != null) {
            //如果存在值,直接返回,无需查询数据库
            return R.success(dishDtoList);
        }


        //初始化条件查询器
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        //设置条件(菜品种类id
        lqw.eq(categoryId != null, Dish::getCategoryId, categoryId);


        //因为逻辑删除所以添加了添加了条件
        lqw.eq(Dish::getIsDeleted, 0);

        //添加条件，查询状态为1（起售状态）的菜品
        lqw.eq(Dish::getStatus, 1);


        //设置查询条件排序, 升序,        更新时间降序
        lqw.orderByAsc(Dish::getSort).orderByDesc(Dish::getCreateTime);


        //查询
        List<Dish> list = dishService.list(lqw);

        //用dtoList存放list处理完之后的数据
        dishDtoList = list.stream().map((temp) -> {

            //dishDto对象用于接受数据
            DishDto dishDto = new DishDto();

            //拷贝
            BeanUtils.copyProperties(temp, dishDto);

            //获取菜品分类id
            Long id = temp.getCategoryId();

            //根据id查询分类的对象
            Category category = categoryService.getById(id);

            if (category != null) {
                //如果查询到的分类对象不是空
                String categoryName = category.getName();
                //给dishDto对象设置categoryName
                dishDto.setCategoryName(categoryName);
            }

            //获取当前菜品的id
            Long dishId = temp.getId();

            //设置条件查询器(查询菜品口味的
            LambdaQueryWrapper<DishFlavor> lqwDf = new LambdaQueryWrapper<>();

            //设置条件为菜品id--dishId
            lqwDf.eq(DishFlavor::getDishId, dishId);

            //查询到这个菜品id对应的所有口味集合
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavors = dishFlavorService.list(lqwDf);

            dishDto.setFlavors(dishFlavors);

            return dishDto;

        }).collect(Collectors.toList());


        //如果redis不存在，查询数据库，并将数据库查询结果，缓存在redis，并设置过期时间 在返回之前将数据存放到redis

        redisTemplate.opsForValue().set(key, dishDtoList, 60, TimeUnit.MINUTES);

        //返回查询结果
        return R.success(dishDtoList);
    }


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

        //方式一
        //清理所有菜品的缓存数据
//        Set keys = redisTemplate.keys("dish_*"); //获取所有以dish_xxx开头的key
//        redisTemplate.delete(keys); //删除这些key


//      //方式二
        //清理当前添加菜品分类下的缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);


        //新增菜品，同时插入菜品对应的口味数据
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");

    }


    /**
     * 菜品分页查询
     *
     * @return
     */
    /* @GetMapping("/page")
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
        List<DishDto> list = records.stream().map((item) -> {
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


    }*/
    @GetMapping("/page")
    public R<Map> getAll(int page, int pageSize, String name) {
        //记录日志
        log.info("page={},pageSize={},name={}", page, pageSize, name);

        //构造分页构造器
        IPage<Dish> dishPage = new Page<>(page, pageSize);
        ///对象拷贝分页构造器
        IPage<DishDto> dishDtoPage = new Page<>();

        //新建查询条件
        LambdaQueryWrapper<Dish> lqw = new LambdaQueryWrapper<>();

        //逻辑删除添加了查询条件
        lqw.eq(Dish::getIsDeleted, 0);

        //指定查询条件
        lqw.like(StringUtils.isNotEmpty(name), Dish::getName, name);

        //添加排序条件 降序
        lqw.orderByDesc(Dish::getUpdateTime);

        //查询
        // int count = dishService.count(lqw);
        dishService.page(dishPage, lqw);


        //4). 遍历分页查询列表数据，根据分类ID查询分类信息，从而获取该菜品的分类名称
        //对象拷贝
        // protected List<T> records; page中的集合属性 用于查询到的对象集合
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //单独取出dishPage中的list进行处理
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
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
        Map map = new HashMap<>();


        map.put("records", list);
        long total = dishPage.getTotal();
        int count = (int) total;
        map.put("total", count);


        //返回结果
        return R.success(map);


    }




    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }








    /**
     * 修改菜品(包括口味
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        //记录日志
        log.info("更新菜品:{}", dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        //清理所有菜品的缓存数据
        Set keys = redisTemplate.keys("dish_*"); //获取所有以dish_xxx开头的key
        redisTemplate.delete(keys); //删除这些key

        return R.success("修改菜品成功");

    }


    /**
     * 批量修改状态
     *
     * @param status
     * @param ids
     * @return R
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,
                                  @RequestParam List<Long> ids) {

        dishService.updateStatus(status, ids);

        return R.success("操作成功");
    }

    /**
     * 批量逻辑删除
     * 删除菜品/批量删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteIds(@RequestParam List<Long> ids) {

        dishService.deleteWithFlavor(ids);

        return R.success("删除成功");
    }

}
