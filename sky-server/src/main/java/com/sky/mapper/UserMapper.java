package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;

@Mapper
public interface  UserMapper {

    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("SELECT * from user where openid = #{openid}")
    User getByOpenId (String openid);






    void insert(User user);
}

