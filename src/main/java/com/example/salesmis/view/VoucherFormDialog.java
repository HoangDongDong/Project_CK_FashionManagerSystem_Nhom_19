package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.example.salesmis.model.dto.VoucherDTO;

public class VoucherFormDialog extends JDialog {
    private JTextField txtCode, txtDiscountValue, txtStartDate, txtEndDate, txtUsageLimit, txtMinOrder;
    private JComboBox<String> cbDiscountType;
    private boolean isConfirmed = false;
    private final VoucherDTO voucher;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VoucherFormDialog(Frame parent, String title, VoucherDTO initialData) {
        super(parent, title, true);
        this.voucher = initialData == null ? new VoucherDTO() : initialData;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCode = new JTextField(20);
        txtDiscountValue = new JTextField(20);
        
        cbDiscountType = new JComboBox<>(new String[]{"PERCENTAGE", "FIXED_AMOUNT"});
        
        txtStartDate = new JTextField(20);
        txtStartDate.setToolTipText("yyyy-MM-dd HH:mm:ss");
        
        txtEndDate = new JTextField(20);
        txtEndDate.setToolTipText("yyyy-MM-dd HH:mm:ss");
        
        txtUsageLimit = new JTextField(20);
        txtMinOrder = new JTextField(20);

        int row = 0;
        addFormField(formPanel, "Mã Voucher:", txtCode, gbc, row++);
        addFormField(formPanel, "Mức giảm:", txtDiscountValue, gbc, row++);
        addFormField(formPanel, "Loại giảm:", cbDiscountType, gbc, row++);
        addFormField(formPanel, "Bắt đầu (yyyy-MM-dd HH:mm:ss):", txtStartDate, gbc, row++);
        addFormField(formPanel, "Kết thúc (yyyy-MM-dd HH:mm:ss):", txtEndDate, gbc, row++);
        addFormField(formPanel, "Giới hạn sử dụng:", txtUsageLimit, gbc, row++);
        addFormField(formPanel, "Đơn tối thiểu:", txtMinOrder, gbc, row++);

        // Populate fields
        if (voucher.getVoucherId() != null) {
            txtCode.setText(voucher.getCode());
            txtDiscountValue.setText(voucher.getDiscountValue() != null ? voucher.getDiscountValue().toString() : "0");
            cbDiscountType.setSelectedItem(voucher.getDiscountType() != null && voucher.getDiscountType().contains("PERCENT") ? "PERCENTAGE" : "FIXED_AMOUNT");
            txtStartDate.setText(voucher.getStartDate() != null ? voucher.getStartDate().format(FORMATTER) : "");
            txtEndDate.setText(voucher.getEndDate() != null ? voucher.getEndDate().format(FORMATTER) : "");
            txtUsageLimit.setText(voucher.getUsageLimit() != null ? String.valueOf(voucher.getUsageLimit()) : "");
            txtMinOrder.setText(voucher.getMinOrderValue() != null ? voucher.getMinOrderValue().toString() : "0");
        } else {
            LocalDateTime now = LocalDateTime.now();
            txtStartDate.setText(now.format(FORMATTER));
            txtEndDate.setText(now.plusMonths(1).format(FORMATTER));
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
        if (txtCode.getText().trim().isEmpty() || txtDiscountValue.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã Voucher và Mức giảm không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            voucher.setCode(txtCode.getText().trim().toUpperCase());
            voucher.setDiscountValue(new BigDecimal(txtDiscountValue.getText().trim()));
            voucher.setDiscountType((String) cbDiscountType.getSelectedItem());

            String startStr = txtStartDate.getText().trim();
            if (!startStr.isEmpty()) voucher.setStartDate(LocalDateTime.parse(startStr, FORMATTER));
            else voucher.setStartDate(null);

            String endStr = txtEndDate.getText().trim();
            if (!endStr.isEmpty()) voucher.setEndDate(LocalDateTime.parse(endStr, FORMATTER));
            else voucher.setEndDate(null);

            String limitStr = txtUsageLimit.getText().trim();
            if (!limitStr.isEmpty()) voucher.setUsageLimit(Integer.parseInt(limitStr));
            else voucher.setUsageLimit(null);

            String minStr = txtMinOrder.getText().trim();
            if (!minStr.isEmpty()) voucher.setMinOrderValue(new BigDecimal(minStr));
            else voucher.setMinOrderValue(BigDecimal.ZERO);

            isConfirmed = true;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá trị số (Mức giảm, giới hạn, đơn tối thiểu) không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày giờ phải là yyyy-MM-dd HH:mm:ss", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() { return isConfirmed; }
    public VoucherDTO getVoucherData() { return voucher; }
}
