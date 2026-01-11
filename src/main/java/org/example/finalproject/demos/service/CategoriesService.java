package org.example.finalproject.demos.service;

import org.example.finalproject.demos.pojo.Category;

import java.util.List;

public interface CategoriesService {
    List<Category> getAllCategories();

    Category getCategoryById(Integer id);
}
