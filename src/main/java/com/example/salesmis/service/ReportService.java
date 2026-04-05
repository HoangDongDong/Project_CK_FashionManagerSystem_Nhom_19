package com.example.salesmis.service;

import java.time.LocalDateTime;

import com.example.salesmis.model.dto.ReportDTO;

public interface ReportService {
    ReportDTO calculateTotalRevenue(LocalDateTime startDate, LocalDateTime endDate, Integer staffId);
}
