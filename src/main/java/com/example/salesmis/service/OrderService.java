package com.example.salesmis.service;

import java.util.List;

import com.example.salesmis.model.dto.CreateInvoiceRequest;
import com.example.salesmis.model.dto.InvoiceSummaryDTO;
import com.example.salesmis.model.dto.ProductInventoryDTO;
import com.example.salesmis.model.entity.OrderEntity;

public interface OrderService {
    OrderEntity placeOrder(CreateInvoiceRequest request);
    List<ProductInventoryDTO> searchProducts(String keyword);
    List<InvoiceSummaryDTO> getRecentInvoices(int limit);
}

