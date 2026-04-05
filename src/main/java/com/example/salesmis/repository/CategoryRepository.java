package com.example.salesmis.repository;

import java.util.Optional;

import com.example.salesmis.model.entity.Category;
import jakarta.persistence.EntityManager;

public interface CategoryRepository extends GenericRepository<Category, Integer> {
    Optional<Category> findByName(EntityManager entityManager, String name);
}
