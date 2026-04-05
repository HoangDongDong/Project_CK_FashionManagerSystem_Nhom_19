package com.example.salesmis.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.CategoryDTO;
import com.example.salesmis.model.entity.Category;
import com.example.salesmis.repository.CategoryRepository;
import com.example.salesmis.repository.ProductRepository;
import com.example.salesmis.service.CategoryService;
import com.example.salesmis.service.exception.CategoryNotEmptyException;
import com.example.salesmis.service.exception.DuplicateCategoryException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final EntityManagerProvider entityManagerProvider;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ProductRepository productRepository, EntityManagerProvider entityManagerProvider) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        EntityManager em = entityManagerProvider.createEntityManager();
        try {
            return categoryRepository.findAll(em).stream()
                    .map(c -> new CategoryDTO(c.getCategoryId(), c.getCategoryName(), c.getDescription()))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    @Override
    public void addCategory(CategoryDTO dto) {
        EntityManager em = entityManagerProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Optional<Category> opt = categoryRepository.findByName(em, dto.getCategoryName());
            if (opt.isPresent()) {
                throw new DuplicateCategoryException("Tên danh mục đã tồn tại!");
            }

            Category cat = new Category();
            cat.setCategoryName(dto.getCategoryName());
            cat.setDescription(dto.getDescription());
            categoryRepository.save(em, cat);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void editCategory(Integer id, CategoryDTO dto) {
        EntityManager em = entityManagerProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Category existing = categoryRepository.findById(em, id)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy danh mục!"));

            Optional<Category> byName = categoryRepository.findByName(em, dto.getCategoryName());
            if (byName.isPresent() && !byName.get().getCategoryId().equals(id)) {
                throw new DuplicateCategoryException("Tên danh mục cập nhật bị trùng với danh mục khác!");
            }

            existing.setCategoryName(dto.getCategoryName());
            existing.setDescription(dto.getDescription());
            categoryRepository.save(em, existing);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteCategory(Integer id) {
        EntityManager em = entityManagerProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            long productCount = productRepository.countByCategoryId(em, id);
            if (productCount > 0) {
                throw new CategoryNotEmptyException("Danh mục còn sản phẩm, hãy chuyển SP trước khi xóa!");
            }
            
            categoryRepository.deleteById(em, id);
            
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
