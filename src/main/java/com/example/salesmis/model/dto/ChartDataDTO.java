package com.example.salesmis.model.dto;

import java.math.BigDecimal;

public class ChartDataDTO {
    private String dateLabel;
    private BigDecimal totalRevenue;
    private BigDecimal totalImportCost;

    public ChartDataDTO(String dateLabel, BigDecimal totalRevenue, BigDecimal totalImportCost) {
        this.dateLabel = dateLabel;
        this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
        this.totalImportCost = totalImportCost != null ? totalImportCost : BigDecimal.ZERO;
    }

    public String getDateLabel() { return dateLabel; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public BigDecimal getTotalImportCost() { return totalImportCost; }
}
