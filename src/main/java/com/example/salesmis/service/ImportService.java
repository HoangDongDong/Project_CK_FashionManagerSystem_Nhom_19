package com.example.salesmis.service;

import com.example.salesmis.model.dto.ImportReceiptDTO;

public interface ImportService {
    void processImport(ImportReceiptDTO importDTO);
}
