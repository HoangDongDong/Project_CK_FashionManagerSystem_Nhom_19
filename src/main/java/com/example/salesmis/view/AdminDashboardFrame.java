package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import javax.swing.JButton;
import java.awt.FlowLayout;

import com.example.salesmis.controller.AuthController;
import com.example.salesmis.controller.OrderController;
import com.example.salesmis.controller.ProductController;
import com.example.salesmis.controller.VoucherController;
import com.example.salesmis.controller.CustomerController;
import com.example.salesmis.controller.DashboardController;
import com.example.salesmis.controller.ImportController;
import com.example.salesmis.model.entity.Account;

public class AdminDashboardFrame extends JFrame {
    public AdminDashboardFrame(Account account, AuthController authController, OrderController orderController,
            ProductController productController, VoucherController voucherController,
            com.example.salesmis.controller.StaffController staffController,
            com.example.salesmis.controller.ReportController reportController, com.example.salesmis.controller.CustomerController customerController,
            com.example.salesmis.controller.DashboardController dashboardController,
            com.example.salesmis.controller.ImportController importController,
            com.example.salesmis.controller.CategoryController categoryController) {
        
        super("Fashion Shop - Admin Dashboard");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 26));
        title.setForeground(java.awt.Color.decode("#C28E61"));

        JLabel welcome = new JLabel("Xin chào, " + account.getUsername(), SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setBackground(java.awt.Color.WHITE);
        btnLogout.setForeground(java.awt.Color.decode("#C28E61"));
        btnLogout.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnLogout.addActionListener(e -> {
            authController.logout(account.getUsername());
            dispose();
            LoginFrame.show(authController, orderController, productController, voucherController, staffController, reportController, customerController, dashboardController, importController, categoryController);
        });

        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        welcomePanel.setBackground(java.awt.Color.WHITE);
        welcomePanel.add(welcome);
        welcomePanel.add(btnLogout);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(java.awt.Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.decode("#E0E0E0")),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        title.setHorizontalAlignment(SwingConstants.LEFT);
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(welcomePanel, BorderLayout.EAST);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addTab("Tong quan", new AdminDashboardPanel(dashboardController));
        tabbedPane.addTab("Lap hoa don", new InvoicePanel(account, orderController, productController, voucherController, customerController));
        
        if (customerController != null) {
            tabbedPane.addTab("Khach hang", new CustomerManagementPanel(customerController));
        }
        if (staffController != null) {
            tabbedPane.addTab("Quan ly Nhan vien", new StaffManagementPanel(staffController));
        }
        if (productController != null) {
            tabbedPane.addTab("Quan ly San pham", new ProductManagementPanel(productController, categoryController));
        }
        if (categoryController != null) {
            tabbedPane.addTab("Quan ly Danh muc", new CategoryManagementPanel(categoryController));
        }
        if (voucherController != null) {
            tabbedPane.addTab("Quan ly Ma Khuyen Mai", new VoucherManagementPanel(voucherController));
        }
        if (reportController != null) {
            tabbedPane.addTab("Bao cao Doanh thu", new ReportPanel(reportController, staffController));
        }
        if (importController != null) {
            tabbedPane.addTab("Nhap hang", new ImportPanel(importController));
        }
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
}
