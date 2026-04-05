package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.util.List;
import javax.swing.JComboBox;

import com.example.salesmis.model.dto.CategoryDTO;
import com.example.salesmis.model.dto.ProductDTO;

public class ProductFormDialog extends JDialog {
    private JTextField txtCode, txtName, txtPrice, txtMaterial, txtSize, txtColor, txtWeight, txtStock;
    private JTextArea txtDesc;
    private JComboBox<CategoryDTO> cbxCategory;
    private boolean isConfirmed = false;
    private ProductDTO product;
    private List<CategoryDTO> categories;

    public ProductFormDialog(Frame parent, String title, ProductDTO initialData, List<CategoryDTO> categories) {
        super(parent, title, true);
        this.product = initialData == null ? new ProductDTO() : initialData;
        this.categories = categories;
        initComponents(initialData != null && initialData.getProductId() != null);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents(boolean isEditMode) {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCode = new JTextField(20);
        txtName = new JTextField(20);
        txtPrice = new JTextField(20);
        txtMaterial = new JTextField(20);
        txtSize = new JTextField(20);
        txtColor = new JTextField(20);
        txtWeight = new JTextField(20);
        txtStock = new JTextField(20);
        txtDesc = new JTextArea(3, 20);

        cbxCategory = new JComboBox<>();
        cbxCategory.addItem(new CategoryDTO(null, "<Chưa chọn>", null));
        if (categories != null) {
            for (CategoryDTO c : categories) {
                cbxCategory.addItem(c);
            }
        }

        int row = 0;
        addFormField(formPanel, "Danh mục:", cbxCategory, gbc, row++);
        addFormField(formPanel, "Mã SP:", txtCode, gbc, row++);
        addFormField(formPanel, "Tên SP:", txtName, gbc, row++);
        addFormField(formPanel, "Giá cơ bản:", txtPrice, gbc, row++);
        addFormField(formPanel, "Chất liệu:", txtMaterial, gbc, row++);
        addFormField(formPanel, "Kích thước:", txtSize, gbc, row++);
        addFormField(formPanel, "Màu sắc:", txtColor, gbc, row++);
        addFormField(formPanel, "Khối lượng:", txtWeight, gbc, row++);
        
        if (!isEditMode) {
            addFormField(formPanel, "Tồn kho ban đầu:", txtStock, gbc, row++);
        }

        gbc.gridx = 0; gbc.gridy = row++;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(txtDesc), gbc);

        if (isEditMode) {
            txtCode.setText(product.getProductCode());
            txtName.setText(product.getProductName());
            txtPrice.setText(product.getBasePrice() != null ? product.getBasePrice().toString() : "");
            txtDesc.setText(product.getDescription());
            
            if (product.getCategoryId() != null) {
                for (int i = 0; i < cbxCategory.getItemCount(); i++) {
                    if (product.getCategoryId().equals(cbxCategory.getItemAt(i).getCategoryId())) {
                        cbxCategory.setSelectedIndex(i);
                        break;
                    }
                }
            }
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
        if (txtName.getText().trim().isEmpty() || txtCode.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã SP và Tên SP không được để trống", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            CategoryDTO selectedCat = (CategoryDTO) cbxCategory.getSelectedItem();
            product.setCategoryId(selectedCat != null && selectedCat.getCategoryId() != null ? selectedCat.getCategoryId() : null);

            product.setProductCode(txtCode.getText().trim());
            product.setProductName(txtName.getText().trim());
            product.setDescription(txtDesc.getText().trim());
            
            String priceStr = txtPrice.getText().trim();
            if (!priceStr.isEmpty()) product.setBasePrice(new BigDecimal(priceStr));

            product.setMaterial(txtMaterial.getText().trim());
            product.setSize(txtSize.getText().trim());
            product.setColor(txtColor.getText().trim());
            
            String weightStr = txtWeight.getText().trim();
            if (!weightStr.isEmpty()) product.setWeight(new BigDecimal(weightStr));
            
            if (txtStock.isVisible() && !txtStock.getText().trim().isEmpty()) {
                product.setQuantityStock(Integer.parseInt(txtStock.getText().trim()));
            }
            
            isConfirmed = true;
            dispose();
            
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Giá, khối lượng hoặc tồn kho phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() { return isConfirmed; }
    public ProductDTO getProductData() { return product; }
}
