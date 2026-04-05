package com.example.salesmis.controller;

import java.util.List;

import com.example.salesmis.model.dto.CategoryDTO;
import com.example.salesmis.service.CategoryService;
import com.example.salesmis.service.exception.CategoryNotEmptyException;
import com.example.salesmis.service.exception.DuplicateCategoryException;

public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }

    public void createCategory(CategoryDTO dto) throws DuplicateCategoryException, Exception {
        categoryService.addCategory(dto);
    }

    public void updateCategory(Integer id, CategoryDTO dto) throws DuplicateCategoryException, Exception {
        categoryService.editCategory(id, dto);
    }

    public void deleteCategory(Integer id) throws CategoryNotEmptyException, Exception {
        categoryService.deleteCategory(id);
    }
}
