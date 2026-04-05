package com.example.salesmis.repository.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.salesmis.model.entity.ImportDetail;
import com.example.salesmis.model.entity.ImportReceipt;
import com.example.salesmis.repository.ImportRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import com.example.salesmis.repository.AbstractJpaRepository;

public class ImportRepositoryImpl extends AbstractJpaRepository<ImportReceipt, Integer> implements ImportRepository {

    public ImportRepositoryImpl() {
        super(ImportReceipt.class);
    }

    @Override
    public void saveDetail(EntityManager em, ImportDetail detail) {
        if (detail.getDetailId() == null) {
            em.persist(detail);
        } else {
            em.merge(detail);
        }
    }

    @Override
    public BigDecimal getImportCostInPeriod(EntityManager em, LocalDateTime startDate, LocalDateTime endDate) {
        String jpql = "SELECT SUM(r.totalCost) FROM ImportReceipt r WHERE r.importDate >= :startDate AND r.importDate <= :endDate";
        TypedQuery<BigDecimal> query = em.createQuery(jpql, BigDecimal.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public List<Object[]> getImportCostChartData(EntityManager em, LocalDateTime startDate, LocalDateTime endDate) {
        String jpql = "SELECT DATE(r.importDate), SUM(r.totalCost) FROM ImportReceipt r " +
                      "WHERE r.importDate >= :startDate AND r.importDate <= :endDate " +
                      "GROUP BY DATE(r.importDate) ORDER BY DATE(r.importDate) ASC";
        return em.createQuery(jpql, Object[].class)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}
