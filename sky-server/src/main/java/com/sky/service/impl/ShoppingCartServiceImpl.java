package com.sky.service.impl;


import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper DishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //判断当前加入购物车的商品是否已经存在了
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long UserId = BaseContext.getCurrentId();
        shoppingCart.setUserId(UserId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);

        //如果存在了，只需要将数量加一
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);

            shoppingCartMapper.updateNumberbyId(cart);
        } else {
            //如果不存在，需要插入一条购物车数据
            //判断本次添加的是菜品还是套餐
            Long dishId = shoppingCartDTO.getDishId();
            if (dishId != null) {
                //本次添加到购物车的是菜品
                Dish dish = DishMapper.getStatus(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
            } else {
                //本次添加到购物车的是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                Setmeal setmeal = setmealMapper.getInfoById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.insert(shoppingCart);
        }


    }

    /**
     * 查看购物车
     *
     * @return
     */
    @Override
    public List<ShoppingCart> show() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.
                builder().
                userId(userId).
                build();
        List<ShoppingCart> shoppingCartsList = shoppingCartMapper.list(shoppingCart);
        return shoppingCartsList;


    }

    /**
     * 清空购物车
     */
    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.deleteByIds(userId);


    }

    /**
     * 减少购物车
     *
     * @param shoppingCartDTO
     */
    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 根据用户id和菜品id或者套餐id查询
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long UserId = BaseContext.getCurrentId();
        shoppingCart.setUserId(UserId);
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        //判断是否有该菜品或套餐
        if (list != null && list.size() > 0) {
            shoppingCart = list.get(0);
            //判断菜品或套餐的数量
            Integer number = shoppingCart.getNumber();
            //如果数量为1，则删除该菜品或套餐
            if (number == 1) {
                shoppingCartMapper.deleteById(shoppingCart.getId());
                //如果数量大于1，则减少数量
            } else {
                shoppingCart.setNumber(shoppingCart.getNumber() - 1);
                shoppingCartMapper.updateNumberbyId(shoppingCart);
            }
        }

    }


}
