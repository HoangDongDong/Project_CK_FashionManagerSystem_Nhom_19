package com.example.salesmis.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ImportReceiptDTO {
    private Integer receiptId;
    private Integer supplierId;
    private Integer staffId;
    private LocalDateTime importDate;
    private BigDecimal totalCost;
    private String status;
    private String note;
    private List<ImportDetailDTO> items;

    public Integer getReceiptId() { return receiptId; }
    public void setReceiptId(Integer receiptId) { this.receiptId = receiptId; }
    public Integer getSupplierId() { return supplierId; }
    public void setSupplierId(Integer supplierId) { this.supplierId = supplierId; }
    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public LocalDateTime getImportDate() { return importDate; }
    public void setImportDate(LocalDateTime importDate) { this.importDate = importDate; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public List<ImportDetailDTO> getItems() { return items; }
    public void setItems(List<ImportDetailDTO> items) { this.items = items; }
}
