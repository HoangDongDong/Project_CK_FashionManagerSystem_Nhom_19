package com.example.salesmis.repository.impl;

import java.util.Optional;

import com.example.salesmis.model.entity.Category;
import com.example.salesmis.repository.AbstractJpaRepository;
import com.example.salesmis.repository.CategoryRepository;

import jakarta.persistence.EntityManager;

public class CategoryRepositoryImpl extends AbstractJpaRepository<Category, Integer> implements CategoryRepository {
    
    public CategoryRepositoryImpl() {
        super(Category.class);
    }

    @Override
    public Optional<Category> findByName(EntityManager entityManager, String name) {
        return entityManager.createQuery("SELECT c FROM Category c WHERE c.categoryName = :name", Category.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }
}
