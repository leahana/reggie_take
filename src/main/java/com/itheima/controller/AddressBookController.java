package com.itheima.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.entity.AddressBook;
import com.itheima.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {


    /*
- 新增地址
- 地址列表查询
- 设置默认地址
- 编辑地址
- 删除地址
     */
    @Autowired
    private AddressBookService addressBookService;


    /**
     * 新增地址
     */
    @PostMapping
    public R<AddressBook> saveAddressBook(@RequestBody AddressBook addressBook) {
        //返回当前线程所对应的线程局部变量的值,过滤器中设置set过了
        addressBook.setUserId(BaseContext.getCurrentId());

        //记录日志
        log.info("addressBook:{}", addressBook);

        //调用service中save
        addressBookService.save(addressBook);

        //返回结果
        return R.success(addressBook);
    }


    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefaultAddressBook(@RequestBody AddressBook addressBook) {
        //记录日志
        log.info("addressBook:{}", addressBook);

        //设置更新条件器
        LambdaUpdateWrapper<AddressBook> luw = new LambdaUpdateWrapper<>();

        //用户id
        //设置更新条件user_id=线程中取出的 这个对象的user_id
        luw.eq(AddressBook::getUserId, BaseContext.getCurrentId());

        //设置默认地址条件为否
        luw.set(AddressBook::getIsDefault, 0);

        //把该用户的其他地址设置为普通地址
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(luw);


        //设置为默认地址
        addressBook.setIsDefault(1);

        //主键id
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);


        return R.success(addressBook);

    }


    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R queryAddressBook(@PathVariable Long id) {
        //根据id查询
        AddressBook addressBook = addressBookService.getById(id);

        //判断查询结果
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }

    }


    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> queryDefaultAddressBook() {

        //条件查询器
        LambdaUpdateWrapper<AddressBook> lqw = new LambdaUpdateWrapper<>();

        //设置查询条件(用户id
        lqw.eq(AddressBook::getUserId, BaseContext.getCurrentId());

        //设置查询条件(地址状态 为1的是默认地址
        lqw.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(lqw);

        //根据查询结果返回结果
        if (null != addressBook) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }

    }


    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> queryAddressBooks(AddressBook addressBook) {
        //===========================================
        //根据从线程局部对象中取出id,给userid进行设置

        //查询这个userId下 的所有地址
        addressBook.setUserId(BaseContext.getCurrentId());

        //记录日志
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> lqw = new LambdaQueryWrapper<>();

        //设置查询条件,查询条件满足这个用户id的
        lqw.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());

        //设置查询条件 根据更新时间降序
        lqw.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        //查询
        List<AddressBook> list = addressBookService.list(lqw);

        //返回结果
        return R.success(list);
    }


    @DeleteMapping
    public R<String> deleteAddressBooks(@RequestParam List<Long> ids) {

        boolean flag = addressBookService.removeByIds(ids);
        if (flag) {
            return R.success("删除成功");
        } else {
            return R.error("请稍后再试");
        }
    }
}
