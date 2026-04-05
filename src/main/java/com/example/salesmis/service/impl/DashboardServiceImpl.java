package com.example.salesmis.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.ChartDataDTO;
import com.example.salesmis.model.dto.DashboardDTO;
import com.example.salesmis.model.dto.ExpiringVoucherDTO;
import com.example.salesmis.repository.CustomerRepository;
import com.example.salesmis.repository.ImportRepository;
import com.example.salesmis.repository.InvoiceRepository;
import com.example.salesmis.repository.ProductRepository;
import com.example.salesmis.repository.VoucherRepository;
import com.example.salesmis.service.DashboardService;

import jakarta.persistence.EntityManager;

public class DashboardServiceImpl implements DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final ImportRepository importRepository;
    private final EntityManagerProvider entityManagerProvider;

    public DashboardServiceImpl(InvoiceRepository invoiceRepository, CustomerRepository customerRepository,
            ProductRepository productRepository, VoucherRepository voucherRepository,
            ImportRepository importRepository, EntityManagerProvider entityManagerProvider) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.voucherRepository = voucherRepository;
        this.importRepository = importRepository;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public DashboardDTO fetchSummary(String timeFilter) {
        EntityManager em = entityManagerProvider.createEntityManager();
        try {
            DashboardDTO dto = new DashboardDTO();

            LocalDateTime start = null;
            LocalDateTime end = null;
            LocalDate today = LocalDate.now();

            if ("TODAY".equals(timeFilter)) {
                start = today.atStartOfDay();
                end = today.atTime(LocalTime.MAX);
            } else if ("THIS_WEEK".equals(timeFilter)) {
                start = today.minusDays(today.getDayOfWeek().getValue() - 1).atStartOfDay();
                end = start.plusDays(6).with(LocalTime.MAX);
            } else if ("THIS_MONTH".equals(timeFilter)) {
                start = YearMonth.from(today).atDay(1).atStartOfDay();
                end = YearMonth.from(today).atEndOfMonth().atTime(LocalTime.MAX);
            }
            // "ALL_TIME" leaves start and end as null

            // 1. Dãy thẻ số liệu tổng quan (KPIs)
            Object[] revenueAndOrder = invoiceRepository.getRevenueAndOrderCount(em, start, end);
            BigDecimal totalRevenue = (BigDecimal) revenueAndOrder[0];
            Long orderCount = (Long) revenueAndOrder[1];
            
            dto.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            dto.setOrderCount(orderCount != null ? orderCount : 0L);

            BigDecimal totalImport = importRepository.getImportCostInPeriod(em, start, end);
            dto.setTotalImportCost(totalImport != null ? totalImport : BigDecimal.ZERO);

            Long customerCount = customerRepository.getTotalCustomers(em, start, end);
            dto.setCustomerCount(customerCount != null ? customerCount : 0L);

            // 2. Khu vực cảnh báo (Alerts)
            dto.setLowStockAlerts(productRepository.getLowStockAlerts(em));
            
            List<ExpiringVoucherDTO> expiring = voucherRepository.getExpiringVouchers(em, 3).stream()
                    .map(v -> new ExpiringVoucherDTO(v.getCode(), v.getStatus(), v.getEndDate(), v.getUsageLimit()))
                    .collect(Collectors.toList());
            dto.setExpiringVouchers(expiring);

            List<Object[]> revenueRaw = invoiceRepository.getRevenueChartData(em, start, end);
            List<Object[]> importRaw = importRepository.getImportCostChartData(em, start, end);
            
            java.util.Map<String, BigDecimal> revenueMap = new java.util.LinkedHashMap<>();
            java.util.Map<String, BigDecimal> importMap = new java.util.LinkedHashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (Object[] row : revenueRaw) {
                if (row[0] != null) {
                    String label = ((java.sql.Date) row[0]).toLocalDate().format(formatter);
                    revenueMap.put(label, (BigDecimal) row[1]);
                }
            }
            for (Object[] row : importRaw) {
                if (row[0] != null) {
                    String label = ((java.sql.Date) row[0]).toLocalDate().format(formatter);
                    importMap.put(label, (BigDecimal) row[1]);
                }
            }
            
            java.util.Set<String> allLabels = new java.util.TreeSet<>();
            allLabels.addAll(revenueMap.keySet());
            allLabels.addAll(importMap.keySet());

            List<ChartDataDTO> chartData = new ArrayList<>();
            for (String label : allLabels) {
                BigDecimal rev = revenueMap.getOrDefault(label, BigDecimal.ZERO);
                BigDecimal imp = importMap.getOrDefault(label, BigDecimal.ZERO);
                chartData.add(new ChartDataDTO(label, rev, imp));
            }
            dto.setRevenueChartData(chartData);

            return dto;
        } finally {
            em.close();
        }
    }
}
