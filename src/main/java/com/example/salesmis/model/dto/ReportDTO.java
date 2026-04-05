package com.example.salesmis.model.dto;

import java.math.BigDecimal;
import java.util.Map;

public class ReportDTO {
    private BigDecimal totalRevenue;
    private int totalOrders;
    private Map<String, BigDecimal> chartData;

    public ReportDTO() {}

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    private BigDecimal totalImportCost;

    public BigDecimal getTotalImportCost() {
        return totalImportCost;
    }

    public void setTotalImportCost(BigDecimal totalImportCost) {
        this.totalImportCost = totalImportCost;
    }

    public Map<String, BigDecimal> getChartData() {
        return chartData;
    }

    public void setChartData(Map<String, BigDecimal> chartData) {
        this.chartData = chartData;
    }

    private java.util.List<OrderDTO> orders;

    public java.util.List<OrderDTO> getOrders() {
        return orders;
    }

    public void setOrders(java.util.List<OrderDTO> orders) {
        this.orders = orders;
    }
}
