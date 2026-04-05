package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.example.salesmis.model.dto.CustomerDTO;

public class CustomerFormDialog extends JDialog {
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtAddress;

    private boolean confirmed = false;
    private CustomerDTO customerData;

    public CustomerFormDialog(Frame parent, String title, CustomerDTO existing) {
        super(parent, title, true);
        initComponents();
        if (existing != null) {
            populateForm(existing);
        }
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtName = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtAddress = new JTextField();

        formPanel.add(new JLabel("Tên khách hàng:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("SĐT:"));
        formPanel.add(txtPhone);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(txtEmail);
        formPanel.add(new JLabel("Địa chỉ:"));
        formPanel.add(txtAddress);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void populateForm(CustomerDTO existing) {
        txtName.setText(existing.getCustomerName());
        txtPhone.setText(existing.getPhone());
        txtEmail.setText(existing.getEmail());
        txtAddress.setText(existing.getAddress());
        this.customerData = existing;
    }

    private void handleSave() {
        if (txtName.getText().trim().isEmpty() || txtPhone.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên và SĐT không được để trống!");
            return;
        }

        if (customerData == null) {
            customerData = new CustomerDTO();
        }

        customerData.setCustomerName(txtName.getText().trim());
        customerData.setPhone(txtPhone.getText().trim());
        customerData.setEmail(txtEmail.getText().trim());
        customerData.setAddress(txtAddress.getText().trim());

        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public CustomerDTO getCustomerData() {
        return customerData;
    }
}
