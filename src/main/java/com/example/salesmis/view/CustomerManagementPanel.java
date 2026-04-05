package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.CustomerController;
import com.example.salesmis.model.dto.CustomerDTO;

public class CustomerManagementPanel extends JPanel {
    private final CustomerController customerController;
    private JTable tableCustomer;
    private DefaultTableModel tableModel;
    private List<CustomerDTO> currentList;
    private JTextField txtSearch;

    public CustomerManagementPanel(CustomerController customerController) {
        this.customerController = customerController;
        initComponents();
        loadData("");
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // TOP TOOLBAR
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Tìm");
        JButton btnAdd = new JButton("Thêm KH mới");
        JButton btnEdit = new JButton("Sửa thông tin");
        JButton btnDelete = new JButton("Cấm Mua (Khóa)");

        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> banCustomer());

        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);

        // TABLE
        String[] columns = {"ID", "Tên KH", "SĐT", "Email", "Địa chỉ", "Điểm", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableCustomer = new JTable(tableModel);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(tableCustomer), BorderLayout.CENTER);
    }

    private void loadData(String keyword) {
        try {
            currentList = customerController.searchCustomers(keyword);
            tableModel.setRowCount(0);
            for (CustomerDTO dto : currentList) {
                String status = Boolean.TRUE.equals(dto.getIsActive()) ? "Hoạt động" : "Bị khóa";
                tableModel.addRow(new Object[]{
                    dto.getCustomerId(),
                    dto.getCustomerName(),
                    dto.getPhone(),
                    dto.getEmail(),
                    dto.getAddress(),
                    dto.getLoyaltyPoints(),
                    status
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm KH: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        Window parentWin = SwingUtilities.windowForComponent(this);
        CustomerFormDialog dialog = new CustomerFormDialog((java.awt.Frame) parentWin, "Thêm Khách Hàng", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                customerController.createCustomer(dialog.getCustomerData());
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
                loadData("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi thêm KH: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = tableCustomer.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khách hàng!");
            return;
        }

        CustomerDTO selected = currentList.get(row);
        
        Window parentWin = SwingUtilities.windowForComponent(this);
        CustomerFormDialog dialog = new CustomerFormDialog((java.awt.Frame) parentWin, "Cập nhật Khách Hàng", selected);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                customerController.updateCustomer(selected.getCustomerId(), dialog.getCustomerData());
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void banCustomer() {
        int row = tableCustomer.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần khóa!");
            return;
        }

        CustomerDTO selected = currentList.get(row);
        int answer = JOptionPane.showConfirmDialog(this, "Bạn có chắn chắn đưa khách hàng này vào Blacklist?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (answer == JOptionPane.YES_OPTION) {
            try {
                customerController.banCustomer(selected.getCustomerId());
                JOptionPane.showMessageDialog(this, "Khách hàng đã bị đưa vào Blacklist!");
                loadData("");
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
