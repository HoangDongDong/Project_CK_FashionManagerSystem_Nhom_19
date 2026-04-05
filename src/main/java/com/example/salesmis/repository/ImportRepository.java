package com.example.salesmis.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.example.salesmis.model.entity.ImportDetail;
import com.example.salesmis.model.entity.ImportReceipt;

import jakarta.persistence.EntityManager;

public interface ImportRepository extends GenericRepository<ImportReceipt, Integer> {
    void saveDetail(EntityManager em, ImportDetail detail);
    java.math.BigDecimal getImportCostInPeriod(EntityManager em, LocalDateTime startDate, LocalDateTime endDate);
    List<Object[]> getImportCostChartData(EntityManager em, LocalDateTime startDate, LocalDateTime endDate);
}
