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
}
