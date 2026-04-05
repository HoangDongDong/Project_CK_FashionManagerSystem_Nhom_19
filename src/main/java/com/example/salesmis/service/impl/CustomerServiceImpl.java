package com.example.salesmis.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.model.dto.CustomerDTO;
import com.example.salesmis.model.entity.Customer;
import com.example.salesmis.repository.CustomerRepository;
import com.example.salesmis.service.CustomerService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final EntityManagerProvider entityManagerProvider;

    public CustomerServiceImpl(CustomerRepository customerRepository, EntityManagerProvider entityManagerProvider) {
        this.customerRepository = customerRepository;
        this.entityManagerProvider = entityManagerProvider;
    }

    private CustomerDTO mapToDTO(Customer entity) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(entity.getCustomerId());
        dto.setCustomerName(entity.getCustomerName());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setLoyaltyPoints(entity.getLoyaltyPoints());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }

    @Override
    public List<CustomerDTO> getCustomers(String keyword) {
        EntityManager em = entityManagerProvider.createEntityManager();
        try {
            List<Customer> customers = customerRepository.search(em, keyword);
            return customers.stream().map(this::mapToDTO).collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    @Override
    public CustomerDTO addCustomer(CustomerDTO dto) {
        EntityManager em = entityManagerProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (customerRepository.phoneExists(em, dto.getPhone(), null)) {
                throw new RuntimeException("So dien thoai da ton tai.");
            }

            Customer entity = new Customer();
            entity.setCustomerName(dto.getCustomerName());
            entity.setPhone(dto.getPhone());
            entity.setEmail(dto.getEmail());
            entity.setAddress(dto.getAddress());
            entity.setLoyaltyPoints(0);
            entity.setIsActive(true);
            entity.setCreatedAt(LocalDateTime.now());

            customerRepository.save(em, entity);

            tx.commit();
            return mapToDTO(entity);
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public CustomerDTO editCustomer(Integer customerId, CustomerDTO dto) {
        EntityManager em = entityManagerProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Customer entity = customerRepository.findById(em, customerId)
                    .orElseThrow(() -> new RuntimeException("Khong tim thay khach hang ID: " + customerId));

            if (customerRepository.phoneExists(em, dto.getPhone(), customerId)) {
                throw new RuntimeException("So dien thoai d ton tai tren mot khach hang khac.");
            }

            entity.setCustomerName(dto.getCustomerName());
            entity.setPhone(dto.getPhone());
            entity.setEmail(dto.getEmail());
            entity.setAddress(dto.getAddress());
            // Loyalty points update not exposed here

            customerRepository.save(em, entity);

            tx.commit();
            return mapToDTO(entity);
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    @Override
    public void setCustomerInactive(Integer customerId) {
        EntityManager em = entityManagerProvider.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Customer entity = customerRepository.findById(em, customerId)
                    .orElseThrow(() -> new RuntimeException("Khong tim thay khach hang ID: " + customerId));

            entity.setIsActive(false);
            customerRepository.save(em, entity);
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }
}
