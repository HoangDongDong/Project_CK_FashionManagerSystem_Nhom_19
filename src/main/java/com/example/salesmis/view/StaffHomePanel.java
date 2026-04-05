package com.example.salesmis.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.example.salesmis.model.entity.Account;

public class StaffHomePanel extends JPanel {

    public StaffHomePanel(Account account) {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("HỒ SƠ NHÂN VIÊN", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        add(title, BorderLayout.NORTH);

        JPanel profileCard = new JPanel(new GridLayout(6, 2, 10, 20));
        profileCard.setBorder(BorderFactory.createTitledBorder("Thông tin cá nhân"));

        addLabelValue(profileCard, "Tài khoản:", account.getUsername());
        addLabelValue(profileCard, "Phân quyền:", account.getRoleName());
        
        if (account.getStaff() != null) {
            addLabelValue(profileCard, "Mã nhân viên (Staff ID):", String.valueOf(account.getStaff().getStaffId()));
            addLabelValue(profileCard, "Họ và Tên:", account.getStaff().getFullName());
            addLabelValue(profileCard, "Số điện thoại:", account.getStaff().getPhone());
            addLabelValue(profileCard, "Email:", account.getStaff().getEmail());
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            addLabelValue(profileCard, "Ngày vào làm:", account.getStaff().getHireDate() != null ? account.getStaff().getHireDate().format(f) : "N/A");
            addLabelValue(profileCard, "Địa chỉ:", account.getStaff().getAddress());
        } else {
            profileCard.add(new JLabel("Lỗi: Tài khoản không liên kết với thông tin nhân sự."));
        }

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.add(profileCard);
        add(wrapper, BorderLayout.CENTER);
    }

    private void addLabelValue(JPanel p, String L1, String L2) {
        JLabel lbp = new JLabel(" " + L1);
        lbp.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel lbv = new JLabel(L2);
        lbv.setFont(new Font("SansSerif", Font.PLAIN, 14));
        p.add(lbp);
        p.add(lbv);
    }
}
