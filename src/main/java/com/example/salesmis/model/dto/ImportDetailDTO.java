package com.example.salesmis.model.dto;

import java.math.BigDecimal;

public class ImportDetailDTO {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private BigDecimal importPrice;
    private BigDecimal subtotal;

    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getImportPrice() { return importPrice; }
    public void setImportPrice(BigDecimal importPrice) { this.importPrice = importPrice; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    
    private Integer minStockLevel;
    private Integer maxStockLevel;
    
    public Integer getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }
    public Integer getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(Integer maxStockLevel) { this.maxStockLevel = maxStockLevel; }
}
