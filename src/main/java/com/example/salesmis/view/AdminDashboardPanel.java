package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import com.example.salesmis.controller.DashboardController;
import com.example.salesmis.model.dto.ChartDataDTO;
import com.example.salesmis.model.dto.DashboardDTO;
import com.example.salesmis.model.dto.ExpiringVoucherDTO;
import com.example.salesmis.model.dto.LowStockAlertDTO;

public class AdminDashboardPanel extends JPanel {

    private final DashboardController dashboardController;
    private JLabel lblRevenue, lblOrderCount, lblCustomerCount;
    private JComboBox<String> comboFilter;
    
    private DefaultTableModel stockTableModel, voucherTableModel;
    private JTable stockTable, voucherTable;
    private SimpleBarChartPanel chartPanel;
    private JLabel lblImportCost;

    public AdminDashboardPanel(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
        initComponents();
        loadData("TODAY");
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 1. TOP HEADER & FILTER
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(new JLabel("Thống kê theo: "));
        comboFilter = new JComboBox<>(new String[]{"Ngày hôm nay", "Tuần này", "Tháng này", "Toàn bộ thời gian"});
        comboFilter.addActionListener(e -> {
            int idx = comboFilter.getSelectedIndex();
            String filter = "TODAY";
            if (idx == 1) filter = "THIS_WEEK";
            else if (idx == 2) filter = "THIS_MONTH";
            else if (idx == 3) filter = "ALL_TIME";
            loadData(filter);
        });
        topPanel.add(comboFilter);

        // 2. KPI CARDS
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        lblRevenue = createKpiLabel("0 ₫");
        lblImportCost = createKpiLabel("0 ₫");
        lblOrderCount = createKpiLabel("0");
        lblCustomerCount = createKpiLabel("0");

        kpiPanel.add(createKpiCard("Doanh Thu", lblRevenue, new Color(20, 160, 100)));
        kpiPanel.add(createKpiCard("Chi Phí Nhập", lblImportCost, new Color(220, 80, 80)));
        kpiPanel.add(createKpiCard("Đơn Hàng", lblOrderCount, new Color(50, 120, 220)));
        kpiPanel.add(createKpiCard("Khách Mới", lblCustomerCount, new Color(220, 120, 30)));

        JPanel upperModule = new JPanel(new BorderLayout());
        upperModule.add(topPanel, BorderLayout.NORTH);
        upperModule.add(kpiPanel, BorderLayout.CENTER);

        // 3. ALERTS TABLE
        JPanel leftAlertsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        
        stockTableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên SP", "Tồn", "Tối thiểu"}, 0);
        stockTable = new JTable(stockTableModel);
        JPanel pnlStock = new JPanel(new BorderLayout());
        pnlStock.setBorder(BorderFactory.createTitledBorder("Cảnh Báo Sắp Hết Hàng"));
        pnlStock.add(new JScrollPane(stockTable), BorderLayout.CENTER);

        voucherTableModel = new DefaultTableModel(new String[]{"Voucher", "Trạng thái", "Hết hạn", "Lượt dùng"}, 0);
        voucherTable = new JTable(voucherTableModel);
        JPanel pnlVou = new JPanel(new BorderLayout());
        pnlVou.setBorder(BorderFactory.createTitledBorder("Mã Giảm Giá Sắp Cũ"));
        pnlVou.add(new JScrollPane(voucherTable), BorderLayout.CENTER);

        leftAlertsPanel.add(pnlStock);
        leftAlertsPanel.add(pnlVou);

        // 4. CHART
        chartPanel = new SimpleBarChartPanel();
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createTitledBorder("Biểu đồ Doanh thu"));
        chartContainer.add(chartPanel, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftAlertsPanel, chartContainer);
        splitPane.setDividerLocation(450);
        splitPane.setResizeWeight(0.4);

        add(upperModule, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JLabel createKpiLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        lbl.setForeground(Color.WHITE);
        return lbl;
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color bg) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(bg);
        p.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(240, 240, 240));

        p.add(lblTitle, BorderLayout.NORTH);
        p.add(valueLabel, BorderLayout.CENTER);
        return p;
    }

    private void loadData(String filter) {
        new SwingWorker<DashboardDTO, Void>() {
            @Override
            protected DashboardDTO doInBackground() throws Exception {
                return dashboardController.getDashboardData(filter);
            }

            @Override
            protected void done() {
                try {
                    DashboardDTO dto = get();
                    lblRevenue.setText(String.format("%,.0f ₫", dto.getTotalRevenue()));
                    lblImportCost.setText(String.format("%,.0f ₫", dto.getTotalImportCost()));
                    lblOrderCount.setText(dto.getOrderCount().toString());
                    lblCustomerCount.setText(dto.getCustomerCount().toString());

                    stockTableModel.setRowCount(0);
                    for (LowStockAlertDTO ast : dto.getLowStockAlerts()) {
                        stockTableModel.addRow(new Object[]{ast.getProductCode(), ast.getProductName(), ast.getQuantityStock(), ast.getMinStockLevel()});
                    }

                    voucherTableModel.setRowCount(0);
                    DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    for (ExpiringVoucherDTO ev : dto.getExpiringVouchers()) {
                        voucherTableModel.addRow(new Object[]{ev.getCode(), ev.getStatus(), ev.getEndDate().format(f), ev.getUsageLimit()});
                    }

                    chartPanel.setData(dto.getRevenueChartData());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }
}

class SimpleBarChartPanel extends JPanel {
    private List<ChartDataDTO> data;

    public void setData(List<ChartDataDTO> data) {
        this.data = data;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) {
            g.drawString("Không có dữ liệu", getWidth()/2 - 40, getHeight()/2);
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int pad = 40;
        int w = getWidth() - 2 * pad;
        int h = getHeight() - 2 * pad;

        BigDecimal maxVal = BigDecimal.ONE;
        for (ChartDataDTO d : data) {
            if (d.getTotalRevenue().compareTo(maxVal) > 0) maxVal = d.getTotalRevenue();
            if (d.getTotalImportCost().compareTo(maxVal) > 0) maxVal = d.getTotalImportCost();
        }

        double boxWidth = w / (double) data.size();
        double barGroupWidth = boxWidth * 0.8;
        double singleBarWidth = barGroupWidth / 2.0;

        g2.setColor(Color.LIGHT_GRAY);
        g2.drawLine(pad, getHeight() - pad, getWidth() - pad, getHeight() - pad);
        g2.drawLine(pad, pad, pad, getHeight() - pad);

        for (int i = 0; i < data.size(); i++) {
            ChartDataDTO d = data.get(i);
            
            double revPct = d.getTotalRevenue().doubleValue() / maxVal.doubleValue();
            int revBarH = (int) (revPct * h);
            int revBarX = pad + (int)(i * boxWidth + (boxWidth - barGroupWidth) / 2);
            int revBarY = getHeight() - pad - revBarH;
            
            g2.setColor(new Color(20, 160, 100)); // Doanh Thu (Xanh lá)
            g2.fillRect(revBarX, revBarY, (int)singleBarWidth - 2, revBarH);

            double impPct = d.getTotalImportCost().doubleValue() / maxVal.doubleValue();
            int impBarH = (int) (impPct * h);
            int impBarX = revBarX + (int)singleBarWidth;
            int impBarY = getHeight() - pad - impBarH;
            
            g2.setColor(new Color(220, 120, 30)); // Chi phí nhập (Cam)
            g2.fillRect(impBarX, impBarY, (int)singleBarWidth - 2, impBarH);
        }
        
        // Vẽ chú thích (Legend)
        g2.setColor(new Color(20, 160, 100));
        g2.fillRect(pad + 10, 10, 15, 15);
        g2.setColor(Color.BLACK);
        g2.drawString("Doanh Thu", pad + 30, 22);

        g2.setColor(new Color(220, 120, 30));
        g2.fillRect(pad + 110, 10, 15, 15);
        g2.setColor(Color.BLACK);
        g2.drawString("Chi Phí Nhập", pad + 130, 22);
    }
}
