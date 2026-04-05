package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import jakarta.persistence.PersistenceException;

import com.example.salesmis.controller.AuthController;
import com.example.salesmis.controller.OrderController;
import com.example.salesmis.controller.ProductController;
import com.example.salesmis.controller.VoucherController;
import com.example.salesmis.controller.CustomerController;
import com.example.salesmis.controller.DashboardController;
import com.example.salesmis.model.entity.Account;
import com.example.salesmis.service.exception.AuthenticationException;

public class LoginFrame extends JFrame {
    private final transient AuthController authController;
    private final transient OrderController orderController;
    private final transient ProductController productController;
    private final transient VoucherController voucherController;
    private final transient com.example.salesmis.controller.StaffController staffController;
    private final transient com.example.salesmis.controller.ReportController reportController;
    private final transient CustomerController customerController;
    private final transient DashboardController dashboardController;
    private final transient com.example.salesmis.controller.ImportController importController;
    private final transient com.example.salesmis.controller.CategoryController categoryController;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginFrame(AuthController authController, OrderController orderController, ProductController productController,
            VoucherController voucherController, com.example.salesmis.controller.StaffController staffController,
            com.example.salesmis.controller.ReportController reportController, CustomerController customerController, DashboardController dashboardController, com.example.salesmis.controller.ImportController importController, com.example.salesmis.controller.CategoryController categoryController) {
        this.authController = authController;
        this.orderController = orderController;
        this.productController = productController;
        this.voucherController = voucherController;
        this.staffController = staffController;
        this.reportController = reportController;
        this.customerController = customerController;
        this.dashboardController = dashboardController;
        this.importController = importController;
        this.categoryController = categoryController;

        setTitle("Fashion Shop - Đăng Nhập");
        setSize(480, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        java.awt.Font h1Font = new java.awt.Font("Serif", java.awt.Font.BOLD, 24);
        
        JLabel titleLabel = new JLabel("FASHION SHOP LOGIN", javax.swing.SwingConstants.CENTER);
        titleLabel.setFont(h1Font);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        titleLabel.setForeground(java.awt.Color.decode("#C28E61"));

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));
        formPanel.setBackground(java.awt.Color.WHITE);

        usernameField = new JTextField();
        usernameField.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 16));
        
        passwordField = new JPasswordField();
        passwordField.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 16));
        
        loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 15));
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 15));

        formPanel.add(lblUser);
        formPanel.add(usernameField);
        formPanel.add(lblPass);
        formPanel.add(passwordField);

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(java.awt.Color.WHITE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        loginButton.setPreferredSize(new java.awt.Dimension(200, 40));
        actionPanel.add(loginButton);

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(java.awt.Color.WHITE);
        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(actionPanel, BorderLayout.SOUTH);

        setLayout(new java.awt.GridBagLayout()); // to center the cardPanel
        add(cardPanel);

        loginButton.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap day du username/password", "Thong bao",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Account account = authController.login(username, password);
            JOptionPane.showMessageDialog(this,
                    "Dang nhap thanh cong! Xin chao " + account.getUsername(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            openDashboardByRole(account);
        } catch (AuthenticationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Dang nhap that bai", JOptionPane.ERROR_MESSAGE);
        } catch (PersistenceException ex) {
            JOptionPane.showMessageDialog(this,
                    "Khong ket noi duoc database. Kiem tra MySQL dang chay, ten DB shop_management,\n"
                            + "va mat khau trong persistence.xml hoac -Djakarta.persistence.jdbc.password=...\n"
                            + "Chi tiet: " + rootMessage(ex),
                    "Loi ket noi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Loi he thong: " + rootMessage(ex), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openDashboardByRole(Account account) {
        String role = account.getRoleName() == null ? "" : account.getRoleName().trim().toLowerCase(Locale.ROOT);

        JFrame dashboard;
        if ("admin".equals(role)) {
            dashboard = new AdminDashboardFrame(account, authController, orderController, productController, voucherController, staffController, reportController, customerController, dashboardController, importController, categoryController);
        } else {
            dashboard = new StaffDashboardFrame(account, authController, orderController, productController, voucherController, staffController, reportController, customerController, dashboardController, importController, categoryController);
        }

        dashboard.setVisible(true);
        dispose();
    }

    private static String rootMessage(Throwable ex) {
        Throwable t = ex;
        while (t.getCause() != null && t.getCause() != t) {
            t = t.getCause();
        }
        String msg = t.getMessage();
        return msg != null ? msg : t.getClass().getSimpleName();
    }

    public static void show(AuthController authController, OrderController orderController, ProductController productController,
            VoucherController voucherController, com.example.salesmis.controller.StaffController staffController,
            com.example.salesmis.controller.ReportController reportController, CustomerController customerController, DashboardController dashboardController, com.example.salesmis.controller.ImportController importController, com.example.salesmis.controller.CategoryController categoryController) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame(authController, orderController, productController, voucherController, staffController, reportController, customerController, dashboardController, importController, categoryController);
            frame.setVisible(true);
        });
    }
}
