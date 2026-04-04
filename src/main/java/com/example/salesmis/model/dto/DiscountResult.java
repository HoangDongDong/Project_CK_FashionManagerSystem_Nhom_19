package com.example.salesmis.model.dto;

import java.math.BigDecimal;

/** Ket qua ap dung voucher: tien giam va tong don sau giam (theo sequence diagram). */
public class DiscountResult {
    private Integer voucherId;
    private BigDecimal discountAmount;
    private BigDecimal newTotal;

    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getNewTotal() {
        return newTotal;
    }

    public void setNewTotal(BigDecimal newTotal) {
        this.newTotal = newTotal;
    }
}
