package com.example.salesmis.model.dto;

public class LowStockAlertDTO {
    private String productCode;
    private String productName;
    private Integer quantityStock;
    private Integer minStockLevel;

    public LowStockAlertDTO(String productCode, String productName, Integer quantityStock, Integer minStockLevel) {
        this.productCode = productCode;
        this.productName = productName;
        this.quantityStock = quantityStock;
        this.minStockLevel = minStockLevel;
    }

    public String getProductCode() { return productCode; }
    public String getProductName() { return productName; }
    public Integer getQuantityStock() { return quantityStock; }
    public Integer getMinStockLevel() { return minStockLevel; }
}
