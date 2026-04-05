package com.example.salesmis.repository.impl;

import java.util.List;

import com.example.salesmis.model.entity.Staff;
import com.example.salesmis.repository.AbstractJpaRepository;
import com.example.salesmis.repository.StaffRepository;

import jakarta.persistence.EntityManager;

public class StaffRepositoryImpl extends AbstractJpaRepository<Staff, Integer> implements StaffRepository {
    public StaffRepositoryImpl() {
        super(Staff.class);
    }

    @Override
    public List<Staff> findAllStaff(EntityManager entityManager) {
        return entityManager.createQuery("SELECT s FROM Staff s LEFT JOIN FETCH s.account", Staff.class)
                .getResultList();
    }
}
