# HỆ THỐNG QUẢN LÝ CỬA HÀNG THỜI TRANG
Java Swing MVC + Hibernate/JPA + MySQL

________________________________________

## 1. Giới thiệu đề tài
Đây là dự án xây dựng phần mềm quản lý cửa hàng thời trang chạy trên desktop, được phát triển bằng Java Swing, sử dụng mô hình MVC, Hibernate/JPA để kết nối cơ sở dữ liệu MySQL.
Hệ thống hỗ trợ các chức năng chính:
- Đăng nhập hệ thống
- Quản lý sản phẩm và danh mục
- Quản lý kho và nhập hàng
- Quản lý khách hàng
- Quản lý khuyến mãi (Voucher)
- Quản lý nhân viên
- Lập hóa đơn bán hàng (POS)
- Dashboard tổng quan
- Báo cáo doanh thu
- Phân quyền ADMIN / STAFF

________________________________________

## 2. Công nghệ sử dụng
- Java 17
- Java Swing (Thư viện FlatLaf hỗ trợ giao diện)
- Maven
- Hibernate ORM / JPA
- MySQL 8+
- IntelliJ IDEA / VS Code / Eclipse

________________________________________

## 3. Kiến trúc hệ thống
Dự án được tổ chức theo kiến trúc:
- View: Giao diện Java Swing
- Controller: Tiếp nhận thao tác từ giao diện
- Service: Xử lý nghiệp vụ
- Repository: Thao tác dữ liệu qua JPA
- Entity: Ánh xạ đối tượng với bảng trong cơ sở dữ liệu

Luồng chính: View -> Controller -> Service -> Repository -> Database

________________________________________

## 4. Chức năng chính

### 4.1. Đăng nhập
- Đăng nhập bằng tài khoản nhân viên
- Phân quyền ADMIN / STAFF

### 4.2. Quản lý sản phẩm và danh mục
- Thêm, sửa, xóa, tìm kiếm sản phẩm
- Thêm, sửa, xóa, tìm kiếm danh mục sản phẩm
- Gán danh mục cho sản phẩm
- Quản lý thông tin chi tiết: kích thước, màu sắc, chất liệu,...

### 4.3. Quản lý khách hàng
- Thêm khách hàng mới
- Cập nhật thông tin khách hàng
- Tìm kiếm khách hàng
- Theo dõi điểm tích lũy (Loyalty points) cho các lần mua

### 4.4. Lập hóa đơn bán hàng (POS)
- Tích hợp sản phẩm vào giỏ hàng
- Áp dụng mã giảm giá (Voucher)
- Xử lý thanh toán
- Tự động trừ số lượng tồn kho theo sản phẩm đã bán

### 4.5. Quản lý kho và nhập hàng
- Lập phiếu nhập hàng
- Giám sát mức tồn kho (Min/Max Stock Level)
- Tự động cộng số lượng tồn kho khi nhập thêm

### 4.6. Quản lý khuyến mãi (Voucher)
- Tạo mã giảm giá (Voucher) mới
- Đặt hạn mức sử dụng (số lần, giá trị tối thiểu)
- Định cấu hình thời gian áp dụng (Start/End Date)

### 4.7. Quản lý nhân viên
- Chức năng dành riêng cho Admin
- Thêm mới tài khoản nhân viên
- Khóa/mở khóa tài khoản nhân viên

### 4.8. Dashboard và Báo cáo doanh thu
- Thống kê doanh thu bán hàng
- Thống kê số lượng sản phẩm bán ra
- Hiển thị biểu đồ dữ liệu trực quan
- Tính toán chi phí nhập hàng

________________________________________

## 5. Cấu trúc thư mục dự án
- `src/main/java`: Mã nguồn chính
- `src/main/resources`: File cấu hình `persistence.xml` và các tài nguyên khác
- `database`: Các script MySQL khởi tạo cơ sở dữ liệu
- `uml`: File thiết kế UML
- `docs`: Các tài liệu liên quan đến dự án

________________________________________

## 6. Hướng dẫn tạo cơ sở dữ liệu

**Bước 1: Tạo database**
Chạy lần lượt các file script trong thư mục `database`:
1. `01_create_database.sql`
2. `02_create_tables.sql`
3. `03_insert_sample_data.sql`

**Bước 2: Cấu hình lại persistence.xml**
Mở file: `src/main/resources/META-INF/persistence.xml`
Và chỉnh sửa lại các thông số:
- username MySQL
- password MySQL
- JDBC URL nếu cần thiết

Ví dụ:
```xml
<property name="jakarta.persistence.jdbc.url" value="jdbc:mysql://localhost:3306/shop_management?useSSL=false&amp;serverTimezone=Asia/Ho_Chi_Minh&amp;allowPublicKeyRetrieval=true"/>
<property name="jakarta.persistence.jdbc.user" value="root"/>
<property name="jakarta.persistence.jdbc.password" value="YOUR_MYSQL_PASSWORD"/>
```

________________________________________

## 7. Hướng dẫn chạy project

**Cách 1: Chạy bằng IntelliJ IDEA**
1. Mở project trong IntelliJ IDEA
2. Chờ Maven tải dependencies
3. Chạy file `AppLauncher.java`

**Cách 2: Chạy bằng Maven**
Mở terminal tại thư mục project gốc và chạy 2 lệnh sau:
```bash
mvn clean compile
mvn exec:java "-Dexec.mainClass=com.example.salesmis.AppLauncher"
```

________________________________________

## 8. Tài khoản mẫu
**ADMIN**
- Username: `admin`
- Password: `12345`

**STAFF**
- Username: `staff` (hoặc `staff02`)
- Password: `12345`

________________________________________

## 9. Phân quyền
**ADMIN**
- Trải nghiệm toàn bộ chức năng hệ thống.
- Xem dashboard và báo cáo doanh thu.
- Quản lý sản phẩm, danh mục, kho hàng.
- Lập hóa đơn và nhập hàng.
- Quản lý khách hàng.
- Quản lý nhân viên (Thêm mới, khóa/mở khóa tài khoản).
- Quản lý khuyến mãi (Voucher).

**STAFF**
- Truy cập giới hạn vào một số tính năng thao tác bán hàng hàng ngày.
- Xem dashboard (dành cho phần Staff).
- Quản lý sản phẩm, khách hàng.
- Lập hóa đơn bán hàng.
- Không thể thêm nhân viên mới, không thể quản lý voucher.

________________________________________

## 10. UML
Các sơ đồ thiết kế UML (`.puml`) được lưu trong thư mục `uml/` hoặc đính kèm trong báo cáo, bao gồm các loại sơ đồ chính:
- Use Case Diagram (`use_case.puml`)
- Activity Diagram (Các sơ đồ hoạt động: đăng nhập, lập hóa đơn, thống kê,...)
- Sequence Diagram (Các sơ đồ tuần tự: quản lý sản phẩm, đơn hàng, khách hàng,...)

________________________________________

## 11. Thành viên nhóm đề xuất (Nhóm 19)

- **Đông:** Thiết kế Database, Use Case Diagram, thiết kế Activity Diagram, Sequence Diagram và code phần đăng nhập, đăng xuất, quản lý khách hàng, nhân viên. Viết nội dung báo cáo.
- **Mạnh:** Thiết kế Activity Diagram, Sequence Diagram và code phần dashboard staff, lập hóa đơn, quản lý sản phẩm, báo cáo doanh thu. Viết nội dung báo cáo.
- **Quân:** Thiết kế Activity Diagram, Sequence Diagram và code phần dashboard admin, quản lý khuyến mại, nhập hàng, quản lý danh mục. Chỉnh sửa và format hoàn chỉnh báo cáo.

________________________________________

## 12. Các điểm nổi bật của đồ án
- Áp dụng mô hình MVC rõ ràng
- Sử dụng Hibernate / JPA thay vì JDBC thuần với nhiều thao tác
- Các giao dịch (Transaction) được quản lý đồng nhất
- Dashboard trực quan hiện đại, sử dụng FlatLaf (Web-like Flat Design) nâng tầm thiết kế
- Giao diện dễ nhìn, trải nghiệm thao tác tốt.

________________________________________

## 13. Hướng phát triển thêm
- Xây dựng app tích điểm phiên bản mobile cho khách hàng
- Cảnh báo trực tiếp cho các tồn kho sắp hết/đã hết qua email
- Đăng nhập nâng cao sử dụng nhận diện sinh trắc hoặc 2FA
- Quản lý ca làm việc (shifts) nhân viên

________________________________________

## 14. Kết luận
Dự án được xây dựng là một bài học thực tế lớn, minh họa rõ việc xây dựng một hệ thống POS hiện đại bằng công nghệ Java (Swing / Hibernate). Dự án đáp ứng tốt nhu cầu quản lý thời trang đa chiều với sự phân tách kiến trúc để bảo trì dễ dàng và tăng hiệu suất theo thời gian.
