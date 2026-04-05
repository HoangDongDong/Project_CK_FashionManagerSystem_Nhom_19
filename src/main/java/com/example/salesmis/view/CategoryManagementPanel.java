package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.CategoryController;
import com.example.salesmis.model.dto.CategoryDTO;
import com.example.salesmis.service.exception.CategoryNotEmptyException;
import com.example.salesmis.service.exception.DuplicateCategoryException;

public class CategoryManagementPanel extends JPanel {
    private final CategoryController categoryController;

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtId;
    private JTextField txtName;
    private JTextArea txtDesc;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    public CategoryManagementPanel(CategoryController categoryController) {
        this.categoryController = categoryController;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // LEFT: Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin Danh Mục"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(20);
        txtId.setEditable(false);
        txtName = new JTextField(20);
        txtDesc = new JTextArea(4, 20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Mã DM:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Tên DM (*):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtName, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(new JScrollPane(txtDesc), gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Cập Nhật");
        btnDelete = new JButton("Xóa");
        btnClear = new JButton("Làm Mới");

        btnAdd.addActionListener(e -> addCategory());
        btnUpdate.addActionListener(e -> updateCategory());
        btnDelete.addActionListener(e -> deleteCategory());
        btnClear.addActionListener(e -> clearForm());

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.WEST);

        // RIGHT: Table
        tableModel = new DefaultTableModel(new String[]{"Mã DM", "Tên Danh Mục", "Mô Tả"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::onRowSelect);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Danh sách"));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try {
            List<CategoryDTO> list = categoryController.getAllCategories();
            for (CategoryDTO dto : list) {
                tableModel.addRow(new Object[]{dto.getCategoryId(), dto.getCategoryName(), dto.getDescription()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRowSelect(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
            int row = table.getSelectedRow();
            txtId.setText(tableModel.getValueAt(row, 0).toString());
            txtName.setText(tableModel.getValueAt(row, 1).toString());
            txtDesc.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        }
    }

    private void clearForm() {
        table.clearSelection();
        txtId.setText("");
        txtName.setText("");
        txtDesc.setText("");
        txtName.requestFocus();
    }

    private void addCategory() {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        CategoryDTO dto = new CategoryDTO(null, txtName.getText().trim(), txtDesc.getText().trim());
        try {
            categoryController.createCategory(dto);
            JOptionPane.showMessageDialog(this, "Thêm danh mục thành công!");
            clearForm();
            loadData();
        } catch (DuplicateCategoryException ex) {
            JOptionPane.showMessageDialog(this, "Tên danh mục đã tồn tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCategory() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên danh mục không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Integer id = Integer.parseInt(txtId.getText());
        CategoryDTO dto = new CategoryDTO(null, txtName.getText().trim(), txtDesc.getText().trim());
        try {
            categoryController.updateCategory(id, dto);
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            clearForm();
            loadData();
        } catch (DuplicateCategoryException ex) {
            JOptionPane.showMessageDialog(this, "Tên danh mục đã tồn tại!", "Lỗi", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCategory() {
        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer id = Integer.parseInt(txtId.getText());
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa danh mục này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                categoryController.deleteCategory(id);
                JOptionPane.showMessageDialog(this, "Xóa danh mục thành công!");
                clearForm();
                loadData();
            } catch (CategoryNotEmptyException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Xóa thất bại", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
