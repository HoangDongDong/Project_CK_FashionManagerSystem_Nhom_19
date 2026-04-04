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
import com.example.salesmis.model.entity.Account;
import com.example.salesmis.service.exception.AuthenticationException;

public class LoginFrame extends JFrame {
    private final transient AuthController authController;
    private final transient OrderController orderController;
    private final transient ProductController productController;
    private final transient VoucherController voucherController;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JButton loginButton;

    public LoginFrame(AuthController authController, OrderController orderController, ProductController productController,
            VoucherController voucherController) {
        this.authController = authController;
        this.orderController = orderController;
        this.productController = productController;
        this.voucherController = voucherController;

        setTitle("Fashion Shop - Login");
        setSize(420, 220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Dang nhap");

        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);

        JPanel actionPanel = new JPanel();
        actionPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));
        actionPanel.add(loginButton);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

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
            dashboard = new AdminDashboardFrame(account, orderController, productController, voucherController);
        } else {
            dashboard = new StaffDashboardFrame(account, orderController, productController, voucherController);
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
            VoucherController voucherController) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame(authController, orderController, productController, voucherController);
            frame.setVisible(true);
        });
    }
}
