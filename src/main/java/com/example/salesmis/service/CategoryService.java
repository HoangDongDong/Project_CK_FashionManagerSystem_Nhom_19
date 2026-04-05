package com.example.salesmis.service;

import java.util.List;
import com.example.salesmis.model.dto.CategoryDTO;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    void addCategory(CategoryDTO dto);
    void editCategory(Integer id, CategoryDTO dto);
    void deleteCategory(Integer id);
}
