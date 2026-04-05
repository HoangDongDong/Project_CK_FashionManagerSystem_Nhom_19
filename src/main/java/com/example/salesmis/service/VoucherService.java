package com.example.salesmis.service;

import java.math.BigDecimal;
import java.util.List;

import com.example.salesmis.model.dto.DiscountResult;
import com.example.salesmis.model.dto.VoucherListItemDTO;

public interface VoucherService {
    /**
     * Kiem tra ma, han, dieu kien toi thieu va tinh giam gia (sequence: Service nhan voucherCode + tong don hien tai).
     */
    DiscountResult validateAndCalculateDiscount(String voucherCode, BigDecimal currentOrderTotal);

    /**
     * Kiem tra lai khi luu hoa don (theo voucherId da ap dung o giao dien).
     */
    DiscountResult validateAndCalculateForOrder(Integer voucherId, BigDecimal currentOrderTotal);

    /** Danh sach ma giam gia de chon tren man hinh lap hoa don. */
    List<VoucherListItemDTO> listVouchersForSelection();

    // Administrative methods
    List<com.example.salesmis.model.dto.VoucherDTO> getVoucherList();
    void addVoucher(com.example.salesmis.model.dto.VoucherDTO voucherDTO);
    void editVoucher(Integer id, com.example.salesmis.model.dto.VoucherDTO voucherDTO);
    void setVoucherInactive(Integer id);
}
