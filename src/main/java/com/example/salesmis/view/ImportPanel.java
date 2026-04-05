package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.ImportController;
import com.example.salesmis.model.dto.ImportDetailDTO;
import com.example.salesmis.model.dto.ImportReceiptDTO;

public class ImportPanel extends JPanel {
    private final ImportController importController;

    private JTextField txtSupplierId;
    private JTextField txtStaffId;
    private JTextField txtNote;
    private JTextField txtProductId;
    private JTextField txtQuantity;
    private JTextField txtImportPrice;
    private JTextField txtMinStock;
    private JTextField txtMaxStock;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel lblTotalCost;

    private List<ImportDetailDTO> cartItems;

    public ImportPanel(ImportController importController) {
        this.importController = importController;
        this.cartItems = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Phân vùng Thông tin Phiếu Nhập & Thêm Sản Phẩm
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JPanel pnlReceiptInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlReceiptInfo.setBorder(BorderFactory.createTitledBorder("Thông tin Lô Hàng"));
        pnlReceiptInfo.add(new JLabel("Mã NCC:"));
        txtSupplierId = new JTextField(5);
        pnlReceiptInfo.add(txtSupplierId);
        
        pnlReceiptInfo.add(new JLabel("Mã NV:"));
        txtStaffId = new JTextField(5);
        pnlReceiptInfo.add(txtStaffId);

        pnlReceiptInfo.add(new JLabel("Ghi chú:"));
        txtNote = new JTextField(20);
        pnlReceiptInfo.add(txtNote);
        
        JPanel pnlAddItem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlAddItem.setBorder(BorderFactory.createTitledBorder("Thêm Sản Phẩm (Giỏ Nhập Hàng)"));
        pnlAddItem.add(new JLabel("Mã SP:"));
        txtProductId = new JTextField(5);
        pnlAddItem.add(txtProductId);
        
        pnlAddItem.add(new JLabel("Số lượng:"));
        txtQuantity = new JTextField(5);
        pnlAddItem.add(txtQuantity);
        
        pnlAddItem.add(new JLabel("Giá nhập:"));
        txtImportPrice = new JTextField(10);
        pnlAddItem.add(txtImportPrice);
        
        pnlAddItem.add(new JLabel("Min:"));
        txtMinStock = new JTextField("0", 4);
        pnlAddItem.add(txtMinStock);
        
        pnlAddItem.add(new JLabel("Max:"));
        txtMaxStock = new JTextField("9999", 5);
        pnlAddItem.add(txtMaxStock);
        
        JButton btnAdd = new JButton("Thêm vào giỏ");
        btnAdd.addActionListener(e -> addToCart());
        pnlAddItem.add(btnAdd);

        topPanel.add(pnlReceiptInfo);
        topPanel.add(pnlAddItem);

        add(topPanel, BorderLayout.NORTH);

        // 2. Bảng Danh sách sản phẩm mua tạm (Cart)
        String[] cols = {"Mã SP", "Số lượng", "Giá nhập", "Thành tiền"};
        cartTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết Lô Hàng"));
        centerPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);

        // 3. Nút "Save Lô Hàng" và Tổng tiền
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalCost = new JLabel("Tổng cộng: 0 ₫");
        lblTotalCost.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        lblTotalCost.setForeground(java.awt.Color.RED);
        bottomPanel.add(lblTotalCost);

        JButton btnSave = new JButton("Save Lô Hàng");
        btnSave.addActionListener(e -> saveReceipt());
        bottomPanel.add(btnSave);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addToCart() {
        try {
            Integer prodId = Integer.parseInt(txtProductId.getText().trim());
            int qty = Integer.parseInt(txtQuantity.getText().trim());
            BigDecimal price = new BigDecimal(txtImportPrice.getText().trim());
            int minStock = Integer.parseInt(txtMinStock.getText().trim());
            int maxStock = Integer.parseInt(txtMaxStock.getText().trim());

            if (qty <= 0 || price.compareTo(BigDecimal.ZERO) < 0 || minStock < 0 || maxStock <= minStock) {
                JOptionPane.showMessageDialog(this, "Số lượng, giá nhập, hoặc min/max tồn kho không hợp lệ!");
                return;
            }

            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(qty));

            ImportDetailDTO detailDTO = new ImportDetailDTO();
            detailDTO.setProductId(prodId);
            detailDTO.setQuantity(qty);
            detailDTO.setImportPrice(price);
            detailDTO.setSubtotal(subtotal);
            detailDTO.setMinStockLevel(minStock);
            detailDTO.setMaxStockLevel(maxStock);

            cartItems.add(detailDTO);
            updateCartTable();

            txtProductId.setText("");
            txtQuantity.setText("");
            txtImportPrice.setText("");
            txtMinStock.setText("0");
            txtMaxStock.setText("9999");
            txtProductId.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ.");
        }
    }

    private void updateCartTable() {
        String[] cols = {"Mã SP", "Số lượng", "Giá nhập", "Min", "Max", "Thành tiền"};
        cartTableModel.setColumnIdentifiers(cols);
        cartTableModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;
        for (ImportDetailDTO item : cartItems) {
            cartTableModel.addRow(new Object[]{
                item.getProductId(),
                item.getQuantity(),
                String.format("%,.0f", item.getImportPrice()),
                item.getMinStockLevel(),
                item.getMaxStockLevel(),
                String.format("%,.0f", item.getSubtotal())
            });
            total = total.add(item.getSubtotal());
        }
        lblTotalCost.setText(String.format("Tổng cộng: %,.0f ₫", total));
    }

    private void saveReceipt() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Giỏ nhập hàng đang trống!");
            return;
        }

        try {
            Integer suppId = null;
            if (!txtSupplierId.getText().trim().isEmpty()) {
                suppId = Integer.parseInt(txtSupplierId.getText().trim());
            }

            Integer staffId = null;
            if (!txtStaffId.getText().trim().isEmpty()) {
                staffId = Integer.parseInt(txtStaffId.getText().trim());
            }

            BigDecimal totalCost = BigDecimal.ZERO;
            for (ImportDetailDTO item : cartItems) {
                totalCost = totalCost.add(item.getSubtotal());
            }

            ImportReceiptDTO receiptDTO = new ImportReceiptDTO();
            receiptDTO.setSupplierId(suppId);
            receiptDTO.setStaffId(staffId);
            receiptDTO.setNote(txtNote.getText().trim());
            receiptDTO.setImportDate(LocalDateTime.now());
            receiptDTO.setStatus("COMPLETED");
            receiptDTO.setTotalCost(totalCost);
            receiptDTO.setItems(new ArrayList<>(cartItems));

            importController.createImportReceipt(receiptDTO);
            
            JOptionPane.showMessageDialog(this, "Lưu lô hàng thành công!");
            resetPanel();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu lô hàng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetPanel() {
        txtSupplierId.setText("");
        txtStaffId.setText("");
        txtNote.setText("");
        txtProductId.setText("");
        txtQuantity.setText("");
        txtImportPrice.setText("");
        txtMinStock.setText("0");
        txtMaxStock.setText("9999");
        cartItems.clear();
        updateCartTable();
    }
}
