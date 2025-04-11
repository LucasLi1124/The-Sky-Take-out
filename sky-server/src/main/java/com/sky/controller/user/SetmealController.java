package com.sky.controller.user;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.ResolutionSyntax;
import java.util.List;

@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Slf4j
@Api(tags = "C端套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @GetMapping("/list")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")
    public Result<List<Setmeal>> list(Long categoryId) {
        log.info("根据分类id查询套餐：{}", categoryId);
        List<Setmeal> list = setmealService.searchByCateforyId(categoryId);
        return Result.success(list);
    }

    @GetMapping("/dish/{id}")
    public Result getByIdWithFlavor(@PathVariable Long id) {
        log.info("根据套餐id查询套餐：{}", id);
        List <DishItemVO> dishItemVO = setmealService.getByIdWithSetmealDish(id);
        return Result.success(dishItemVO);
    }

}
