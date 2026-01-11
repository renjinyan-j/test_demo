package org.example.finalproject.demos.service.impl;

import org.example.finalproject.demos.mapper.CategoriesMapper;
import org.example.finalproject.demos.pojo.Category;
import org.example.finalproject.demos.service.CategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CategoriesServiceImpl implements CategoriesService {

    @Autowired
    private CategoriesMapper categoriesMapper;

    @Override
    public List<Category> getAllCategories() {
        return categoriesMapper.getAllCategories();
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoriesMapper.getCategoryById(id);
    }
}
