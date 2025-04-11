package com.sky.mapper;


import com.sky.entity.AddressBook;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Mapper
public interface AddressMapper {


    /**
     * 添加新地址
     *
     * @param addressBook
     */
    void addNewAddress(AddressBook addressBook);


    /**
     * 条件查询
     * @param addressBook
     * @return
     */
    List<AddressBook> list(AddressBook addressBook);

    /**
     * 根据id查询地址
     * @param id
     * @return
     */
    @Select("select * from address_book where id = #{id}")
    AddressBook getById(Long id);

    /**
     * 根据id删除地址
     * @param id
     */
    @Delete("DELETE from address_book where id = #{id}")
    void deleteById(Long id);

    /**
     * 根据id修改地址
     * @param addressBook
     */
    void updateAddressById(AddressBook addressBook);


    /**
     * 设置默认地址
     * @param addressBook
     */
    @Update("update address_book set is_default = #{isDefault} where user_id = #{userId} ")
    void update(AddressBook addressBook);


}
