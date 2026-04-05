package com.example.salesmis.repository;

import java.util.List;
import java.util.Optional;

import com.example.salesmis.model.entity.Voucher;

import jakarta.persistence.EntityManager;

public interface VoucherRepository extends GenericRepository<Voucher, Integer> {
    /**
     * Tim voucher dang ACTIVE theo ma (tuong duong SELECT ... WHERE code = ? AND status = 'ACTIVE').
     */
    Optional<Voucher> findActiveByCode(EntityManager entityManager, String code);

    /** Tat ca voucher co ma (sap xep theo code) de hien thi tren giao dien chon. */
    List<Voucher> findAllWithCodeOrderByCode(EntityManager entityManager);

    /** Lay danh sach vouchers sap xep moi nhat */
    List<Voucher> findAllOrderByIdDesc(EntityManager entityManager);

    /** Kiem tra ma code da ton tai chua */
    boolean checkCodeExists(EntityManager entityManager, String code);

    /** Lay cac voucher sap het han trong khoang daysAhead ngay (tinh ca hien tai) */
    List<Voucher> getExpiringVouchers(EntityManager entityManager, int daysAhead);
}
