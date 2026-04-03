package com.example.salesmis.controller;

import java.util.List;

import com.example.salesmis.model.dto.ProductDetailDTO;
import com.example.salesmis.model.dto.ProductInventoryDTO;
import com.example.salesmis.service.ProductService;

public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Gọi từ ProductSearchPanel.searchProducts(keyword)
    public List<ProductInventoryDTO> findProducts(String keyword) {
        return productService.findProducts(keyword);
    }

    // Gọi từ ProductDetailDialog.getProductDetails(productId)
    public ProductDetailDTO getProductDetails(Integer productId) {
        return productService.getFullProductInfo(productId);
    }
}
