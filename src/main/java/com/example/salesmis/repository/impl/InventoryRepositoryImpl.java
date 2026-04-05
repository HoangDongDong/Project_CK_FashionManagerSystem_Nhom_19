package com.example.salesmis.repository.impl;

import com.example.salesmis.model.entity.Inventory;
import com.example.salesmis.model.entity.Product;
import com.example.salesmis.repository.InventoryRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;

import com.example.salesmis.repository.AbstractJpaRepository;

public class InventoryRepositoryImpl extends AbstractJpaRepository<Inventory, Integer> implements InventoryRepository {

    public InventoryRepositoryImpl() {
        super(Inventory.class);
    }

    @Override
    public void addStock(EntityManager em, Integer productId, int quantity, Integer minStockLevel, Integer maxStockLevel) {
        TypedQuery<Inventory> query = em.createQuery(
                "SELECT i FROM Inventory i WHERE i.product.productId = :productId", Inventory.class);
        query.setParameter("productId", productId);
        
        java.util.List<Inventory> results = query.getResultList();
        Inventory inv;
        if (results.isEmpty()) {
            inv = new Inventory();
            Product p = em.find(Product.class, productId);
            inv.setProduct(p);
            inv.setQuantityStock(quantity);
            inv.setMinStockLevel(minStockLevel != null ? minStockLevel : 0);
            inv.setMaxStockLevel(maxStockLevel != null ? maxStockLevel : 9999);
            inv.setLastUpdated(LocalDateTime.now());
            em.persist(inv);
        } else {
            inv = results.get(0);
            inv.setQuantityStock(inv.getQuantityStock() + quantity);
            if (minStockLevel != null) inv.setMinStockLevel(minStockLevel);
            if (maxStockLevel != null) inv.setMaxStockLevel(maxStockLevel);
            inv.setLastUpdated(LocalDateTime.now());
            em.merge(inv);
        }
    }
}
