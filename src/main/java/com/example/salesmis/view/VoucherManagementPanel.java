package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.VoucherController;
import com.example.salesmis.model.dto.VoucherDTO;

public class VoucherManagementPanel extends JPanel {
    private final VoucherController voucherController;
    private JTable tableVoucher;
    private DefaultTableModel tableModel;
    private List<VoucherDTO> currentList;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public VoucherManagementPanel(VoucherController voucherController) {
        this.voucherController = voucherController;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // TOOLBAR
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Thêm Voucher");
        JButton btnEdit = new JButton("Sửa thông tin");
        JButton btnLock = new JButton("Khóa Voucher");
        JButton btnRefresh = new JButton("Làm mới");

        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnLock.addActionListener(e -> disableVoucher());
        btnRefresh.addActionListener(e -> loadData());

        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnLock);
        toolbar.add(btnRefresh);

        // TABLE
        String[] columns = {"ID", "Mã code", "Mức giảm", "Điều kiện", "Hiệu lực từ", "Hết hạn", "Giới hạn", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableVoucher = new JTable(tableModel);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(tableVoucher), BorderLayout.CENTER);
    }

    private void loadData() {
        try {
            currentList = voucherController.getAllVouchers();
            tableModel.setRowCount(0);

            for (VoucherDTO v : currentList) {
                String val = v.getDiscountValue() != null ? v.getDiscountValue().stripTrailingZeros().toPlainString() : "0";
                val += "PERCENTAGE".equalsIgnoreCase(v.getDiscountType()) ? "%" : " đ";
                
                String minOrder = v.getMinOrderValue() != null ? "Đơn >= " + v.getMinOrderValue().stripTrailingZeros().toPlainString() : "Mọi đơn";
                String start = v.getStartDate() != null ? v.getStartDate().format(FORMATTER) : "-";
                String end = v.getEndDate() != null ? v.getEndDate().format(FORMATTER) : "-";
                
                String limit = v.getUsageLimit() != null ? String.valueOf(v.getUsageLimit()) : "Không giới hạn";

                tableModel.addRow(new Object[]{
                    v.getVoucherId(),
                    v.getCode(),
                    val,
                    minOrder,
                    start,
                    end,
                    limit,
                    v.getStatus()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        Window parentWin = SwingUtilities.windowForComponent(this);
        VoucherFormDialog dialog = new VoucherFormDialog((java.awt.Frame) parentWin, "Thêm Mã Voucher Mới", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                voucherController.createVoucher(dialog.getVoucherData());
                JOptionPane.showMessageDialog(this, "Thêm Voucher thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditDialog() {
        int row = tableVoucher.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 mã Voucher để sửa!");
            return;
        }

        VoucherDTO selected = currentList.get(row);
        Window parentWin = SwingUtilities.windowForComponent(this);
        VoucherFormDialog dialog = new VoucherFormDialog((java.awt.Frame) parentWin, "Sửa thông tin Voucher", selected);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                voucherController.updateVoucher(selected.getVoucherId(), dialog.getVoucherData());
                JOptionPane.showMessageDialog(this, "Cập nhật Voucher thành công!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void disableVoucher() {
        int row = tableVoucher.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Voucher muốn khóa!");
            return;
        }

        VoucherDTO selected = currentList.get(row);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắn chắn muốn khóa Voucher: " + selected.getCode() + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                voucherController.disableVoucher(selected.getVoucherId());
                JOptionPane.showMessageDialog(this, "Voucher đã bị vô hiệu hóa!");
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
