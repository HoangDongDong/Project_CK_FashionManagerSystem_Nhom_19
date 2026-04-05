package com.example.salesmis.repository;

import java.util.List;

import com.example.salesmis.model.entity.Customer;

import jakarta.persistence.EntityManager;

public interface CustomerRepository extends GenericRepository<Customer, Integer> {
    List<Customer> findAll(EntityManager em);
    List<Customer> search(EntityManager em, String keyword);
    boolean phoneExists(EntityManager em, String phone, Integer excludeCustomerId);
    Long getTotalCustomers(EntityManager em, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate);
}
