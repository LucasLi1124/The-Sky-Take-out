package com.sky.mapper;


import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {


    List<Long> getBySetmealId(List<Long> ids);



    void insertBatch(List<SetmealDish> setmealDishes);


    @Select("Select * From setmeal_dish where setmeal_id = #{setmealId} ")
    List<SetmealDish> getBySetmealld(Long id);


    @Delete("DELETE from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteByIds(Long id);



}
