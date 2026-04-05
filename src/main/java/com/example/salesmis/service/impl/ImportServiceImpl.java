package com.example.salesmis.service.impl;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.ImportDetailDTO;
import com.example.salesmis.model.dto.ImportReceiptDTO;
import com.example.salesmis.model.entity.ImportDetail;
import com.example.salesmis.model.entity.ImportReceipt;
import com.example.salesmis.model.entity.Product;
import com.example.salesmis.model.entity.Staff;
import com.example.salesmis.model.entity.Supplier;
import com.example.salesmis.repository.ImportRepository;
import com.example.salesmis.repository.InventoryRepository;
import com.example.salesmis.service.ImportService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ImportServiceImpl implements ImportService {

    private final ImportRepository importRepository;
    private final InventoryRepository inventoryRepository;
    private final EntityManagerProvider emProvider;

    public ImportServiceImpl(ImportRepository importRepository, InventoryRepository inventoryRepository, EntityManagerProvider emProvider) {
        this.importRepository = importRepository;
        this.inventoryRepository = inventoryRepository;
        this.emProvider = emProvider;
    }

    @Override
    public void processImport(ImportReceiptDTO importDTO) {
        EntityManager em = emProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            ImportReceipt receipt = new ImportReceipt();
            if (importDTO.getSupplierId() != null) {
                receipt.setSupplier(em.find(Supplier.class, importDTO.getSupplierId()));
            }
            if (importDTO.getStaffId() != null) {
                receipt.setStaff(em.find(Staff.class, importDTO.getStaffId()));
            }
            receipt.setImportDate(importDTO.getImportDate());
            receipt.setTotalCost(importDTO.getTotalCost());
            receipt.setStatus(importDTO.getStatus());
            receipt.setNote(importDTO.getNote());

            receipt = importRepository.save(em, receipt);

            for (ImportDetailDTO detailDTO : importDTO.getItems()) {
                ImportDetail detail = new ImportDetail();
                detail.setReceipt(receipt);
                detail.setProduct(em.find(Product.class, detailDTO.getProductId()));
                detail.setQuantity(detailDTO.getQuantity());
                detail.setImportPrice(detailDTO.getImportPrice());
                detail.setSubtotal(detailDTO.getSubtotal());

                importRepository.saveDetail(em, detail);

                // Update inventory
                inventoryRepository.addStock(em, detailDTO.getProductId(), detailDTO.getQuantity(), detailDTO.getMinStockLevel(), detailDTO.getMaxStockLevel());
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Error processing import receipt", e);
        } finally {
            em.close();
        }
    }
}
