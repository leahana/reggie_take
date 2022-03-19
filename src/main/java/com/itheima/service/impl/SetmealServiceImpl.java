package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.common.CustomException;
import com.itheima.entity.Category;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.entity.SetmealDto;
import com.itheima.mapper.SetmealMapper;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private DishService dishService;


    private SetmealDto setmealDto;

    /**
     * 添加 同时添加菜品和套餐
     *
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal库
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
//======================================================================================//
        setmealDishes = setmealDishes.stream().peek((item) -> {
            //setmeal_dish的SetmealId关联了套餐表主键id
            //获取list集合中每一个setmealDto对象的id属性
            //绑定菜品id

            item.setSetmealId(setmealDto.getId());

            // return item;

        }).collect(Collectors.toList());

        //保存套餐和菜品的关联信息，操作setmeal_dish,执行insert操作
        setmealDishService.saveBatch(setmealDishes);

    }


    /**
     * 删除 同时删除套餐和菜品关联数据
     *
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
        //查询套餐状态 确定是否可以删除
        //初始化条件查询器
        LambdaQueryWrapper<Setmeal> lqw = new LambdaQueryWrapper<>();

        //添加id条件
        lqw.in(Setmeal::getId, ids);

        //添加状态条件
        lqw.eq(Setmeal::getStatus, 1);

        //统计计数
        int count = this.count(lqw);

        //如果count>0 说明有菜品关联 不能删除 抛出异常
        if (count > 0) {
            throw new CustomException("套餐正在售卖中不能删除");
        }
        //如果可以删除,先删除表 setmeal 中的数据

        this.removeByIds(ids);

        //新建条件查询器准备删除SetmealDish数据
        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();

        //添加条件reggie
        qw.in(SetmealDish::getId, ids);

        //删除关系 setmeal_dish表中的数据
        setmealDishService.remove(qw);
    }


    /**
     * 根据id获取setmealDto
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto getWithDish(Long id) {

        //setmealid
        //本类getById(从ServiceImpl继承来的
        Setmeal setmeal = this.getById(id);

        //初始化条件查询器
        LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();

        //添加id条件
        lqw.eq(SetmealDish::getId, id);

        //套餐包含的菜品
        List<SetmealDish> list = setmealDishService.list(lqw);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        setmealDto.setSetmealDishes(list);
        Long categoryId = setmeal.getCategoryId();

        Category byId = categoryService.getById(categoryId);

        setmealDto.setCategoryName(byId.getName());

        this.setmealDto = setmealDto;

        return setmealDto;
    }


//  {id=1503791759585292290,
//  categoryId=1413342269393674242,
//  name=商务套餐,
//  price=12100,
//  status=1,
//  code=,
//  description=111,
//  image=0d5c0e2b-b0d0-455c-b6d7-92034d9221d5.png,
//  createTime=2022-03-16 01:54:29,
//  createUser=1,
//  updateUser=1,
//  categoryName=商务套餐,
//  setmealDishes=[{copies=1, dishId=1397851668262465537, name=口味蛇, price=16800},
//  {copies=1, dishId=1397851370462687234, name=邵阳猪血丸子, price=13800}],
//  idType=1413342269393674242}

    @Override
    public boolean updateWithDish(SetmealDto setmealDto) {
    /*
SetmealDto(


SetmealDishes=[SetmealDish(id=null,setmealId=null,dishId=1397851668262465537,name=口味蛇,price=16800,
copies=1,
sort=null,
createTime=null, updateTime=null, createUser=null,updateUser=null, isDeleted=null)],




 categoryName=商务套餐)
 */
        Long setmealId = this.setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        //添加条件
        qw.eq(SetmealDish::getId, setmealId);

        //删除关系 setmeal_dish表中关联setmeal表主键的菜品

        setmealDishService.remove(qw);


        this.removeById(setmealId);

        BeanUtils.copyProperties(setmealDto, this.setmealDto);


        this.saveWithDish(this.setmealDto);


        //从map中获取setmeal的id
//        String _setmealId = (String) map.get("id");
//        Long setmealId = Long.valueOf(_setmealId);
        //新建条件查询器准备删除SetmealDish数据
//        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();

        //添加条件reggie
//        qw.eq(SetmealDish::getId, setmealId);

        //删除关系 setmeal_dish表中关联setmeal表主键的菜品
        //setmealDishService.remove(qw);

        //根据setmealId删除setmeal表的数据
        //this.removeById(setmealId);


        //封装SetmealDto准备调用本类的save方法
        //SetmealDto属性 setmeal属性+SetmealDishes集合+categoryName
//        SetmealDto setmealDto = new SetmealDto();

        //从map中获取setmealDishes,这里的setmealDish{copies=1, dishId=1397851370462687234, name=邵阳猪血丸子, price=13800}
        //除了自动填充的,还有setmealId为空
        //ArrayList<SetmealDish> list = new ArrayList<>();

//        ArrayList list = (ArrayList) map.get("setmealDishes");




      /*  Long categoryId = (Long) map.get("categoryId");
        String name = (String) map.get("name");
        BigDecimal price = (BigDecimal) map.get("price");
        String description = (String) map.get("description");
        String image = (String) map.get("image");
        String categoryName = (String) map.get("categoryName");

        setmealDto.setCategoryId(categoryId);
        setmealDto.setName(name);
        setmealDto.setPrice(price);
        setmealDto.setDescription(description);
        setmealDto.setImage(image);
        setmealDto.setCategoryName(categoryName);
        setmealDto.setSetmealDishes(setmealDishes);

        this.saveWithDish(setmealDto);
*/

        //log.error(setmealDishes.toString() + "..." + setmealDishes.getClass().toString());
        //遍历list


        //List<SetmealDish> dishes = HAUtils.objToList(setmealDishes, SetmealDish.class);

        //log.error(dishes.toString());



     /*   //先删除当前套餐下的菜品数据setmeal_dish表中关联的菜品


        List<SetmealDish> setmealDishes = (List<SetmealDish>) map.get("setmealDishes");
        log.error(setmealDishes.toString());


        setmealDishes.stream().peek((temp) -> temp.setSetmealId((Long) map.get("id")));

*/


      /*  LambdaQueryWrapper<SetmealDish> lqw = new LambdaQueryWrapper<>();

        lqw.eq(SetmealDish::getSetmealId, map.get("id"));

        setmealDishService.remove(lqw);

        setmealDishService.saveBatch(setmealDishes);*/
        //初始化条件查询器

       /* LambdaUpdateWrapper<Setmeal> luw = new LambdaUpdateWrapper<>();
        luw.eq(Setmeal::getId, map.get("id"))
                .set(Setmeal::getCategoryId, map.get("categoryId"))
                .set(Setmeal::getName, map.get("name"))
                .set(Setmeal::getPrice, map.get("price"))
                .set(Setmeal::getStatus, map.get("status"))
                .set(Setmeal::getDescription, map.get("description"))
                .set(Setmeal::getImage, map.get("image"));

        boolean update = this.update(luw);
*/


        return true;
    }


    @Override
    public boolean updateWithDishV2(SetmealDto setmealDto) {



        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> qw = new LambdaQueryWrapper<>();
        //添加条件
        qw.eq(SetmealDish::getSetmealId, setmealId);

        //删除关系 setmeal_dish表中关联setmeal表主键的菜品
        setmealDishService.remove(qw);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        log.error(setmealId.toString());
        log.error(setmealDishes.toString());
        setmealDishes = setmealDishes.stream().peek((emp) -> emp.setSetmealId(setmealId)).collect(Collectors.toList());
        log.error(setmealDishes.toString());

        setmealDishService.saveBatch(setmealDishes);


        LambdaUpdateWrapper<Setmeal> luwS = new LambdaUpdateWrapper<>();

        luwS.eq(Setmeal::getId, setmealId);

        this.update(setmealDto, luwS);

        return true;

    }
}
