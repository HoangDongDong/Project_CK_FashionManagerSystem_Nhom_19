package com.example.salesmis.controller;

import com.example.salesmis.model.dto.DashboardDTO;
import com.example.salesmis.service.DashboardService;

public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    public DashboardDTO getDashboardData(String timeFilter) {
        return dashboardService.fetchSummary(timeFilter);
    }
}
