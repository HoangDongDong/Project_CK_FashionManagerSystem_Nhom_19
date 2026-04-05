package com.example.salesmis.controller;

import com.example.salesmis.model.dto.ImportReceiptDTO;
import com.example.salesmis.service.ImportService;

public class ImportController {
    private final ImportService importService;

    public ImportController(ImportService importService) {
        this.importService = importService;
    }

    public void createImportReceipt(ImportReceiptDTO importDTO) {
        importService.processImport(importDTO);
    }
}
