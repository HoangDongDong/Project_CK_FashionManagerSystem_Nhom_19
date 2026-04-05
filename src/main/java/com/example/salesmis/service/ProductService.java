package com.example.salesmis.service;

import java.util.List;

import com.example.salesmis.model.dto.ProductDetailDTO;
import com.example.salesmis.model.dto.ProductInventoryDTO;

public interface ProductService {
    // findProducts: tìm kiếm theo tên hoặc mã sản phẩm
    List<ProductInventoryDTO> findProducts(String keyword);
    // getFullProductInfo: lấy thông tin đầy đủ kèm danh sách thuộc tính
    ProductDetailDTO getFullProductInfo(Integer productId);
    void createNewProduct(com.example.salesmis.model.dto.ProductDTO productDTO);
    void editProduct(Integer productId, com.example.salesmis.model.dto.ProductDTO productDTO);
    void softDeleteProduct(Integer productId);
}
