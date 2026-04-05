package com.example.salesmis.repository;

import com.example.salesmis.model.entity.Inventory;

import jakarta.persistence.EntityManager;

public interface InventoryRepository extends GenericRepository<Inventory, Integer> {
    void addStock(EntityManager em, Integer productId, int quantity, Integer minStockLevel, Integer maxStockLevel);
}
