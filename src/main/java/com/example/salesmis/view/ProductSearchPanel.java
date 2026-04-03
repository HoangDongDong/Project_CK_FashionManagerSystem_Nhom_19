package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.ProductController;
import com.example.salesmis.model.dto.ProductInventoryDTO;

/**
 * ProductSearchPanel – tab "Tìm kiếm sản phẩm" trong StaffDashboardFrame.
 * Theo UML sequence:
 *   Nhân viên → nhập từ khóa → bấm "Tìm kiếm"
 *     → searchProducts(keyword) → ProductController.findProducts(keyword)
 *     → hiển thị danh sách / thông báo không tìm thấy
 *   Nhân viên → chọn dòng → bấm "Xem chi tiết"
 *     → ProductDetailDialog.getProductDetails(productId)
 */
public class ProductSearchPanel extends JPanel {

    private static final Color  BG_PANEL    = new Color(245, 246, 250);
    private static final Color  ACCENT      = new Color(52, 120, 246);
    private static final Color  BTN_DETAIL  = new Color(34, 170, 100);
    private static final Font   TITLE_FONT  = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font   LABEL_FONT  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font   PLAIN_FONT  = new Font("Segoe UI", Font.PLAIN, 13);

    private final ProductController productController;

    private JTextField           searchField;
    private JButton              btnSearch;
    private JButton              btnViewDetail;
    private JLabel               statusLabel;
    private JTable               resultTable;
    private DefaultTableModel    tableModel;

    // giữ productId theo từng row trong bảng để tránh phụ thuộc vào tên hiển thị
    private java.util.List<Integer> productIds = new java.util.ArrayList<>();

    public ProductSearchPanel(ProductController productController) {
        this.productController = productController;
        setBackground(BG_PANEL);
        setLayout(new BorderLayout(0, 0));
        setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        add(buildTopPanel(),    BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        add(buildStatusBar(),   BorderLayout.SOUTH);

        // Load toàn bộ sản phẩm khi khởi tạo panel
        SwingUtilities.invokeLater(() -> runSearch(""));
    }

    // ===================== NORTH: thanh tìm kiếm =====================
    private JPanel buildTopPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setBackground(BG_PANEL);

        // Title
        JLabel title = new JLabel("🔍  Tìm kiếm sản phẩm");
        title.setFont(TITLE_FONT);
        title.setForeground(new Color(30, 40, 80));
        wrapper.add(title, BorderLayout.NORTH);

        // Search bar row
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setBackground(BG_PANEL);

        JLabel lbl = new JLabel("Từ khóa:");
        lbl.setFont(LABEL_FONT);

        searchField = new JTextField(30);
        searchField.setFont(PLAIN_FONT);
        searchField.setPreferredSize(new Dimension(300, 32));
        searchField.setToolTipText("Nhập tên hoặc mã sản phẩm rồi Enter / bấm Tìm kiếm");

        // Enter → search
        searchField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doSearch");
        searchField.getActionMap().put("doSearch", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { doSearch(); }
        });

        btnSearch = styledButton("Tìm kiếm", ACCENT);
        btnSearch.addActionListener(e -> doSearch());

        btnViewDetail = styledButton("Xem chi tiết", BTN_DETAIL);
        btnViewDetail.setEnabled(false);
        btnViewDetail.addActionListener(e -> openDetail());

        searchRow.add(lbl);
        searchRow.add(searchField);
        searchRow.add(btnSearch);
        searchRow.add(btnViewDetail);

        wrapper.add(searchRow, BorderLayout.CENTER);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return wrapper;
    }

    // ===================== CENTER: kết quả dạng bảng =====================
    private JScrollPane buildTablePanel() {
        String[] columns = {"Mã SP", "Tên sản phẩm", "Giá (₫)", "Tồn kho"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        resultTable = new JTable(tableModel);
        resultTable.setFont(PLAIN_FONT);
        resultTable.setRowHeight(28);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultTable.setSelectionBackground(new Color(210, 225, 255));
        resultTable.setGridColor(new Color(220, 225, 240));
        resultTable.getTableHeader().setFont(LABEL_FONT);
        resultTable.getTableHeader().setBackground(ACCENT);
        resultTable.getTableHeader().setForeground(Color.WHITE);
        resultTable.getTableHeader().setReorderingAllowed(false);

        // Cột mã SP: hẹp lại
        resultTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        resultTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        resultTable.getColumnModel().getColumn(3).setPreferredWidth(90);

        // Khi chọn dòng → enable nút Xem chi tiết
        resultTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                btnViewDetail.setEnabled(resultTable.getSelectedRow() >= 0);
            }
        });

        // Double-click → mở chi tiết luôn
        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openDetail();
            }
        });

        JScrollPane scroll = new JScrollPane(resultTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 210, 240)));
        return scroll;
    }

    // ===================== SOUTH: status bar =====================
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        bar.setBackground(BG_PANEL);
        statusLabel = new JLabel("Sẵn sàng.");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(Color.GRAY);
        bar.add(statusLabel);
        return bar;
    }

    // ===================== Logic =====================

    private void doSearch() {
        String keyword = searchField.getText().trim();
        runSearch(keyword);
    }

    /**
     * Chạy tìm kiếm trên background thread để không block EDT.
     */
    private void runSearch(String keyword) {
        btnSearch.setEnabled(false);
        btnViewDetail.setEnabled(false);
        statusLabel.setText("Đang tìm kiếm…");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<List<ProductInventoryDTO>, Void>() {
            @Override
            protected List<ProductInventoryDTO> doInBackground() {
                return productController.findProducts(keyword);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                btnSearch.setEnabled(true);
                try {
                    List<ProductInventoryDTO> results = get();
                    populateTable(results);
                    if (results.isEmpty()) {
                        statusLabel.setText("Không tìm thấy sản phẩm nào khớp với \"" + keyword + "\".");
                    } else {
                        statusLabel.setText("Tìm thấy " + results.size() + " sản phẩm.");
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Lỗi: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void populateTable(List<ProductInventoryDTO> results) {
        tableModel.setRowCount(0);
        productIds.clear();

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        for (ProductInventoryDTO dto : results) {
            tableModel.addRow(new Object[]{
                dto.getProductCode() != null ? dto.getProductCode() : "—",
                dto.getProductName() != null ? dto.getProductName() : "—",
                dto.getBasePrice() != null ? nf.format(dto.getBasePrice()) : "—",
                dto.getQuantityStock() != null ? dto.getQuantityStock() : 0
            });
            productIds.add(dto.getProductId());
        }
    }

    private void openDetail() {
        int row = resultTable.getSelectedRow();
        if (row < 0 || row >= productIds.size()) return;

        Integer productId = productIds.get(row);
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Chạy trên background để không block UI khi tải
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { return null; }
            @Override protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    ProductDetailDialog dialog = new ProductDetailDialog(parent, productController, productId);
                    dialog.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ProductSearchPanel.this,
                            "Khong mo duoc chi tiet: " + ex.getMessage(),
                            "Loi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // ===================== Helper =====================
    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(LABEL_FONT);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 32));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
