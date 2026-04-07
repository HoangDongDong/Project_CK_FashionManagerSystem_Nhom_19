# 👗 Fashion Management System (Nhom 19)

Hệ thống Quản lý Cửa hàng Thời trang (Fashion Management System) được xây dựng bằng **Java Swing** và **Hibernate (JPA)**, sử dụng cơ sở dữ liệu **MySQL**. Dự án cung cấp một giải pháp toàn diện để quản lý bán hàng, kho, nhân viên, khách hàng và báo cáo doanh thu với giao diện hiện đại (được tùy biến nâng cao bằng thư viện FlatLaf).

---

## 🚀 Tính năng nổi bật (Features)

Hệ thống được phân quyền với 2 vai trò chính: **Admin (Quản lý)** và **Staff (Nhân viên)**.

- **Quản lý Bán hàng (POS / Lập hóa đơn):** Tích hợp sản phẩm vào giỏ, áp dụng mã giảm giá (Voucher), xử lý thanh toán và in hoá đơn.
- **Quản lý Sản phẩm & Danh mục:** Thêm, sửa, xoá và theo dõi sản phẩm theo các phân loại danh mục.
- **Quản lý Kho (Inventory) & Nhập hàng:** Lập phiếu nhập hàng, giám sát mức tồn kho tối thiểu/tối đa (Min/Max Stock Level), tự động cập nhật số lượng tồn khi bán hoặc nhập thêm.
- **Quản lý Khách hàng:** Theo dõi thông tin khách hàng, ghi nhận điểm tích luỹ (Loyalty points) cho các lần mua.
- **Quản lý Nhân viên:** Dành riêng cho Admin để thêm mới, khóa/mở khóa tài khoản bảo mật của nhân viên.
- **Khuyến mãi (Voucher):** Đặt hạn mức sử dụng (Số lần, Giá trị tối thiểu) và lên lịch áp dụng (Start/End Date).
- **Thống kê & Báo cáo:** Dashboard tổng quan hỗ trợ xuất báo cáo doanh thu, chi phí nhập hàng và biểu đồ trực quan (Biểu đồ số liệu thời gian thực).

---

## 🛠️ Công nghệ sử dụng (Tech Stack)

- **Ngôn ngữ:** Java 17
- **Giao diện (GUI):** Java Swing + FlatLaf (Web-like Flat Design)
- **Cơ sở dữ liệu:** MySQL 8.x
- **ORM / Database Tool:** Hibernate ORM 6.4 + Jakarta Persistence API (JPA) 3.1
- **Trình quản lý gói:** Apache Maven

---

## ⚙️ Yêu cầu môi trường (Prerequisites)

- JDK 17 hoặc mới hơn.
- Apache Maven (được cài đặt và cấu hình trong PATH).
- MySQL Server đang hoạt động.
- Trình quản lý CSDL (Ví dụ: MySQL Workbench, DBeaver) để nạp Database ban đầu.

---

## 📥 Cài đặt và Khởi chạy (Installation & Setup)

### Bước 1: Thiết lập Cơ sở dữ liệu
1. Mở hệ quản trị MySQL của bạn.
2. Chạy đoạn script trong file `DB.sql` được đính kèm ở thư mục gốc để khởi tạo cấu trúc các bảng (Schema: `shop_management`) và dữ liệu mẫu (nếu có).

### Bước 2: Cấu hình kết nối (Database connection)
1. Hãy đảm bảo thông tin đăng nhập trong tệp cấu hình JPA (`src/main/resources/META-INF/persistence.xml`) khớp với máy của bạn:
   ```xml
   <property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/shop_management"/>
   <property name="jakarta.persistence.jdbc.user" value="root"/>
   <property name="jakarta.persistence.jdbc.password" value="your_password_here"/>
   ```

### Bước 3: Biên dịch & Tải thư viện
Sử dụng Maven để dọn dẹp và tải tất cả các thư viện cần thiết (bao gồm Hibernate, MySQL Connector, FlatLaf,...):
```bash
mvn clean compile
```

### Bước 4: Chạy Ứng dụng
Bạn có thể chạy dự án bằng lệnh Maven:
```bash
mvn compile exec:java "-Dexec.mainClass=com.example.salesmis.AppLauncher"
```
*Hoặc mở dự án bằng IDE (VS Code, IntelliJ IDEA, Eclipse) và nhấn `Run` ở file `AppLauncher.java`.*

---

## 👥 Nhóm phát triển (Nhom 19)
- *Dự án môn học / Hệ thống được phát triển chuyên biệt cho ngành học: Ngôn ngữ lập trình tiên tiến.*
