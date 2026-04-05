package com.example.salesmis.service;

import com.example.salesmis.model.dto.DashboardDTO;

public interface DashboardService {
    DashboardDTO fetchSummary(String timeFilter);
}
