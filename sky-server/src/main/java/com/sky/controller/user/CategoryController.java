package com.sky.controller.user;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Slf4j
@Api(tags = "C端分类相关接口")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        log.info("查询分类：{}", type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }


}
