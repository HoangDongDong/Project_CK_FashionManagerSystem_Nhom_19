package com.example.salesmis.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.ReportDTO;
import com.example.salesmis.model.entity.OrderEntity;
import com.example.salesmis.repository.ImportRepository;
import com.example.salesmis.repository.InvoiceRepository;
import com.example.salesmis.service.ReportService;

import jakarta.persistence.EntityManager;

public class ReportServiceImpl implements ReportService {

    private final InvoiceRepository invoiceRepository;
    private final ImportRepository importRepository;
    private final EntityManagerProvider entityManagerProvider;

    public ReportServiceImpl(InvoiceRepository invoiceRepository, ImportRepository importRepository, EntityManagerProvider entityManagerProvider) {
        this.invoiceRepository = invoiceRepository;
        this.importRepository = importRepository;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public ReportDTO calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate, Integer staffId) {
        EntityManager em = entityManagerProvider.createEntityManager();
        try {
            List<OrderEntity> orders = invoiceRepository.findOrdersInPeriod(em, startDate, endDate, staffId);
            
            BigDecimal finalTotal = BigDecimal.ZERO;
            Map<String, BigDecimal> dailyRevenue = new LinkedHashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<com.example.salesmis.model.dto.OrderDTO> orderDTOs = new java.util.ArrayList<>();

            for (OrderEntity order : orders) {
                // Chỉ thống kê những hóa đơn có trạng thái "COMPLETED" (nếu có yêu cầu, tùy dự án)
                if ("COMPLETED".equalsIgnoreCase(order.getStatus())) {
                    BigDecimal orderFinal = order.getFinalTotal() != null ? order.getFinalTotal() : BigDecimal.ZERO;
                    finalTotal = finalTotal.add(orderFinal);
                    
                    String dayKey = order.getOrderDate().format(formatter);
                    dailyRevenue.put(dayKey, dailyRevenue.getOrDefault(dayKey, BigDecimal.ZERO).add(orderFinal));
                    
                    String staffName = order.getStaff() != null ? order.getStaff().getFullName() : "N/A";
                    String customerName = order.getCustomer() != null ? order.getCustomer().getCustomerName() : "Khách vãng lai";
                    
                    orderDTOs.add(new com.example.salesmis.model.dto.OrderDTO(
                        order.getOrderId(), order.getOrderNumber(), order.getOrderDate(), order.getStatus(), orderFinal, staffName, customerName
                    ));
                }
            }

            ReportDTO reportDTO = new ReportDTO();
            reportDTO.setTotalRevenue(finalTotal);
            reportDTO.setTotalOrders(orderDTOs.size());
            reportDTO.setChartData(dailyRevenue);
            reportDTO.setOrders(orderDTOs);
            
            BigDecimal importCost = importRepository.getImportCostInPeriod(em, startDate, endDate);
            reportDTO.setTotalImportCost(importCost);

            return reportDTO;
        } finally {
            em.close();
        }
    }
}
