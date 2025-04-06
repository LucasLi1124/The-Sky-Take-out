package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {


    @Select("select count(id) from dish where category_id = #{id}")
    Integer countByCategoryId(Long categoryId);

    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish getStatus(Long id);


    @Delete("DELETE from dish where id = #{id}")
    void deleteById(Long id);

    @Select("SELECT * FROM dish Where id = #{id}")
    DishVO getByIdWithFlavor(Long id);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);



    List<Dish> list(Dish dish);
}
