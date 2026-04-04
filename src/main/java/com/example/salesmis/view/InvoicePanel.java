package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.OrderController;
import com.example.salesmis.controller.ProductController;
import com.example.salesmis.controller.VoucherController;
import com.example.salesmis.model.dto.CreateInvoiceRequest;
import com.example.salesmis.model.dto.DiscountResult;
import com.example.salesmis.model.dto.InvoiceItemRequest;
import com.example.salesmis.model.dto.InvoiceSummaryDTO;
import com.example.salesmis.model.dto.ProductInventoryDTO;
import com.example.salesmis.model.dto.VoucherListItemDTO;
import com.example.salesmis.model.entity.OrderEntity;
import com.example.salesmis.service.exception.InsufficientStockException;
import com.example.salesmis.service.exception.InvalidVoucherException;
import com.example.salesmis.service.exception.RequirementNotMetException;

public class InvoicePanel extends JPanel {
    private final transient OrderController orderController;
    private final transient ProductController productController;
    private final transient VoucherController voucherController;
    private final JTextField staffIdField;
    private final JTextField customerIdField;
    private final JComboBox<VoucherListItemDTO> voucherCombo;
    private final JTextField noteField;
    private final JTextField searchField;
    private final JTextField quantityToAddField;

    private final DefaultTableModel productTableModel;
    private final DefaultTableModel cartTableModel;
    private final DefaultTableModel historyTableModel;

    private final List<ProductInventoryDTO> productResults = new ArrayList<>();
    private final List<CartItem> cartItems = new ArrayList<>();

    private JTable productTable; // kept as field for selection access

    private final NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Integer appliedVoucherId;
    private final JLabel lblSubtotal;
    private final JLabel lblDiscount;
    private final JLabel lblFinalTotal;

    public InvoicePanel(OrderController orderController, ProductController productController, VoucherController voucherController) {
        this.orderController = orderController;
        this.productController = productController;
        this.voucherController = voucherController;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel orderInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        staffIdField = new JTextField(7);
        customerIdField = new JTextField(7);
        noteField = new JTextField(20);
        orderInfoPanel.add(new JLabel("Staff ID:"));
        orderInfoPanel.add(staffIdField);
        orderInfoPanel.add(new JLabel("Customer ID:"));
        orderInfoPanel.add(customerIdField);
        orderInfoPanel.add(new JLabel("Note:"));
        orderInfoPanel.add(noteField);

        JPanel voucherPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        voucherCombo = new JComboBox<>();
        voucherCombo.setPreferredSize(new Dimension(520, 28));
        JButton reloadVouchersButton = new JButton("Tai lai DS ma");
        JButton applyVoucherButton = new JButton("Ap dung");
        lblSubtotal = new JLabel();
        lblDiscount = new JLabel();
        lblFinalTotal = new JLabel();
        voucherPanel.add(new JLabel("Chon ma giam gia:"));
        voucherPanel.add(voucherCombo);
        voucherPanel.add(reloadVouchersButton);
        voucherPanel.add(applyVoucherButton);
        voucherPanel.add(new JLabel("  |  "));
        voucherPanel.add(new JLabel("Tong hang:"));
        voucherPanel.add(lblSubtotal);
        voucherPanel.add(new JLabel("Giam:"));
        voucherPanel.add(lblDiscount);
        voucherPanel.add(new JLabel("Thanh tien:"));
        voucherPanel.add(lblFinalTotal);

        JPanel northStack = new JPanel(new BorderLayout(4, 4));
        northStack.add(orderInfoPanel, BorderLayout.NORTH);
        northStack.add(voucherPanel, BorderLayout.SOUTH);
        add(northStack, BorderLayout.NORTH);

        applyVoucherButton.addActionListener(e -> handleApplyVoucher());
        reloadVouchersButton.addActionListener(e -> loadVoucherComboItems());
        loadVoucherComboItems();
        resetVoucherSummaryLabels();

        JPanel productPanel = new JPanel(new BorderLayout(6, 6));
        productPanel.setBorder(BorderFactory.createTitledBorder("Bang san pham"));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchField = new JTextField(24);
        JButton searchButton = new JButton("Tim");
        JButton detailButton = new JButton("Xem chi tiet");
        detailButton.setEnabled(false);
        searchPanel.add(new JLabel("Tu khoa:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(detailButton);

        // Enter key in search field triggers search
        searchField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "doSearch");
        searchField.getActionMap().put("doSearch", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { loadProducts(); }
        });

        productTableModel = new DefaultTableModel(new Object[] {"ID", "Ma SP", "Ten san pham", "Gia", "Ton kho"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Enable/disable detail button based on row selection
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                detailButton.setEnabled(productTable.getSelectedRow() >= 0);
            }
        });

        // Double-click on product row opens detail
        productTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) openProductDetail();
            }
        });

        productPanel.add(searchPanel, BorderLayout.NORTH);
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel cartPanel = new JPanel(new BorderLayout(6, 6));
        cartPanel.setBorder(BorderFactory.createTitledBorder("Gio tam thoi"));

        JPanel addToCartPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        quantityToAddField = new JTextField(8);
        JButton addToCartButton = new JButton("Them vao gio");
        JButton removeFromCartButton = new JButton("Xoa khoi gio");
        addToCartPanel.add(new JLabel("So luong:"));
        addToCartPanel.add(quantityToAddField);
        addToCartPanel.add(addToCartButton);
        addToCartPanel.add(removeFromCartButton);

        cartTableModel = new DefaultTableModel(
                new Object[] {"ID", "Ten san pham", "So luong", "Don gia", "Thanh tien"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        JTable cartTable = new JTable(cartTableModel);

        JPanel cartActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createInvoiceButton = new JButton("Lap hoa don");
        cartActionPanel.add(createInvoiceButton);

        cartPanel.add(addToCartPanel, BorderLayout.NORTH);
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        cartPanel.add(cartActionPanel, BorderLayout.SOUTH);

        JSplitPane topSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productPanel, cartPanel);
        topSplitPane.setResizeWeight(0.55);

        JPanel historyPanel = new JPanel(new BorderLayout(6, 6));
        historyPanel.setBorder(BorderFactory.createTitledBorder("Bang hoa don da mua"));
        historyTableModel = new DefaultTableModel(
                new Object[] {"Order ID", "So hoa don", "Ngay lap", "Trang thai", "Tong tien"}, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
        JTable historyTable = new JTable(historyTableModel);
        JButton refreshHistoryButton = new JButton("Tai lai hoa don");
        JPanel historyActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        historyActionPanel.add(refreshHistoryButton);
        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);
        historyPanel.add(historyActionPanel, BorderLayout.SOUTH);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topSplitPane, historyPanel);
        mainSplitPane.setResizeWeight(0.62);
        add(mainSplitPane, BorderLayout.CENTER);

        searchButton.addActionListener(e -> loadProducts());
        addToCartButton.addActionListener(e -> addSelectedProductToCart(productTable.getSelectedRow()));
        removeFromCartButton.addActionListener(e -> removeCartItem(cartTable.getSelectedRow()));
        createInvoiceButton.addActionListener(e -> createInvoice());
        refreshHistoryButton.addActionListener(e -> loadRecentInvoices());
        detailButton.addActionListener(e -> openProductDetail());

        loadProducts();
        loadRecentInvoices();
    }

    private void loadProducts() {
        productTableModel.setRowCount(0);
        productResults.clear();

        String keyword = searchField.getText().trim();
        new SwingWorker<List<ProductInventoryDTO>, Void>() {
            @Override
            protected List<ProductInventoryDTO> doInBackground() {
                return orderController.searchProducts(keyword);
            }
            @Override
            protected void done() {
                try {
                    List<ProductInventoryDTO> products = get();
                    productResults.addAll(products);
                    for (ProductInventoryDTO product : productResults) {
                        productTableModel.addRow(new Object[] {
                                product.getProductId(),
                                product.getProductCode(),
                                product.getProductName(),
                                formatMoney(product.getBasePrice()),
                                product.getQuantityStock()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(InvoicePanel.this,
                            "Loi tai san pham: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void openProductDetail() {
        int row = productTable.getSelectedRow();
        if (row < 0 || row >= productResults.size()) return;
        Integer productId = productResults.get(row).getProductId();
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        new ProductDetailDialog(parent, productController, productId).setVisible(true);
    }

    private void addSelectedProductToCart(int selectedProductRow) {
        if (selectedProductRow < 0 || selectedProductRow >= productResults.size()) {
            JOptionPane.showMessageDialog(this, "Vui long chon san pham trong bang", "Thong bao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int quantity;
        try {
            quantity = parseRequiredInteger(quantityToAddField.getText(), "So luong");
            if (quantity <= 0) {
                throw new IllegalArgumentException("So luong phai > 0");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Du lieu khong hop le", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProductInventoryDTO selected = productResults.get(selectedProductRow);
        int existingIndex = indexOfCartItem(selected.getProductId());
        if (existingIndex >= 0) {
            CartItem existing = cartItems.get(existingIndex);
            existing.quantity += quantity;
        } else {
            CartItem newItem = new CartItem();
            newItem.product = selected;
            newItem.quantity = quantity;
            cartItems.add(newItem);
        }
        quantityToAddField.setText("");
        refreshCartTable();
    }

    private int indexOfCartItem(Integer productId) {
        for (int i = 0; i < cartItems.size(); i++) {
            if (cartItems.get(i).product.getProductId().equals(productId)) {
                return i;
            }
        }
        return -1;
    }

    private void removeCartItem(int selectedCartRow) {
        if (selectedCartRow < 0 || selectedCartRow >= cartItems.size()) {
            return;
        }
        cartItems.remove(selectedCartRow);
        refreshCartTable();
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (CartItem item : cartItems) {
            BigDecimal unitPrice = item.product.getBasePrice() == null ? BigDecimal.ZERO : item.product.getBasePrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.quantity));
            cartTableModel.addRow(new Object[] {
                    item.product.getProductId(),
                    item.product.getProductName(),
                    item.quantity,
                    formatMoney(unitPrice),
                    formatMoney(subtotal)
            });
        }
        clearAppliedVoucher();
    }

    private BigDecimal computeCartSubtotal() {
        BigDecimal sum = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            BigDecimal unitPrice = item.product.getBasePrice() == null ? BigDecimal.ZERO : item.product.getBasePrice();
            sum = sum.add(unitPrice.multiply(BigDecimal.valueOf(item.quantity)));
        }
        return sum;
    }

    private void clearAppliedVoucher() {
        appliedVoucherId = null;
        resetVoucherSummaryLabels();
    }

    private void resetVoucherSummaryLabels() {
        BigDecimal sub = computeCartSubtotal();
        lblSubtotal.setText(formatMoney(sub));
        lblDiscount.setText(formatMoney(BigDecimal.ZERO));
        lblFinalTotal.setText(formatMoney(sub));
    }

    private void loadVoucherComboItems() {
        try {
            List<VoucherListItemDTO> list = voucherController.listVouchersForInvoice();
            voucherCombo.removeAllItems();
            voucherCombo.addItem(VoucherListItemDTO.placeholder());
            for (VoucherListItemDTO dto : list) {
                voucherCombo.addItem(dto);
            }
            voucherCombo.setSelectedIndex(0);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Khong tai duoc danh sach voucher: " + ex.getMessage(), "Loi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleApplyVoucher() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Gio hang dang trong, khong the ap dung voucher", "Thong bao",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        Object selected = voucherCombo.getSelectedItem();
        if (!(selected instanceof VoucherListItemDTO item) || item.isPlaceholder()) {
            JOptionPane.showMessageDialog(this, "Vui long chon mot ma giam gia trong danh sach", "Thong bao",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        String code = item.getCode() == null ? "" : item.getCode().trim();
        if (code.isBlank()) {
            JOptionPane.showMessageDialog(this, "Ma voucher khong hop le", "Thong bao", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BigDecimal currentTotal = computeCartSubtotal();
        try {
            DiscountResult result = voucherController.applyVoucher(code, currentTotal);
            appliedVoucherId = result.getVoucherId();
            lblSubtotal.setText(formatMoney(currentTotal));
            lblDiscount.setText(formatMoney(result.getDiscountAmount()));
            lblFinalTotal.setText(formatMoney(result.getNewTotal()));
            JOptionPane.showMessageDialog(this, "Ap dung ma thanh cong", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
        } catch (InvalidVoucherException ex) {
            appliedVoucherId = null;
            resetVoucherSummaryLabels();
            JOptionPane.showMessageDialog(this, "Ma khong hop le hoac het han", "Loi voucher", JOptionPane.ERROR_MESSAGE);
        } catch (RequirementNotMetException ex) {
            appliedVoucherId = null;
            resetVoucherSummaryLabels();
            JOptionPane.showMessageDialog(this, "Don hang chua du gia tri toi thieu", "Loi voucher", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            appliedVoucherId = null;
            resetVoucherSummaryLabels();
            JOptionPane.showMessageDialog(this, "Loi: " + ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createInvoice() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Gio tam dang rong", "Thong bao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CreateInvoiceRequest request = new CreateInvoiceRequest();
        request.setStaffId(parseNullableInteger(staffIdField.getText()));
        request.setCustomerId(parseNullableInteger(customerIdField.getText()));
        request.setVoucherId(appliedVoucherId);
        request.setNote(noteField.getText().trim());

        List<InvoiceItemRequest> items = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            InvoiceItemRequest requestItem = new InvoiceItemRequest();
            requestItem.setProductId(cartItem.product.getProductId());
            requestItem.setQuantity(cartItem.quantity);
            requestItem.setUnitPrice(cartItem.product.getBasePrice());
            items.add(requestItem);
        }
        request.setItems(items);

        try {
            OrderEntity createdOrder = orderController.createOrder(request);
            JOptionPane.showMessageDialog(this,
                    "Lap hoa don thanh cong. Ma: " + createdOrder.getOrderNumber(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            cartItems.clear();
            noteField.setText("");
            voucherCombo.setSelectedIndex(0);
            refreshCartTable();
            loadProducts();
            loadRecentInvoices();
        } catch (InsufficientStockException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Khong du ton kho", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidVoucherException ex) {
            JOptionPane.showMessageDialog(this, "Ma khong hop le hoac het han", "Loi voucher", JOptionPane.ERROR_MESSAGE);
        } catch (RequirementNotMetException ex) {
            JOptionPane.showMessageDialog(this, "Don hang chua du gia tri toi thieu", "Loi voucher", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Khong the lap hoa don: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentInvoices() {
        historyTableModel.setRowCount(0);
        List<InvoiceSummaryDTO> invoices = orderController.getRecentInvoices(50);
        for (InvoiceSummaryDTO invoice : invoices) {
            String orderDate = invoice.getOrderDate() == null ? "" : invoice.getOrderDate().format(dateTimeFormatter);
            historyTableModel.addRow(new Object[] {
                    invoice.getOrderId(),
                    invoice.getOrderNumber(),
                    orderDate,
                    invoice.getStatus(),
                    formatMoney(invoice.getFinalTotal())
            });
        }
    }

    private Integer parseRequiredInteger(String value, String fieldName) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(fieldName + " khong duoc de trong");
        }
        return Integer.valueOf(normalized);
    }

    private Integer parseNullableInteger(String value) {
        String normalized = value == null ? "" : value.trim();
        return normalized.isBlank() ? null : Integer.valueOf(normalized);
    }

    private String formatMoney(BigDecimal value) {
        BigDecimal money = value == null ? BigDecimal.ZERO : value;
        return currencyFormat.format(money);
    }

    private static class CartItem {
        private ProductInventoryDTO product;
        private int quantity;
    }
}
