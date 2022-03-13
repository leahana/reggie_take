package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.entity.Employee;
import com.itheima.mapper.EmployeeMapper;
import com.itheima.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

/*
处理逻辑如下：

①. 将页面提交的密码password进行md5加密处理, 得到加密后的字符串

②. 根据页面提交的用户名username查询数据库中员工数据信息

③. 如果没有查询到, 则返回登录失败结果

④. 密码比对，如果不一致, 则返回登录失败结果

⑤. 查看员工状态，如果为已禁用状态，则返回员工已禁用结果

⑥. 登录成功，将员工id存入Session, 并返回登录成功结果


 */

}
