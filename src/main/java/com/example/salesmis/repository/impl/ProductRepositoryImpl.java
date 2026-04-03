package com.example.salesmis.repository.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.salesmis.model.dto.ProductInventoryDTO;
import com.example.salesmis.model.entity.Inventory;
import com.example.salesmis.model.entity.Product;
import com.example.salesmis.repository.AbstractJpaRepository;
import com.example.salesmis.repository.ProductRepository;

import jakarta.persistence.EntityManager;

public class ProductRepositoryImpl extends AbstractJpaRepository<Product, Integer> implements ProductRepository {
    public ProductRepositoryImpl() {
        super(Product.class);
    }

    @Override
    public Optional<Product> findByCode(EntityManager entityManager, String productCode) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.productCode = :productCode", Product.class)
                .setParameter("productCode", productCode)
                .getResultStream()
                .findFirst();
    }

    @Override
    public List<ProductInventoryDTO> searchProductsWithStock(EntityManager entityManager, String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        String pattern = "%" + normalizedKeyword + "%";

        return entityManager.createQuery("""
                SELECT p.productId, p.productCode, p.productName, p.basePrice, COALESCE(i.quantityStock, 0)
                FROM Product p
                LEFT JOIN p.inventory i
                WHERE (p.isActive = true OR p.isActive IS NULL)
                    AND (
                        :keyword = ''
                        OR LOWER(p.productCode) LIKE :pattern
                        OR LOWER(p.productName) LIKE :pattern
                    )
                ORDER BY p.productName
                """, Object[].class)
                .setParameter("keyword", normalizedKeyword)
                .setParameter("pattern", pattern)
                .getResultStream()
                .map(row -> {
                    ProductInventoryDTO dto = new ProductInventoryDTO();
                    dto.setProductId((Integer) row[0]);
                    dto.setProductCode((String) row[1]);
                    dto.setProductName((String) row[2]);
                    dto.setBasePrice((java.math.BigDecimal) row[3]);
                    dto.setQuantityStock((Integer) row[4]);
                    return dto;
                })
                .toList();
    }

    @Override
    public int getStockQuantity(EntityManager entityManager, Integer productId) {
        Integer quantity = entityManager.createQuery(
                        "SELECT i.quantityStock FROM Inventory i WHERE i.product.productId = :productId", Integer.class)
                .setParameter("productId", productId)
                .getResultStream()
                .findFirst()
                .orElse(0);
        return quantity == null ? 0 : quantity;
    }

    @Override
    public boolean hasEnoughStock(EntityManager entityManager, Integer productId, int requiredQuantity) {
        return getStockQuantity(entityManager, productId) >= requiredQuantity;
    }

    @Override
    public Optional<Product> findProductWithAttributes(EntityManager entityManager, Integer productId) {
        // LEFT JOIN FETCH to eagerly load attributes in one query (avoids N+1)
        return entityManager.createQuery(
                "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.attributes LEFT JOIN FETCH p.inventory WHERE p.productId = :id",
                Product.class)
                .setParameter("id", productId)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void decreaseStock(EntityManager entityManager, Integer productId, int quantity) {
        Inventory inventory = entityManager.createQuery(
                        "SELECT i FROM Inventory i WHERE i.product.productId = :productId", Inventory.class)
                .setParameter("productId", productId)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Inventory not found for productId=" + productId));

        int currentStock = inventory.getQuantityStock() == null ? 0 : inventory.getQuantityStock();
        if (currentStock < quantity) {
            throw new IllegalStateException("Insufficient stock for productId=" + productId);
        }

        inventory.setQuantityStock(currentStock - quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        entityManager.merge(inventory);
    }
}
