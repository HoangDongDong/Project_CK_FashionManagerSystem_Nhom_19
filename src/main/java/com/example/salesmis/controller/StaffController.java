package com.example.salesmis.controller;

import java.util.List;

import com.example.salesmis.model.dto.StaffDTO;
import com.example.salesmis.service.StaffService;

public class StaffController {
    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    public List<StaffDTO> getStaffList() {
        return staffService.getAllStaff();
    }

    public void addStaff(StaffDTO dto) {
        staffService.save(dto);
    }

    public void updateStaff(Integer id, StaffDTO dto) {
        dto.setStaffId(id);
        staffService.save(dto);
    }

    public void lockStaff(Integer id) {
        staffService.updateStatus(id, false);
    }
}
