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

import com.example.salesmis.controller.ProductController;
import com.example.salesmis.model.dto.ProductDTO;
import com.example.salesmis.model.dto.ProductDetailDTO;
import com.example.salesmis.model.dto.ProductInventoryDTO;

import com.example.salesmis.controller.CategoryController;
import com.example.salesmis.model.dto.CategoryDTO;

public class ProductManagementPanel extends JPanel {
    private final ProductController productController;
    private final CategoryController categoryController;
    private JTable tableProduct;
    private DefaultTableModel tableModel;
    private List<ProductInventoryDTO> currentList;
    private JTextField txtSearch;

    public ProductManagementPanel(ProductController productController, CategoryController categoryController) {
        this.productController = productController;
        this.categoryController = categoryController;
        initComponents();
        loadData("");
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // TOP TOOLBAR
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField(15);
        JButton btnSearch = new JButton("Tìm");
        JButton btnAdd = new JButton("Thêm SP mới");
        JButton btnViewDetails = new JButton("Xem chi tiết");
        JButton btnEdit = new JButton("Sửa thông tin");
        JButton btnDelete = new JButton("Ngừng KD (Xóa)");

        btnSearch.addActionListener(e -> loadData(txtSearch.getText().trim()));
        btnAdd.addActionListener(e -> showAddDialog());
        btnViewDetails.addActionListener(e -> showDetailsDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> softDeleteProduct());

        toolbar.add(txtSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnAdd);
        toolbar.add(btnViewDetails);
        toolbar.add(btnEdit);
        toolbar.add(btnDelete);

        // TABLE
        String[] columns = {"ID", "Mã SP", "Tên SP", "Giá Cơ bản", "Tồn Kho"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableProduct = new JTable(tableModel);

        add(toolbar, BorderLayout.NORTH);
        add(new JScrollPane(tableProduct), BorderLayout.CENTER);
    }

    private void loadData(String keyword) {
        try {
            currentList = productController.findProducts(keyword);
            tableModel.setRowCount(0);
            for (ProductInventoryDTO dto : currentList) {
                tableModel.addRow(new Object[]{
                    dto.getProductId(),
                    dto.getProductCode(),
                    dto.getProductName(),
                    dto.getBasePrice(),
                    dto.getQuantityStock()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm SP: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddDialog() {
        Window parentWin = SwingUtilities.windowForComponent(this);
        List<CategoryDTO> cats = categoryController != null ? categoryController.getAllCategories() : null;
        ProductFormDialog dialog = new ProductFormDialog((java.awt.Frame) parentWin, "Thêm Sản phẩm", null, cats);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                productController.addProduct(dialog.getProductData());
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công!");
                loadData("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi thêm SP: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showDetailsDialog() {
        int row = tableProduct.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 sản phẩm để xem chi tiết!");
            return;
        }

        Integer pId = currentList.get(row).getProductId();
        Window parentWin = SwingUtilities.windowForComponent(this);
        ProductDetailDialog dialog = new ProductDetailDialog((javax.swing.JFrame) parentWin, productController, pId);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int row = tableProduct.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 sản phẩm!");
            return;
        }

        Integer pId = currentList.get(row).getProductId();
        
        try {
            // Lấy Detail
            ProductDetailDTO detail = productController.getProductDetails(pId);
            ProductDTO mapped = new ProductDTO();
            mapped.setProductId(detail.getProductId());
            mapped.setProductName(detail.getProductName());
            mapped.setProductCode(detail.getProductCode());
            mapped.setBasePrice(detail.getBasePrice());
            mapped.setDescription(detail.getDescription());
            mapped.setCategoryId(detail.getCategoryId());
            
            Window parentWin = SwingUtilities.windowForComponent(this);
            List<CategoryDTO> cats = categoryController != null ? categoryController.getAllCategories() : null;
            ProductFormDialog dialog = new ProductFormDialog((java.awt.Frame) parentWin, "Cập nhật Sản phẩm", mapped, cats);
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                productController.updateProduct(pId, dialog.getProductData());
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadData("");
            }
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void softDeleteProduct() {
        int row = tableProduct.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần xóa!");
            return;
        }

        Integer pId = currentList.get(row).getProductId();
        int answer = JOptionPane.showConfirmDialog(this, "Bạn có chắn chắn ngừng kinh doanh sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (answer == JOptionPane.YES_OPTION) {
            try {
                productController.deleteProduct(pId);
                JOptionPane.showMessageDialog(this, "Sản phẩm đã được cấu hình ngừng kinh doanh!");
                loadData("");
            } catch (Exception ex) {
                 JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
