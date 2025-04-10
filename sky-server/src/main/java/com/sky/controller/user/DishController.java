package com.sky.controller.user;


import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;



    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("查询菜品分类：{}",categoryId);
        //构造redis中的key，规则：dish_分类id
        String key = "dish_" + categoryId;

        //查询redis是否存在菜品数据
        List<DishVO> dishVOList = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (dishVOList != null && dishVOList.size() > 0){
            //如果存在，直接返回，无须查询数据库
            return Result.success(dishVOList);
        }


        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE);//查询起售菜品
        //如果不存在，查询数据库，将查询到的菜品数据保存到redis中
        dishVOList = dishService.searchByDish(dish);
        redisTemplate.opsForValue().set(key,dishVOList);

        return Result.success(dishVOList);
    }



}
