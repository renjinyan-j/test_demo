package org.example.finalproject.demos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.finalproject.demos.pojo.Category;

import java.util.List;

@Mapper
public interface CategoriesMapper {
    // 查询所有分类
    List<Category> getAllCategories();

    // 根据ID查询分类
    Category getCategoryById(Integer id);
}
