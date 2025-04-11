package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.entity.User;
import com.sky.mapper.AddressMapper;
import com.sky.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 新增地址
     *
     * @param addressBook
     */
    @Override
    public void addNewAddress(AddressBook addressBook) {
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressBook.setIsDefault(0);
        addressMapper.addNewAddress(addressBook);
    }


    /**
     * 条件查询地址
     *
     * @return
     */
    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        return addressMapper.list(addressBook);
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressMapper.getById(id);
        return addressBook;
    }

    /**
     * 根据id删除地址
     *
     * @param id
     */

    @Override
    public void deleteById(Long id) {
        addressMapper.deleteById(id);


    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     */

    @Override
    public void UpdateAddressById(AddressBook addressBook) {
        addressMapper.updateAddressById(addressBook);

    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     */

    @Transactional
    @Override
    public void SetDefultAddress(AddressBook addressBook) {
        //先将所有地址设置为不是默认地址
        addressBook.setIsDefault(0);
        Long userId = BaseContext.getCurrentId();
        addressBook.setUserId(userId);
        addressMapper.update(addressBook);

        // 根据用户的操作，将地址设置为默认地址
        addressBook.setIsDefault(1);
        addressMapper.updateAddressById(addressBook);

    }
}


