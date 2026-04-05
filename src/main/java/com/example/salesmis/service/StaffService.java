package com.example.salesmis.service;

import java.util.List;
import com.example.salesmis.model.dto.StaffDTO;

public interface StaffService {
    List<StaffDTO> getAllStaff();
    void save(StaffDTO dto);
    void updateStatus(Integer staffId, boolean isActive);
}
