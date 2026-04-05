package com.example.salesmis.repository;

import java.util.List;

import com.example.salesmis.model.entity.OrderEntity;

import jakarta.persistence.EntityManager;

public interface InvoiceRepository extends GenericRepository<OrderEntity, Integer> {
    List<OrderEntity> findRecentInvoices(EntityManager entityManager, int limit);
    List<OrderEntity> findOrdersInPeriod(EntityManager entityManager, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, Integer staffId);
    Object[] getRevenueAndOrderCount(EntityManager entityManager, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
    List<Object[]> getRevenueChartData(EntityManager entityManager, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}
