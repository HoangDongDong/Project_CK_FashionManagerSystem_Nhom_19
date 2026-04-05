package com.example.salesmis.repository;

import java.util.List;

import com.example.salesmis.model.entity.Staff;

import jakarta.persistence.EntityManager;

public interface StaffRepository extends GenericRepository<Staff, Integer> {
    List<Staff> findAllStaff(EntityManager entityManager);
}
