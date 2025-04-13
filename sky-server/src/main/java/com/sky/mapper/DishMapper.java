package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

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


    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);



    List<Dish> list(Dish dish);

    List<Dish> getBySetmealId(Long id);


    @Select("SELECT * FROM dish left join setmeal_dish on setmeal_dish.dish_id = dish.id where setmeal_id  = #{id}")
    List<DishItemVO> getByIdWithSetmealDish(Long setmealId);


    @Select("SELECT * from dish where dish.id = category_id ")
    List<Dish> searchByCateforyId(Long categoryId);

    /**
     * 查询菜品总览
     *
     * @return
     */
    Integer countByMap(Map map);
}
