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

    @Override
    public List<OrderEntity> findOrdersInPeriod(EntityManager entityManager, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate, Integer staffId) {
        String jpql = "SELECT o FROM OrderEntity o WHERE o.orderDate BETWEEN :startDate AND :endDate";
        if (staffId != null) {
            jpql += " AND o.staff.staffId = :staffId";
        }
        jpql += " ORDER BY o.orderDate ASC";

        jakarta.persistence.TypedQuery<OrderEntity> query = entityManager.createQuery(jpql, OrderEntity.class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate);
        
        if (staffId != null) {
            query.setParameter("staffId", staffId);
        }
        return query.getResultList();
    }
    @Override
    public Object[] getRevenueAndOrderCount(EntityManager entityManager, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        String jpql = "SELECT SUM(o.finalTotal), COUNT(o) FROM OrderEntity o WHERE o.status = 'COMPLETED'";
        if (startDate != null && endDate != null) {
            jpql += " AND o.orderDate >= :start AND o.orderDate <= :end";
        }
        
        jakarta.persistence.TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        if (startDate != null && endDate != null) {
            query.setParameter("start", startDate);
            query.setParameter("end", endDate);
        }
        return query.getResultList().isEmpty() ? new Object[]{java.math.BigDecimal.ZERO, 0L} : query.getSingleResult();
    }

    @Override
    public List<Object[]> getRevenueChartData(EntityManager entityManager, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        String jpql = "SELECT DATE(o.orderDate), SUM(o.finalTotal) FROM OrderEntity o WHERE o.status = 'COMPLETED'";
        if (startDate != null && endDate != null) {
            jpql += " AND o.orderDate >= :start AND o.orderDate <= :end";
        }
        jpql += " GROUP BY DATE(o.orderDate) ORDER BY DATE(o.orderDate)";
        
        jakarta.persistence.TypedQuery<Object[]> query = entityManager.createQuery(jpql, Object[].class);
        if (startDate != null && endDate != null) {
            query.setParameter("start", startDate);
            query.setParameter("end", endDate);
        }
        return query.getResultList();
    }
}
