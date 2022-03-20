package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.Employee;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface EmployeeService extends IService<Employee> {

}
