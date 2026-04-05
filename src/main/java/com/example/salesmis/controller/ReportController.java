package com.example.salesmis.controller;

import java.time.LocalDateTime;

import com.example.salesmis.model.dto.ReportDTO;
import com.example.salesmis.service.ReportService;

public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    public ReportDTO getRevenueReport(LocalDateTime startDate, LocalDateTime endDate, Integer staffId) {
        return reportService.calculateTotalRevenue(startDate, endDate, staffId);
    }
}
