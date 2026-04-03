package com.example.salesmis;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.config.JpaEntityManagerProvider;
import com.example.salesmis.controller.AuthController;
import com.example.salesmis.controller.OrderController;
import com.example.salesmis.controller.ProductController;
import com.example.salesmis.repository.AccountRepository;
import com.example.salesmis.repository.InvoiceRepository;
import com.example.salesmis.repository.ProductRepository;
import com.example.salesmis.repository.impl.AccountRepositoryImpl;
import com.example.salesmis.repository.impl.InvoiceRepositoryImpl;
import com.example.salesmis.repository.impl.ProductRepositoryImpl;
import com.example.salesmis.service.AuthService;
import com.example.salesmis.service.OrderService;
import com.example.salesmis.service.ProductService;
import com.example.salesmis.service.impl.AuthServiceImpl;
import com.example.salesmis.service.impl.OrderServiceImpl;
import com.example.salesmis.service.impl.ProductServiceImpl;
import com.example.salesmis.view.LoginFrame;

public class AppLauncher {
    public static void main(String[] args) {
        EntityManagerProvider entityManagerProvider = new JpaEntityManagerProvider();

        AccountRepository accountRepository = new AccountRepositoryImpl();
        ProductRepository productRepository = new ProductRepositoryImpl();
        InvoiceRepository invoiceRepository = new InvoiceRepositoryImpl();

        AuthService authService = new AuthServiceImpl(accountRepository, entityManagerProvider);
        OrderService orderService = new OrderServiceImpl(productRepository, invoiceRepository, entityManagerProvider);
        ProductService productService = new ProductServiceImpl(productRepository, entityManagerProvider);

        AuthController authController = new AuthController(authService);
        OrderController orderController = new OrderController(orderService);
        ProductController productController = new ProductController(productService);

        LoginFrame.show(authController, orderController, productController);
    }
}


