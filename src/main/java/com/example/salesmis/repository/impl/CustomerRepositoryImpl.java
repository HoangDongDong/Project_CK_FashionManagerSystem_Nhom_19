package com.example.salesmis.repository.impl;

import java.util.List;

import com.example.salesmis.model.entity.Customer;
import com.example.salesmis.repository.AbstractJpaRepository;
import com.example.salesmis.repository.CustomerRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class CustomerRepositoryImpl extends AbstractJpaRepository<Customer, Integer> implements CustomerRepository {

    public CustomerRepositoryImpl() {
        super(Customer.class);
    }

    @Override
    public List<Customer> findAll(EntityManager em) {
        String jpql = "SELECT c FROM Customer c ORDER BY c.customerName ASC";
        return em.createQuery(jpql, Customer.class).getResultList();
    }

    @Override
    public List<Customer> search(EntityManager em, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll(em);
        }
        String jpql = "SELECT c FROM Customer c WHERE LOWER(c.phone) LIKE :kw OR LOWER(c.customerName) LIKE :kw ORDER BY c.customerName ASC";
        return em.createQuery(jpql, Customer.class)
                 .setParameter("kw", "%" + keyword.trim().toLowerCase() + "%")
                 .getResultList();
    }

    @Override
    public boolean phoneExists(EntityManager em, String phone, Integer excludeCustomerId) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String jpql = "SELECT COUNT(c) FROM Customer c WHERE c.phone = :phone";
        if (excludeCustomerId != null) {
            jpql += " AND c.customerId <> :excludeId";
        }
        TypedQuery<Long> query = em.createQuery(jpql, Long.class)
                                   .setParameter("phone", phone.trim());
        if (excludeCustomerId != null) {
            query.setParameter("excludeId", excludeCustomerId);
        }
        return query.getSingleResult() > 0;
    }

    @Override
    public Long getTotalCustomers(EntityManager em, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {
        String jpql = "SELECT COUNT(c) FROM Customer c WHERE 1=1";
        if (startDate != null && endDate != null) {
            jpql += " AND c.createdAt >= :start AND c.createdAt <= :end";
        }
        TypedQuery<Long> query = em.createQuery(jpql, Long.class);
        if (startDate != null && endDate != null) {
            query.setParameter("start", startDate);
            query.setParameter("end", endDate);
        }
        return query.getSingleResult();
    }
}
