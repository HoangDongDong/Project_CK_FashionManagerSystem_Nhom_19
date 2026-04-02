package com.example.salesmis.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

public interface GenericRepository<T, ID> {
    Optional<T> findById(EntityManager entityManager, ID id);
    List<T> findAll(EntityManager entityManager);
    T save(EntityManager entityManager, T entity);
    void deleteById(EntityManager entityManager, ID id);
}
