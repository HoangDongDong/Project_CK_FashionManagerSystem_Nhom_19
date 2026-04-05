package com.example.salesmis.controller;

import java.util.List;

import com.example.salesmis.model.dto.CustomerDTO;
import com.example.salesmis.service.CustomerService;

public class CustomerController {
    
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    public List<CustomerDTO> getAllCustomers() {
        return customerService.getCustomers("");
    }

    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerService.getCustomers(keyword);
    }

    public CustomerDTO createCustomer(CustomerDTO dto) {
        return customerService.addCustomer(dto);
    }

    public CustomerDTO updateCustomer(Integer customerId, CustomerDTO dto) {
        return customerService.editCustomer(customerId, dto);
    }

    public void banCustomer(Integer customerId) {
        customerService.setCustomerInactive(customerId);
    }
}
