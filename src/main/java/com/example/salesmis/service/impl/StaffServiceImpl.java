package com.example.salesmis.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.StaffDTO;
import com.example.salesmis.model.entity.Account;
import com.example.salesmis.model.entity.Staff;
import com.example.salesmis.repository.AccountRepository;
import com.example.salesmis.repository.StaffRepository;
import com.example.salesmis.service.StaffService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final AccountRepository accountRepository;
    private final EntityManagerProvider entityManagerProvider;

    public StaffServiceImpl(StaffRepository staffRepository, AccountRepository accountRepository, EntityManagerProvider entityManagerProvider) {
        this.staffRepository = staffRepository;
        this.accountRepository = accountRepository;
        this.entityManagerProvider = entityManagerProvider;
    }

    @Override
    public List<StaffDTO> getAllStaff() {
        EntityManager em = entityManagerProvider.createEntityManager();
        try {
            List<Staff> staffList = staffRepository.findAllStaff(em);
            return staffList.stream().map(this::toDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    @Override
    public void save(StaffDTO dto) {
        EntityManager em = entityManagerProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            Staff staff;
            Account account;
            
            if (dto.getStaffId() != null) {
                // Update existing
                staff = staffRepository.findById(em, dto.getStaffId())
                        .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
                account = staff.getAccount();
                
                // Update Staff info
                staff.setFullName(dto.getFullName());
                staff.setPhone(dto.getPhone());
                staff.setEmail(dto.getEmail());
                staff.setAddress(dto.getAddress());
                
                // Update Account info
                if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
                    account.setPasswordHash(dto.getPassword().trim());
                }
                if (dto.getIsActive() != null) {
                    account.setIsActive(dto.getIsActive());
                }
                
                staffRepository.save(em, staff);
                accountRepository.save(em, account);
            } else {
                // Create new
                account = new Account();
                account.setUsername(dto.getUsername());
                account.setPasswordHash(dto.getPassword());
                account.setRoleName("STAFF");
                account.setIsActive(true);
                account.setCreatedAt(LocalDateTime.now());
                account = accountRepository.save(em, account);
                
                staff = new Staff();
                staff.setFullName(dto.getFullName());
                staff.setPhone(dto.getPhone());
                staff.setEmail(dto.getEmail());
                staff.setAddress(dto.getAddress());
                staff.setHireDate(LocalDateTime.now());
                staff.setAccount(account);
                
                staffRepository.save(em, staff);
            }
            
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void updateStatus(Integer staffId, boolean isActive) {
        EntityManager em = entityManagerProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Staff staff = staffRepository.findById(em, staffId)
                    .orElseThrow(() -> new IllegalArgumentException("Khong tim thay nhan vien"));
            Account account = staff.getAccount();
            if (account != null) {
                account.setIsActive(isActive);
                accountRepository.save(em, account);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    private StaffDTO toDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setStaffId(staff.getStaffId());
        dto.setFullName(staff.getFullName());
        dto.setPhone(staff.getPhone());
        dto.setEmail(staff.getEmail());
        dto.setAddress(staff.getAddress());
        if (staff.getAccount() != null) {
            dto.setAccountId(staff.getAccount().getAccountId());
            dto.setUsername(staff.getAccount().getUsername());
            dto.setIsActive(staff.getAccount().getIsActive());
        }
        return dto;
    }
}
