package com.example.salesmis.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;

public abstract class AbstractJpaRepository<T, ID> implements GenericRepository<T, ID> {
    private final Class<T> entityClass;

    protected AbstractJpaRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public Optional<T> findById(EntityManager entityManager, ID id) {
        return Optional.ofNullable(entityManager.find(entityClass, id));
    }

    @Override
    public List<T> findAll(EntityManager entityManager) {
        return entityManager.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                .getResultList();
    }

    @Override
    public T save(EntityManager entityManager, T entity) {
        return entityManager.merge(entity);
    }

    @Override
    public void deleteById(EntityManager entityManager, ID id) {
        findById(entityManager, id).ifPresent(entityManager::remove);
    }
}
