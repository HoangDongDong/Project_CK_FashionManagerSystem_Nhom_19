package com.example.salesmis.repository.impl;

import java.util.List;

import com.example.salesmis.model.entity.OrderEntity;
import com.example.salesmis.repository.AbstractJpaRepository;
import com.example.salesmis.repository.InvoiceRepository;

import jakarta.persistence.EntityManager;

public class InvoiceRepositoryImpl extends AbstractJpaRepository<OrderEntity, Integer> implements InvoiceRepository {
    public InvoiceRepositoryImpl() {
        super(OrderEntity.class);
    }

    @Override
    public List<OrderEntity> findRecentInvoices(EntityManager entityManager, int limit) {
        int normalizedLimit = Math.max(1, limit);
        return entityManager.createQuery("SELECT o FROM OrderEntity o ORDER BY o.orderDate DESC", OrderEntity.class)
                .setMaxResults(normalizedLimit)
                .getResultList();
    }
}
