package com.example.salesmis;

import com.example.salesmis.config.EntityManagerProvider;
import com.example.salesmis.config.JpaEntityManagerProvider;
import com.example.salesmis.controller.AuthController;
import com.example.salesmis.controller.OrderController;
import com.example.salesmis.controller.ProductController;
import com.example.salesmis.controller.VoucherController;
import com.example.salesmis.repository.AccountRepository;
import com.example.salesmis.repository.InvoiceRepository;
import com.example.salesmis.repository.ProductRepository;
import com.example.salesmis.repository.VoucherRepository;
import com.example.salesmis.repository.impl.AccountRepositoryImpl;
import com.example.salesmis.repository.impl.InvoiceRepositoryImpl;
import com.example.salesmis.repository.impl.ProductRepositoryImpl;
import com.example.salesmis.repository.impl.VoucherRepositoryImpl;
import com.example.salesmis.service.AuthService;
import com.example.salesmis.service.OrderService;
import com.example.salesmis.service.ProductService;
import com.example.salesmis.service.VoucherService;
import com.example.salesmis.service.impl.AuthServiceImpl;
import com.example.salesmis.service.impl.OrderServiceImpl;
import com.example.salesmis.service.impl.ProductServiceImpl;
import com.example.salesmis.service.impl.VoucherServiceImpl;
import com.example.salesmis.repository.CustomerRepository;
import com.example.salesmis.repository.impl.CustomerRepositoryImpl;
import com.example.salesmis.service.CustomerService;
import com.example.salesmis.service.impl.CustomerServiceImpl;
import com.example.salesmis.service.DashboardService;
import com.example.salesmis.service.impl.DashboardServiceImpl;
import com.example.salesmis.controller.CustomerController;
import com.example.salesmis.controller.DashboardController;
import com.example.salesmis.view.LoginFrame;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public class AppLauncher {
    public static void main(String[] args) {
        setupTheme();
        EntityManagerProvider entityManagerProvider = new JpaEntityManagerProvider();

        AccountRepository accountRepository = new AccountRepositoryImpl();
        ProductRepository productRepository = new ProductRepositoryImpl();
        InvoiceRepository invoiceRepository = new InvoiceRepositoryImpl();
        VoucherRepository voucherRepository = new VoucherRepositoryImpl();

        AuthService authService = new AuthServiceImpl(accountRepository, entityManagerProvider);
        VoucherService voucherService = new VoucherServiceImpl(voucherRepository, entityManagerProvider);
        OrderService orderService = new OrderServiceImpl(productRepository, invoiceRepository, voucherService, entityManagerProvider);
        ProductService productService = new ProductServiceImpl(productRepository, entityManagerProvider);
        
        com.example.salesmis.repository.StaffRepository staffRepository = new com.example.salesmis.repository.impl.StaffRepositoryImpl();
        com.example.salesmis.service.StaffService staffService = new com.example.salesmis.service.impl.StaffServiceImpl(staffRepository, accountRepository, entityManagerProvider);
        
        com.example.salesmis.repository.ImportRepository importRepository = new com.example.salesmis.repository.impl.ImportRepositoryImpl();
        com.example.salesmis.repository.InventoryRepository inventoryRepository = new com.example.salesmis.repository.impl.InventoryRepositoryImpl();
        com.example.salesmis.service.ImportService importService = new com.example.salesmis.service.impl.ImportServiceImpl(importRepository, inventoryRepository, entityManagerProvider);
        com.example.salesmis.controller.ImportController importController = new com.example.salesmis.controller.ImportController(importService);
        
        com.example.salesmis.service.ReportService reportService = new com.example.salesmis.service.impl.ReportServiceImpl(invoiceRepository, importRepository, entityManagerProvider);

        CustomerRepository customerRepository = new CustomerRepositoryImpl();
        CustomerService customerService = new CustomerServiceImpl(customerRepository, entityManagerProvider);

        com.example.salesmis.repository.CategoryRepository categoryRepository = new com.example.salesmis.repository.impl.CategoryRepositoryImpl();
        com.example.salesmis.service.CategoryService categoryService = new com.example.salesmis.service.impl.CategoryServiceImpl(categoryRepository, productRepository, entityManagerProvider);

        DashboardService dashboardService = new DashboardServiceImpl(invoiceRepository, customerRepository, productRepository, voucherRepository, importRepository, entityManagerProvider);

        AuthController authController = new AuthController(authService);
        OrderController orderController = new OrderController(orderService);
        ProductController productController = new ProductController(productService);
        VoucherController voucherController = new VoucherController(voucherService);
        
        com.example.salesmis.controller.StaffController staffController = new com.example.salesmis.controller.StaffController(staffService);
        com.example.salesmis.controller.ReportController reportController = new com.example.salesmis.controller.ReportController(reportService);
        CustomerController customerController = new CustomerController(customerService);
        DashboardController dashboardController = new DashboardController(dashboardService);
        com.example.salesmis.controller.CategoryController categoryController = new com.example.salesmis.controller.CategoryController(categoryService);

        LoginFrame.show(authController, orderController, productController, voucherController, staffController, reportController, customerController, dashboardController, importController, categoryController);
    }

    private static void setupTheme() {
        try {
            FlatLightLaf.setup();
            
            // Primary: #C28E61, Secondary: #2C2C2C, Tertiary: #6FA0B4, Background: #F5F5F5
            UIManager.put("Component.accentColor", Color.decode("#C28E61"));
            UIManager.put("Button.background", Color.decode("#C28E61"));
            UIManager.put("Button.foreground", Color.white);
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Panel.background", Color.decode("#F5F5F5"));
            
            // Fonts
            Font defaultFont = new Font("SansSerif", Font.PLAIN, 14);
            UIManager.put("defaultFont", defaultFont);
        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf");
        }
    }
}


