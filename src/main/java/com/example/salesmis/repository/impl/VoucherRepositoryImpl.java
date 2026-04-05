package com.example.salesmis.repository.impl;

import java.util.List;
import java.util.Optional;

import com.example.salesmis.model.entity.Voucher;
import com.example.salesmis.repository.AbstractJpaRepository;
import com.example.salesmis.repository.VoucherRepository;

import jakarta.persistence.EntityManager;

public class VoucherRepositoryImpl extends AbstractJpaRepository<Voucher, Integer> implements VoucherRepository {
    public VoucherRepositoryImpl() {
        super(Voucher.class);
    }

    @Override
    public Optional<Voucher> findActiveByCode(EntityManager entityManager, String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        String normalized = code.trim();
        return entityManager
                .createQuery(
                        "SELECT v FROM Voucher v WHERE LOWER(TRIM(v.code)) = LOWER(:code) "
                                + "AND (v.status IS NULL OR v.status = 'ACTIVE')",
                        Voucher.class)
                .setParameter("code", normalized)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<Voucher> findAllWithCodeOrderByCode(EntityManager entityManager) {
        return entityManager
                .createQuery("SELECT v FROM Voucher v WHERE v.code IS NOT NULL AND TRIM(v.code) <> '' ORDER BY v.code",
                        Voucher.class)
                .getResultList();
    }

    @Override
    public List<Voucher> findAllOrderByIdDesc(EntityManager entityManager) {
        return entityManager.createQuery("SELECT v FROM Voucher v ORDER BY v.voucherId DESC", Voucher.class).getResultList();
    }

    @Override
    public boolean checkCodeExists(EntityManager entityManager, String code) {
        if (code == null || code.isBlank()) return false;
        Long count = entityManager.createQuery("SELECT COUNT(v) FROM Voucher v WHERE LOWER(TRIM(v.code)) = LOWER(:code)", Long.class)
                .setParameter("code", code.trim())
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public List<com.example.salesmis.model.entity.Voucher> getExpiringVouchers(EntityManager entityManager, int daysAhead) {
        String jpql = "SELECT v FROM Voucher v WHERE v.status = 'ACTIVE' AND v.endDate >= :now AND v.endDate <= :maxDate ORDER BY v.endDate ASC";
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime maxDate = now.plusDays(daysAhead);
        
        return entityManager.createQuery(jpql, com.example.salesmis.model.entity.Voucher.class)
                .setParameter("now", now)
                .setParameter("maxDate", maxDate)
                .getResultList();
    }
}
