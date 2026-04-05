package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.example.salesmis.model.dto.StaffDTO;

public class StaffFormDialog extends JDialog {
    private JTextField txtFullName, txtPhone, txtEmail, txtAddress, txtUsername;
    private JPasswordField txtPassword;
    private JCheckBox chkIsActive;
    private boolean isConfirmed = false;
    private StaffDTO staff;

    public StaffFormDialog(Frame parent, String title, StaffDTO initialData) {
        super(parent, title, true);
        this.staff = initialData == null ? new StaffDTO() : initialData;
        initComponents(initialData != null && initialData.getStaffId() != null);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(boolean isEditMode) {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtFullName = new JTextField(20);
        txtPhone = new JTextField(20);
        txtEmail = new JTextField(20);
        txtAddress = new JTextField(20);
        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);
        chkIsActive = new JCheckBox("Hoạt động");

        int row = 0;
        addFormField(formPanel, "Họ và tên:", txtFullName, gbc, row++);
        addFormField(formPanel, "Số điện thoại:", txtPhone, gbc, row++);
        addFormField(formPanel, "Email:", txtEmail, gbc, row++);
        addFormField(formPanel, "Địa chỉ:", txtAddress, gbc, row++);
        
        addFormField(formPanel, "Tên đăng nhập:", txtUsername, gbc, row++);
        addFormField(formPanel, "Mật khẩu:", txtPassword, gbc, row++);

        gbc.gridx = 1; gbc.gridy = row++;
        formPanel.add(chkIsActive, gbc);

        // Populate fields if edit mode
        if (isEditMode) {
            txtFullName.setText(staff.getFullName());
            txtPhone.setText(staff.getPhone());
            txtEmail.setText(staff.getEmail());
            txtAddress.setText(staff.getAddress());
            
            txtUsername.setText(staff.getUsername());
            txtUsername.setEnabled(false); // Username is not editable
            
            chkIsActive.setSelected(staff.getIsActive() != null ? staff.getIsActive() : false);
        } else {
            chkIsActive.setSelected(true);
        }

        JPanel btnPanel = new JPanel();
        JButton btnSave = new JButton("Lưu");
        JButton btnCancel = new JButton("Hủy");

        btnSave.addActionListener(this::handleSave);
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, String label, java.awt.Component field, GridBagConstraints gbc, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private void handleSave(ActionEvent e) {
        if (txtFullName.getText().trim().isEmpty() || txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên và Tên đăng nhập không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (staff.getStaffId() == null && new String(txtPassword.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mật khẩu cho nhân viên mới", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        staff.setFullName(txtFullName.getText().trim());
        staff.setPhone(txtPhone.getText().trim());
        staff.setEmail(txtEmail.getText().trim());
        staff.setAddress(txtAddress.getText().trim());
        staff.setUsername(txtUsername.getText().trim());
        staff.setIsActive(chkIsActive.isSelected());
        
        String pwd = new String(txtPassword.getPassword()).trim();
        if (!pwd.isEmpty()) {
            staff.setPassword(pwd);
        }

        isConfirmed = true;
        dispose();
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public StaffDTO getStaffData() {
        return staff;
    }
}
