package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.example.salesmis.controller.OrderController;
import com.example.salesmis.controller.ProductController;
import com.example.salesmis.controller.VoucherController;
import com.example.salesmis.model.entity.Account;

public class AdminDashboardFrame extends JFrame {
    public AdminDashboardFrame(Account account, OrderController orderController, ProductController productController,
            VoucherController voucherController) {
        setTitle("Fashion Shop - Admin Dashboard");
        setSize(960, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel title = new JLabel("ADMIN DASHBOARD", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel welcome = new JLabel("Xin chao, " + account.getUsername(), SwingConstants.CENTER);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));

        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBorder(BorderFactory.createEmptyBorder(60, 20, 60, 20));
        homePanel.add(title, BorderLayout.CENTER);
        homePanel.add(welcome, BorderLayout.SOUTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tong quan", homePanel);
        tabbedPane.addTab("Lap hoa don", new InvoicePanel(orderController, productController, voucherController));
        add(tabbedPane);
    }
}
