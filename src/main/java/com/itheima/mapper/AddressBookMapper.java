package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * 地址mapper
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
