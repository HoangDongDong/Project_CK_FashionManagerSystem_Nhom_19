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
            dto.setCategoryId(product.getCategory().getCategoryId());
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

    @Override
    public void createNewProduct(com.example.salesmis.model.dto.ProductDTO dto) {
        EntityManager em = entityManagerProvider.createEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product product = new Product();
            product.setProductCode(dto.getProductCode());
            product.setProductName(dto.getProductName());
            product.setBasePrice(dto.getBasePrice());
            product.setDescription(dto.getDescription());
            product.setIsActive(true);
            product.setCreatedAt(java.time.LocalDateTime.now());
            
            if (dto.getCategoryId() != null) {
                com.example.salesmis.model.entity.Category cat = em.find(com.example.salesmis.model.entity.Category.class, dto.getCategoryId());
                product.setCategory(cat);
            }
            
            em.persist(product);

            // Add basic attributes if provided
            if (dto.getColor() != null || dto.getSize() != null || dto.getMaterial() != null) {
                com.example.salesmis.model.entity.ProductAttribute attr = new com.example.salesmis.model.entity.ProductAttribute();
                attr.setProduct(product);
                attr.setColor(dto.getColor());
                attr.setSize(dto.getSize());
                attr.setMaterial(dto.getMaterial());
                attr.setWeight(dto.getWeight());
                em.persist(attr);
            }

            // Init inventory
            if (dto.getQuantityStock() != null && dto.getQuantityStock() >= 0) {
                com.example.salesmis.model.entity.Inventory inv = new com.example.salesmis.model.entity.Inventory();
                inv.setProduct(product);
                inv.setQuantityStock(dto.getQuantityStock());
                inv.setLastUpdated(java.time.LocalDateTime.now());
                em.persist(inv);
            }

            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void editProduct(Integer productId, com.example.salesmis.model.dto.ProductDTO dto) {
        EntityManager em = entityManagerProvider.createEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Product product = em.find(Product.class, productId);
            if (product == null) throw new IllegalArgumentException("Khong tim thay san pham");

            product.setProductCode(dto.getProductCode());
            product.setProductName(dto.getProductName());
            product.setBasePrice(dto.getBasePrice());
            product.setDescription(dto.getDescription());
            
            if (dto.getCategoryId() != null) {
                com.example.salesmis.model.entity.Category cat = em.find(com.example.salesmis.model.entity.Category.class, dto.getCategoryId());
                product.setCategory(cat);
            }
            
            em.merge(product);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void softDeleteProduct(Integer productId) {
        EntityManager em = entityManagerProvider.createEntityManager();
        jakarta.persistence.EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            productRepository.setProductInactive(em, productId);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
