package com.example.salesmis.model.dto;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {
    private BigDecimal totalRevenue;
    private Long orderCount;
    private Long customerCount;
    
    private List<LowStockAlertDTO> lowStockAlerts;
    private List<ExpiringVoucherDTO> expiringVouchers;
    private List<ChartDataDTO> revenueChartData;

    public DashboardDTO() {
        this.totalRevenue = BigDecimal.ZERO;
        this.orderCount = 0L;
        this.customerCount = 0L;
    }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public Long getOrderCount() { return orderCount; }
    public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }
    public Long getCustomerCount() { return customerCount; }
    public void setCustomerCount(Long customerCount) { this.customerCount = customerCount; }
    
    private BigDecimal totalImportCost;
    public BigDecimal getTotalImportCost() { return totalImportCost; }
    public void setTotalImportCost(BigDecimal totalImportCost) { this.totalImportCost = totalImportCost; }
    public List<LowStockAlertDTO> getLowStockAlerts() { return lowStockAlerts; }
    public void setLowStockAlerts(List<LowStockAlertDTO> lowStockAlerts) { this.lowStockAlerts = lowStockAlerts; }
    public List<ExpiringVoucherDTO> getExpiringVouchers() { return expiringVouchers; }
    public void setExpiringVouchers(List<ExpiringVoucherDTO> expiringVouchers) { this.expiringVouchers = expiringVouchers; }
    public List<ChartDataDTO> getRevenueChartData() { return revenueChartData; }
    public void setRevenueChartData(List<ChartDataDTO> revenueChartData) { this.revenueChartData = revenueChartData; }
}
