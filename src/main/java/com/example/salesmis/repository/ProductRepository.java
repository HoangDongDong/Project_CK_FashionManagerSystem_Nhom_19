package com.example.salesmis.repository;

import java.util.List;
import java.util.Optional;

import com.example.salesmis.model.dto.ProductInventoryDTO;
import com.example.salesmis.model.entity.Product;

import jakarta.persistence.EntityManager;

public interface ProductRepository extends GenericRepository<Product, Integer> {
    Optional<Product> findByCode(EntityManager entityManager, String productCode);
    List<ProductInventoryDTO> searchProductsWithStock(EntityManager entityManager, String keyword);
    int getStockQuantity(EntityManager entityManager, Integer productId);
    boolean hasEnoughStock(EntityManager entityManager, Integer productId, int requiredQuantity);
    void decreaseStock(EntityManager entityManager, Integer productId, int quantity);
    long countByCategoryId(EntityManager entityManager, Integer categoryId);
    // findProductWithAttributes: LEFT JOIN to load attributes collection eagerly
    Optional<Product> findProductWithAttributes(EntityManager entityManager, Integer productId);
    void setProductInactive(EntityManager entityManager, Integer productId);
    Product saveFullProduct(EntityManager entityManager, Product product);
    List<com.example.salesmis.model.dto.LowStockAlertDTO> getLowStockAlerts(EntityManager entityManager);
}
