package com.example.salesmis.model.dto;

import java.math.BigDecimal;

public class ProductDTO {
    private Integer productId;
    private Integer categoryId;
    private String productCode;
    private String productName;
    private BigDecimal basePrice;
    private String description;
    private Boolean isActive;
    
    // ProductAttribute fields (simplified for UI form)
    private String material;
    private String size;
    private String color;
    private BigDecimal weight;
    
    // Inventory field
    private Integer quantityStock;

    public ProductDTO() {}

    // Getters and Setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    private String categoryName;
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public Integer getQuantityStock() { return quantityStock; }
    public void setQuantityStock(Integer quantityStock) { this.quantityStock = quantityStock; }
}
