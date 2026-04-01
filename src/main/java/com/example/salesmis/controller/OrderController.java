package com.example.salesmis.controller;

import com.example.salesmis.service.OrderService;

public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void createOrder() {
        orderService.placeOrder();
    }
}

