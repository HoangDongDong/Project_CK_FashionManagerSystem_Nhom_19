package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.ProductController;
import com.example.salesmis.model.dto.ProductDetailDTO;

/**
 * ProductDetailDialog – hiển thị đầy đủ thông tin sản phẩm và bảng thuộc tính.
 * Được gọi khi người dùng click "Xem chi tiết" từ ProductSearchPanel.
 */
public class ProductDetailDialog extends JDialog {

    private static final Color BG_COLOR    = new Color(245, 246, 250);
    private static final Color ACCENT      = new Color(52, 120, 246);
    private static final Color HEADER_BG   = new Color(230, 237, 255);
    private static final Font  TITLE_FONT  = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font  LABEL_FONT  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font  VALUE_FONT  = new Font("Segoe UI", Font.PLAIN, 13);

    public ProductDetailDialog(JFrame parent, ProductController productController, Integer productId) {
        super(parent, "Chi tiết sản phẩm", true);
        setSize(760, 550);
        setLocationRelativeTo(parent);
        setResizable(false);
        getContentPane().setBackground(BG_COLOR);

        ProductDetailDTO dto;
        try {
            dto = productController.getProductDetails(productId);
        } catch (Exception ex) {
            showError("Khong tai duoc thong tin san pham: " + ex.getMessage());
            return;
        }

        setLayout(new BorderLayout(0, 0));
        add(buildHeaderPanel(dto), BorderLayout.NORTH);
        add(buildInfoPanel(dto),   BorderLayout.CENTER);
        add(buildButtonPanel(),    BorderLayout.SOUTH);
    }

    // --------- Header ---------
    private JPanel buildHeaderPanel(ProductDetailDTO dto) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 12));
        panel.setBackground(ACCENT);

        JLabel title = new JLabel("🛍  " + dto.getProductName());
        title.setFont(TITLE_FONT);
        title.setForeground(Color.WHITE);
        panel.add(title);

        JLabel code = new JLabel("[" + nvl(dto.getProductCode()) + "]");
        code.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        code.setForeground(new Color(200, 220, 255));
        panel.add(code);
        return panel;
    }

    // --------- Thông tin chung + bảng thuộc tính ---------
    private JPanel buildInfoPanel(ProductDetailDTO dto) {
        JPanel wrapper = new JPanel(new BorderLayout(0, 10));
        wrapper.setBackground(BG_COLOR);
        wrapper.setBorder(BorderFactory.createEmptyBorder(14, 18, 8, 18));

        wrapper.add(buildMetaPanel(dto), BorderLayout.NORTH);
        wrapper.add(buildAttributeTable(dto.getAttributes()), BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildMetaPanel(ProductDetailDTO dto) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(HEADER_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 240)),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST; lc.insets = new Insets(4, 0, 4, 14);
        GridBagConstraints vc = new GridBagConstraints();
        vc.anchor = GridBagConstraints.WEST; vc.insets = new Insets(4, 0, 4, 28); vc.weightx = 1;

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        Object[][] fields = {
            {"Mã sản phẩm:",  nvl(dto.getProductCode())},
            {"Danh mục:",     nvl(dto.getCategoryName())},
            {"Giá cơ bản:",   dto.getBasePrice() != null ? nf.format(dto.getBasePrice()) + " ₫" : "—"},
            {"Tồn kho:",      (dto.getQuantityStock() != null ? dto.getQuantityStock() : 0) + " cái"},
            {"Mô tả:",        nvl(dto.getDescription())},
        };

        int row = 0;
        for (Object[] pair : fields) {
            lc.gridx = 0; lc.gridy = row; vc.gridx = 1; vc.gridy = row;
            // Đặt cột mô tả trải rộng nếu là hàng cuối
            if (row == fields.length - 1) { vc.gridwidth = 3; }
            JLabel label = new JLabel((String) pair[0]);
            label.setFont(LABEL_FONT); label.setForeground(new Color(80, 80, 90));
            JLabel value = new JLabel((String) pair[1]);
            value.setFont(VALUE_FONT);
            panel.add(label, lc);
            panel.add(value, vc);
            row++;
            vc.gridwidth = 1; // reset
        }
        return panel;
    }

    private JScrollPane buildAttributeTable(List<ProductDetailDTO.AttributeRow> attributes) {
        String[] columns = {"#", "Size", "Màu sắc", "Chất liệu", "Trọng lượng (kg)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        if (attributes == null || attributes.isEmpty()) {
            model.addRow(new Object[]{"—", "—", "—", "—", "—"});
        } else {
            int idx = 1;
            for (ProductDetailDTO.AttributeRow attr : attributes) {
                model.addRow(new Object[]{
                    idx++,
                    nvl(attr.getSize()),
                    nvl(attr.getColor()),
                    nvl(attr.getMaterial()),
                    attr.getWeight() != null ? attr.getWeight().toPlainString() : "—"
                });
            }
        }

        JTable table = new JTable(model);
        table.setFont(VALUE_FONT);
        table.setRowHeight(26);
        table.getTableHeader().setFont(LABEL_FONT);
        table.getTableHeader().setBackground(ACCENT);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(210, 225, 255));
        table.setGridColor(new Color(220, 225, 240));
        table.getColumnModel().getColumn(0).setPreferredWidth(30);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 210, 240)),
                " Danh sách thuộc tính (Size, Màu sắc, Chất liệu…) ",
                0, 0, LABEL_FONT, ACCENT));
        scroll.setPreferredSize(new Dimension(720, 200));
        return scroll;
    }

    // --------- Nút đóng ---------
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 10));
        panel.setBackground(BG_COLOR);
        JButton btnClose = new JButton("Đóng");
        btnClose.setFont(LABEL_FONT);
        btnClose.setBackground(ACCENT);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setPreferredSize(new Dimension(100, 34));
        btnClose.addActionListener(e -> dispose());
        panel.add(btnClose);
        return panel;
    }

    private void showError(String msg) {
        setLayout(new BorderLayout());
        add(new JLabel(msg, JLabel.CENTER));
    }

    private String nvl(String s) {
        return s != null && !s.isBlank() ? s : "—";
    }
}
