package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.ReportController;
import com.example.salesmis.controller.StaffController;
import com.example.salesmis.model.dto.OrderDTO;
import com.example.salesmis.model.dto.ReportDTO;
import com.example.salesmis.model.dto.StaffDTO;

public class ReportPanel extends JPanel {
    private final ReportController reportController;
    private final StaffController staffController;
    
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JComboBox<StaffItem> cbxStaff;
    
    private JLabel lblTotalRevenue;
    private JLabel lblTotalOrders;
    private JLabel lblTotalImportCost;
    private JLabel lblProfit;
    
    private JTable tableChart;
    private DefaultTableModel chartTableModel;
    
    private JTable tableOrders;
    private DefaultTableModel orderTableModel;

    public ReportPanel(ReportController reportController, StaffController staffController) {
        this.reportController = reportController;
        this.staffController = staffController;
        initComponents();
        loadStaffs();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // TOP CONTROLS
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        topPanel.add(new JLabel("Từ ngày (yyyy-MM-dd):"));
        txtStartDate = new JTextField(10);
        txtStartDate.setText(LocalDate.now().withDayOfMonth(1).toString());
        topPanel.add(txtStartDate);

        topPanel.add(new JLabel("Đến ngày:"));
        txtEndDate = new JTextField(10);
        txtEndDate.setText(LocalDate.now().toString());
        topPanel.add(txtEndDate);

        topPanel.add(new JLabel("Nhân viên:"));
        cbxStaff = new JComboBox<>();
        topPanel.add(cbxStaff);

        JButton btnView = new JButton("Xem thống kê");
        btnView.addActionListener(e -> fetchReport());
        topPanel.add(btnView);

        // SUMMARY LABELS
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        lblTotalRevenue = new JLabel("Tổng doanh thu: 0 ₫");
        lblTotalRevenue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalRevenue.setForeground(new java.awt.Color(20, 160, 100)); // Xanh lá
        
        lblTotalImportCost = new JLabel("Tổng chi phí nhập: 0 ₫");
        lblTotalImportCost.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalImportCost.setForeground(new java.awt.Color(220, 120, 30)); // Cam

        lblProfit = new JLabel("Lợi nhuận: 0 ₫");
        lblProfit.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblProfit.setForeground(java.awt.Color.RED);

        lblTotalOrders = new JLabel("Tổng số đơn: 0");
        lblTotalOrders.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        summaryPanel.add(lblTotalRevenue);
        summaryPanel.add(lblTotalImportCost);
        summaryPanel.add(lblProfit);
        summaryPanel.add(lblTotalOrders);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(summaryPanel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // TABLES (SPLIT PANE)
        String[] chartCols = {"Ngày", "Doanh thu (VNĐ)"};
        chartTableModel = new DefaultTableModel(chartCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableChart = new JTable(chartTableModel);
        JPanel pnlChart = new JPanel(new BorderLayout());
        pnlChart.setBorder(javax.swing.BorderFactory.createTitledBorder("Thống kê theo ngày"));
        pnlChart.add(new JScrollPane(tableChart), BorderLayout.CENTER);

        String[] orderCols = {"Mã Hóa Đơn", "Ngày", "Nhân Viên", "Khách Hàng", "Trạng Thái", "Tổng Tiền"};
        orderTableModel = new DefaultTableModel(orderCols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableOrders = new JTable(orderTableModel);
        JPanel pnlOrders = new JPanel(new BorderLayout());
        pnlOrders.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách hóa đơn chi tiết"));
        pnlOrders.add(new JScrollPane(tableOrders), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlChart, pnlOrders);
        splitPane.setDividerLocation(300);
        splitPane.setResizeWeight(0.3);

        add(splitPane, BorderLayout.CENTER);
    }

    private void loadStaffs() {
        cbxStaff.addItem(new StaffItem(null, "<Tất cả nhân viên>"));
        if (staffController != null) {
            List<StaffDTO> staffs = staffController.getStaffList();
            for (StaffDTO staff : staffs) {
                cbxStaff.addItem(new StaffItem(staff.getStaffId(), staff.getStaffId() + " - " + staff.getFullName()));
            }
        }
    }

    private void fetchReport() {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(txtStartDate.getText().trim(), formatter);
            LocalDate end = LocalDate.parse(txtEndDate.getText().trim(), formatter);

            if (start.isAfter(end)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải trước ngày kết thúc!");
                return;
            }

            LocalDateTime startDateTime = start.atStartOfDay();
            LocalDateTime endDateTime = end.atTime(23, 59, 59);
            
            StaffItem selectedStaff = (StaffItem) cbxStaff.getSelectedItem();
            Integer staffId = null;
            if (selectedStaff != null) {
                staffId = selectedStaff.getId();
            }

            ReportDTO dto = reportController.getRevenueReport(startDateTime, endDateTime, staffId);
            updateUI(dto);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ. Vui lòng dùng yyyy-MM-dd");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thống kê: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void updateUI(ReportDTO dto) {
        if (dto == null) return;

        DecimalFormat df = new DecimalFormat("#,##0");
        BigDecimal totalRev = dto.getTotalRevenue() != null ? dto.getTotalRevenue() : BigDecimal.ZERO;
        BigDecimal totalImp = dto.getTotalImportCost() != null ? dto.getTotalImportCost() : BigDecimal.ZERO;
        BigDecimal profit = totalRev.subtract(totalImp);
        
        lblTotalRevenue.setText("Tổng doanh thu: " + df.format(totalRev) + " ₫");
        lblTotalImportCost.setText("Tổng chi phí nhập: " + df.format(totalImp) + " ₫");
        lblProfit.setText("Lợi nhuận: " + df.format(profit) + " ₫");
        lblTotalOrders.setText("Tổng số đơn: " + dto.getTotalOrders());

        chartTableModel.setRowCount(0);
        Map<String, BigDecimal> chart = dto.getChartData();
        if (chart != null) {
            for (Map.Entry<String, BigDecimal> entry : chart.entrySet()) {
                chartTableModel.addRow(new Object[]{
                    entry.getKey(),
                    df.format(entry.getValue())
                });
            }
        }
        
        orderTableModel.setRowCount(0);
        List<OrderDTO> orders = dto.getOrders();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (orders != null) {
            for (OrderDTO order : orders) {
                orderTableModel.addRow(new Object[]{
                    order.getOrderNumber(),
                    order.getOrderDate().format(timeFormatter),
                    order.getStaffName(),
                    order.getCustomerName(),
                    order.getStatus(),
                    df.format(order.getFinalTotal())
                });
            }
        }
    }

    private static class StaffItem {
        private final Integer id;
        private final String display;

        public StaffItem(Integer id, String display) {
            this.id = id;
            this.display = display;
        }

        public Integer getId() { return id; }

        @Override
        public String toString() { return display; }
    }
}
