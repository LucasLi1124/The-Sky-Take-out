package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

@Mapper
public interface CategoryMapper {

    @Insert(" INSERT INTO category (type, name, sort, status, create_time, update_time, create_user, update_user)" +
            "VALUES" + "(#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void insert(Category category);


    Page<Category> search(CategoryPageQueryDTO categoryPageQueryDTO);

    @Delete("DELETE from category where id = #{id}")
    void deleteById(Long id);

    void update(Category category);

    List<Category> list(Integer type);
}
