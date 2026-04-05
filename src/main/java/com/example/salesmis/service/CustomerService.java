package com.example.salesmis.service;

import java.util.List;

import com.example.salesmis.model.dto.CustomerDTO;

public interface CustomerService {
    List<CustomerDTO> getCustomers(String keyword);
    CustomerDTO addCustomer(CustomerDTO dto);
    CustomerDTO editCustomer(Integer customerId, CustomerDTO dto);
    void setCustomerInactive(Integer customerId);
}
