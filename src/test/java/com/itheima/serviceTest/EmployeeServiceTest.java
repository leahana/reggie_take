package com.itheima.serviceTest;

import com.itheima.common.BaseContext;
import com.itheima.entity.Dish;
import com.itheima.entity.Employee;
import com.itheima.entity.Setmeal;
import com.itheima.entity.SetmealDish;
import com.itheima.service.EmployeeService;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class EmployeeServiceTest {


    /*
    完善了元数据对象处理器之后，我们就可以重新启动项目，完成登录操作后,
    在员工管理模块中，
    测试增加/更新员工信息功能, 直接查询数据库数据变更，
    看看我们在新增/修改数据时，这些公共字段数据是否能够完成自动填充,
    并且看看填充的create_user 及 update_user字段值是不是本地登录用户的ID。
     */
    @Autowired
    private EmployeeService employeeService;

    @Test
    void testAdd() {
        Employee employee = new Employee();
        employee.setUsername("zhangsan123");
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        employee.setIdNumber("320122122222222222");
        employee.setStatus(0);
        employee.setPhone("18751909999");
        employee.setSex("女");
        employee.setName("张三");
        BaseContext.setCurrentId(1L);

        //  boolean save = employeeService.save(employee);


    }

    @Test
    void testUpdate() {

    }

    //尝试使用stream流peek处理
    @Test
    void testPeek(){
        List<SetmealDish> list=new ArrayList<>();
        SetmealDish s1 = new SetmealDish();
        SetmealDish s2 = new SetmealDish();
        SetmealDish s3 = new SetmealDish();
        s1.setName("S1");
        s2.setName("S2");
        s3.setName("S3");
        list.add(s1);
        list.add(s2);
        list.add(s3);
        System.out.println(list);
        list = list.stream().peek((item) -> {
            //setmeal_dish的SetmealId关联了套餐表主键id
            //获取list集合中每一个setmealDto对象的id属性
            //绑定菜品id
            item.setSetmealId(1L);
        }).collect(Collectors.toList());

        System.out.println(list);
    }

}
