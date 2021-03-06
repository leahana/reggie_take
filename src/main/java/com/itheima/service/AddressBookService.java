package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.entity.AddressBook;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AddressBookService extends IService<AddressBook> {
}
