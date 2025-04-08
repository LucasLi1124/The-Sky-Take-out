package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);

        Long dishId = dish.getId();

        List<DishFlavor> list = dishDTO.getFlavors();
        if (list != null && list.size ()> 0) {
            list.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);

            });
            dishFlavorMapper.insertBatch(list);

        }

    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            Dish dish = dishMapper.getStatus(id);
            if (Objects.equals(dish.getStatus(), StatusConstant.ENABLE)) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);

            }
        }
        List<Long> setmealId = setmealDishMapper.getBySetmealId(ids);
        if (setmealId != null && !setmealId.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDishId(id);
        }


    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getStatus(id);
        List<DishFlavor> list = dishFlavorMapper.getByIdWithFlavor(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(list);
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> list = dishDTO.getFlavors();
        if (list != null && !list.isEmpty()) {
            list.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());

            });
            dishFlavorMapper.insertBatch(list);

        }
    }

    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder().
                categoryId(categoryId).
                status(StatusConstant.ENABLE).
                build();
       return dishMapper.list(dish);
    }

    @Override
    @Transactional
    public void status(Integer status, Long id) {
        if(status == StatusConstant.DISABLE){
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);
            List<Long> setmeals = setmealDishMapper.getBySetmealId(dishIds);
            if (setmeals != null && setmeals.size() > 0){
                for (Long setmeal : setmeals){
                    Setmeal setmeal1 = Setmeal.builder().
                            id(setmeal).
                            status(StatusConstant.DISABLE).
                            build();
                    setmealMapper.update(setmeal1);
                }
            }
        }
        Dish dish = Dish.builder().
                id(id).
                status(status).
                build();
        dishMapper.update(dish);
    }


}
