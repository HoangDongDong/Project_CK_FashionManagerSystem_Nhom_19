package com.example.salesmis.model.dto;

import java.time.LocalDateTime;

public class ExpiringVoucherDTO {
    private String code;
    private String status;
    private LocalDateTime endDate;
    private Integer usageLimit;

    public ExpiringVoucherDTO(String code, String status, LocalDateTime endDate, Integer usageLimit) {
        this.code = code;
        this.status = status;
        this.endDate = endDate;
        this.usageLimit = usageLimit;
    }

    public String getCode() { return code; }
    public String getStatus() { return status; }
    public LocalDateTime getEndDate() { return endDate; }
    public Integer getUsageLimit() { return usageLimit; }
}
