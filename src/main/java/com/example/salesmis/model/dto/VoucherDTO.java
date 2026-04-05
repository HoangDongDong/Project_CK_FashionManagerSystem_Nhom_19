package com.example.salesmis.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VoucherDTO {
    private Integer voucherId;
    private String code;
    private BigDecimal discountValue;
    private String discountType; // e.g. PERCENTAGE, FIXED_AMOUNT
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private String status;
    private BigDecimal minOrderValue;

    public Integer getVoucherId() { return voucherId; }
    public void setVoucherId(Integer voucherId) { this.voucherId = voucherId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getMinOrderValue() { return minOrderValue; }
    public void setMinOrderValue(BigDecimal minOrderValue) { this.minOrderValue = minOrderValue; }
}
