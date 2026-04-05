package com.example.salesmis.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDTO {
    private Integer orderId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal finalTotal;
    private String staffName;
    private String customerName;

    public OrderDTO(Integer orderId, String orderNumber, LocalDateTime orderDate, String status, BigDecimal finalTotal, String staffName, String customerName) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.status = status;
        this.finalTotal = finalTotal;
        this.staffName = staffName;
        this.customerName = customerName;
    }

    public Integer getOrderId() { return orderId; }
    public String getOrderNumber() { return orderNumber; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public BigDecimal getFinalTotal() { return finalTotal; }
    public String getStaffName() { return staffName; }
    public String getCustomerName() { return customerName; }
}
