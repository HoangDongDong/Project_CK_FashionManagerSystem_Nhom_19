package com.example.salesmis.model.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "Staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Integer staffId;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true)
    private Account account;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "hire_date")
    private LocalDateTime hireDate;

    @OneToMany(mappedBy = "staff")
    private List<OrderEntity> orders = new ArrayList<>();

    @OneToMany(mappedBy = "staff")
    private List<ImportReceipt> importReceipts = new ArrayList<>();

    public Integer getStaffId() { return staffId; }
    public void setStaffId(Integer staffId) { this.staffId = staffId; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }
    public List<OrderEntity> getOrders() { return orders; }
    public void setOrders(List<OrderEntity> orders) { this.orders = orders; }
    public List<ImportReceipt> getImportReceipts() { return importReceipts; }
    public void setImportReceipts(List<ImportReceipt> importReceipts) { this.importReceipts = importReceipts; }
}
