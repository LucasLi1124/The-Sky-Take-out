package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    CategoryService categoryService;



    @PostMapping
    public Result<String> add(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}",categoryDTO);
        categoryService.add(categoryDTO);
        return Result.success();
    }
    @GetMapping("/page")
    public Result<PageResult> search(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询分类：{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.search(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result<String> deleteById(Long id){
        log.info("删除分类：{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @PutMapping
    public Result<String> update(@RequestBody CategoryDTO categoryDTO){
        log.info("修改分类：{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status,Long id){
        log.info("修改分类状态：{}",status);
        categoryService.status(status,id);
        return Result.success();
    }
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        log.info("查询分类：{}",type);
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
