package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.common.R;
import com.itheima.entity.Employee;
import com.itheima.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;



@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /*
处理逻辑如下：
①. 将页面提交的密码password进行md5加密处理, 得到加密后的字符串
②. 根据页面提交的用户名username查询数据库中员工数据信息
③. 如果没有查询到, 则返回登录失败结果
④. 密码比对，如果不一致, 则返回登录失败结果
⑤. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果
⑥. 登录成功，将员工id存入Session, 并返回登录成功结果
 */

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        System.out.println(employee);

        //1 将前端提交的代码进行md5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2 根据页面提交的用户名查询数据
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        //2.1设定查询条件(根据用户名查询)
        lqw.eq(Employee::getUsername, employee.getUsername());

        //条件查询,因为Service继承了mybatisPlus中的IService,所以简单的service也不用写了
        //所以说..只能把这些判断逻辑放在...controller.
        Employee one = employeeService.getOne(lqw);


        //3 如果没有查询到则返回登录失败结果  R类中静态方法error 返回了R对象 直接调用
        if (one == null) return R.error("用户不存在");

        //4 查询到数据就不会执行上面的if这里直接判断password, !false=true
        if (!one.getPassword().equals(password)) return R.error("密码错误");

        //5 满足以上if后继续判断员工账号是否处于封禁
        if (one.getStatus() == 0) return R.error("账号封禁中");

        //6 千辛万苦 登录成功 存入Session,返回登录成功结果
        request.getSession().setAttribute("employee", one.getId());

        return R.success(one);
    }




    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }




    /**
     * 新增员工
     *
     * @param
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save( @RequestBody Employee employee) {

        //记录日志
        log.info("新增员工,员工信息:{}", employee.toString());

        //设置初始密码 为123456 并且进行加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
 /*       employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //获得当前用户登录的id(long)
        long empId = (long) request.getSession().getAttribute("employee");

        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
*/
        employeeService.save(employee);

        return R.success("添加员工成功");


    }




    /**
     * 员工信息分页查询
     *
     * @param page     当前查询页码
     * @param pageSize 每页展示记录
     * @param name     员工姓名-可选参数
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //记录日志
        log.info("page={},pageSize={},name={}", page, pageSize, name);

        //构造分页构造器
        Page pageinfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lqw = new LambdaQueryWrapper<>();

        //添加过滤条件
        lqw.like(StringUtils.isNotEmpty(name), Employee::getName, name);

        //添加排序条件 降序
        lqw.orderByDesc(Employee::getUpdateTime);

        //查询
        employeeService.page(pageinfo, lqw);
        return R.success(pageinfo);

    }




    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee) {
        //记录日志
        log.info(employee.toString());
        // long empId = (long) request.getSession().getAttribute("employee");


        long id = Thread.currentThread().getId();
        log.info("EmployeeController中的Update" + "线程id为：{}", id);

        //更新信息
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        //log.warn("准备返回R.success");

        return R.success("员工信息修改成功");
    }




    /**
     * 根据id查询员工信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable long id) {
        log.info("根据id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if (employee != null) return R.success(employee);
        return R.error("没有查询到对应的员工信息");
    }
}