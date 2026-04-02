package com.example.salesmis.controller;

import java.util.List;

import com.example.salesmis.model.dto.CreateInvoiceRequest;
import com.example.salesmis.model.dto.InvoiceSummaryDTO;
import com.example.salesmis.model.dto.ProductInventoryDTO;
import com.example.salesmis.model.entity.OrderEntity;
import com.example.salesmis.service.OrderService;

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public OrderEntity createOrder(CreateInvoiceRequest request) {
        return orderService.placeOrder(request);
    }

    public List<ProductInventoryDTO> searchProducts(String keyword) {
        return orderService.searchProducts(keyword);
    }

    public List<InvoiceSummaryDTO> getRecentInvoices(int limit) {
        return orderService.getRecentInvoices(limit);
    }
}

