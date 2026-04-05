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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.StaffController;
import com.example.salesmis.model.dto.StaffDTO;

public class StaffManagementPanel extends JPanel {
    private final StaffController staffController;
    private JTable tableStaff;
    private DefaultTableModel tableModel;
    private List<StaffDTO> currentStaffList;

    public StaffManagementPanel(StaffController staffController) {
        this.staffController = staffController;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // TOP TOOLBAR
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm nhân viên");
        JButton btnEdit = new JButton("Sửa thông tin");
        JButton btnLock = new JButton("Khóa/Mở Khóa tài khoản");
        JButton btnRefresh = new JButton("Làm mới");

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnLock.addActionListener(e -> toggleLockStaff());
        btnRefresh.addActionListener(e -> loadData());

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnLock);
        toolbar.add(btnRefresh);

        // TABLE
        String[] columnNames = {"ID", "Tên đăng nhập", "Họ và tên", "SĐT", "Email", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableStaff = new JTable(tableModel);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(tableStaff), BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            currentStaffList = staffController.getStaffList();
            tableModel.setRowCount(0); // clear
            for (StaffDTO dto : currentStaffList) {
                tableModel.addRow(new Object[]{
                    dto.getStaffId(),
                    dto.getUsername(),
                    dto.getFullName(),
                    dto.getPhone(),
                    dto.getEmail(),
                    (dto.getIsActive() != null && dto.getIsActive()) ? "Hoạt động" : "Bị khóa"
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        Window parentWindow = SwingUtilities.windowForComponent(this);
        StaffFormDialog dialog = new StaffFormDialog((java.awt.Frame) parentWindow, "Thêm nhân viên mới", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                staffController.addStaff(dialog.getStaffData());
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = tableStaff.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên để sửa!");
            return;
        }

        StaffDTO selectedStaff = currentStaffList.get(row);
        Window parentWindow = SwingUtilities.windowForComponent(this);
        StaffFormDialog dialog = new StaffFormDialog((java.awt.Frame) parentWindow, "Cập nhật nhân viên", selectedStaff);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                staffController.updateStaff(selectedStaff.getStaffId(), dialog.getStaffData());
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void toggleLockStaff() {
        int row = tableStaff.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhân viên!");
            return;
        }

        StaffDTO selectedStaff = currentStaffList.get(row);
        boolean currentActive = selectedStaff.getIsActive() != null && selectedStaff.getIsActive();
        boolean newActive = !currentActive; // Toggle
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn " + (newActive ? "mở khóa" : "khóa") + " nhân viên này không?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                selectedStaff.setIsActive(newActive); // update DTO
                staffController.updateStaff(selectedStaff.getStaffId(), selectedStaff);
                JOptionPane.showMessageDialog(this, "Thay đổi trạng thái thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
