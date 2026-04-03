package com.example.salesmis.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.ProductDetailDTO;
import com.example.salesmis.model.dto.ProductInventoryDTO;
import com.example.salesmis.model.entity.Product;
import com.example.salesmis.repository.ProductRepository;
import com.example.salesmis.service.ProductService;

import jakarta.persistence.EntityManager;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final EntityManagerProvider entityManagerProvider;

    public ProductServiceImpl(ProductRepository productRepository, EntityManagerProvider entityManagerProvider) {
        this.productRepository = productRepository;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public List<ProductInventoryDTO> findProducts(String keyword) {
        EntityManager entityManager = entityManagerProvider.createEntityManager();
        try {
            return productRepository.searchProductsWithStock(entityManager, keyword);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public ProductDetailDTO getFullProductInfo(Integer productId) {
        EntityManager entityManager = entityManagerProvider.createEntityManager();
        try {
            Product product = productRepository.findProductWithAttributes(entityManager, productId)
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay san pham ID: " + productId));
            return toDetailDTO(product);
        } finally {
            entityManager.close();
        }
    }

    private ProductDetailDTO toDetailDTO(Product product) {
        ProductDetailDTO dto = new ProductDetailDTO();
        dto.setProductId(product.getProductId());
        dto.setProductCode(product.getProductCode());
        dto.setProductName(product.getProductName());
        dto.setBasePrice(product.getBasePrice());
        dto.setDescription(product.getDescription());
        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getCategoryName());
        }
        if (product.getInventory() != null) {
            dto.setQuantityStock(product.getInventory().getQuantityStock());
        } else {
            dto.setQuantityStock(0);
        }
        List<ProductDetailDTO.AttributeRow> rows = product.getAttributes().stream()
                .map(attr -> new ProductDetailDTO.AttributeRow(
                        attr.getAttributeId(),
                        attr.getSize(),
                        attr.getColor(),
                        attr.getMaterial(),
                        attr.getWeight()))
                .collect(Collectors.toList());
        dto.setAttributes(rows);
        return dto;
    }
}
