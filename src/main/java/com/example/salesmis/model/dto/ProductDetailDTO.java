package com.example.salesmis.model.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductDetailDTO {
    private Integer productId;
    private String productCode;
    private String productName;
    private BigDecimal basePrice;
    private String description;
    private String categoryName;
    private Integer quantityStock;
    private List<AttributeRow> attributes;

    public static class AttributeRow {
        private Integer attributeId;
        private String size;
        private String color;
        private String material;
        private BigDecimal weight;

        public AttributeRow(Integer attributeId, String size, String color, String material, BigDecimal weight) {
            this.attributeId = attributeId;
            this.size = size;
            this.color = color;
            this.material = material;
            this.weight = weight;
        }

        public Integer getAttributeId() { return attributeId; }
        public String getSize() { return size; }
        public String getColor() { return color; }
        public String getMaterial() { return material; }
        public BigDecimal getWeight() { return weight; }
    }

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    
    private Integer categoryId;
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Integer getQuantityStock() { return quantityStock; }
    public void setQuantityStock(Integer quantityStock) { this.quantityStock = quantityStock; }
    public List<AttributeRow> getAttributes() { return attributes; }
    public void setAttributes(List<AttributeRow> attributes) { this.attributes = attributes; }
}
