package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void insertBatch(List<DishFlavor> list);


    @Delete("DELETE FROM dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long id);

    @Select("Select * FROM dish_flavor where dish_id = #{id}")
    List<DishFlavor> getByIdWithFlavor(Long id);
}
