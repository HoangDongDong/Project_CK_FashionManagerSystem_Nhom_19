package com.example.salesmis.controller;

import java.math.BigDecimal;
import java.util.List;

import com.example.salesmis.model.dto.DiscountResult;
import com.example.salesmis.model.dto.VoucherListItemDTO;
import com.example.salesmis.service.VoucherService;

/**
 * Dieu phoi ap dung voucher tu OrderPanel: goi Service, View bat exception de hien thong bao.
 */
public class VoucherController {
    private final VoucherService voucherService;

    public VoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    public DiscountResult applyVoucher(String voucherCode, BigDecimal currentOrderTotal) {
        return voucherService.validateAndCalculateDiscount(voucherCode, currentOrderTotal);
    }

    public List<VoucherListItemDTO> listVouchersForInvoice() {
        return voucherService.listVouchersForSelection();
    }

    public List<com.example.salesmis.model.dto.VoucherDTO> getAllVouchers() {
        return voucherService.getVoucherList();
    }

    public void createVoucher(com.example.salesmis.model.dto.VoucherDTO voucherDTO) {
        voucherService.addVoucher(voucherDTO);
    }

    public void updateVoucher(Integer id, com.example.salesmis.model.dto.VoucherDTO updateDTO) {
        voucherService.editVoucher(id, updateDTO);
    }

    public void disableVoucher(Integer id) {
        voucherService.setVoucherInactive(id);
    }
}
