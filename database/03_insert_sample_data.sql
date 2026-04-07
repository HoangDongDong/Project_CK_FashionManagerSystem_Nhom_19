USE shop_management;

-- ================= 1. ACCOUNT =================
-- Tạo 1 tài khoản Admin và 2 tài khoản Staff
INSERT INTO Account (username, password_hash, role_name, is_active) VALUES
('admin', '12345', 'ADMIN', TRUE),
('staff', '12345', 'STAFF', TRUE),
('staff02', '12345', 'STAFF', TRUE);

-- ================= 2. ADMIN =================
-- Liên kết với account_id = 1
INSERT INTO Admin (account_id, full_name, phone, email, address, management_level) VALUES
(1, 'Nguyễn Văn Quản Lý', '0901234567', 'admin@shop.com', '123 Lê Lợi, Q1, TP.HCM', 1);

-- ================= 3. STAFF =================
-- Liên kết với account_id = 2 và 3
INSERT INTO Staff (account_id, full_name, phone, email, address, hire_date) VALUES
(2, 'Trần Thị Bán Hàng', '0912345678', 'staff1@shop.com', '456 Hai Bà Trưng, Q3, TP.HCM', '2023-01-15 08:00:00'),
(3, 'Lê Văn Kho', '0923456789', 'staff2@shop.com', '789 Nguyễn Đình Chiểu, Q3, TP.HCM', '2023-02-20 08:00:00');

-- ================= 4. CUSTOMER =================
INSERT INTO Customer (customer_name, phone, email, address, loyalty_points, is_active) VALUES
('Phạm Khách Hàng A', '0987654321', 'khacha@gmail.com', '111 Lý Thường Kiệt, Tân Bình, TP.HCM', 150, TRUE),
('Hoàng Khách Hàng B', '0976543210', 'khachb@gmail.com', '222 CMT8, Q10, TP.HCM', 50, TRUE),
('Đinh Khách Hàng C', '0965432109', 'khachc@gmail.com', '333 Điện Biên Phủ, Bình Thạnh, TP.HCM', 0, TRUE);

-- ================= 5. SUPPLIER =================
INSERT INTO Supplier (supplier_name, contact_person, phone, email, address, tax_code) VALUES
('Công ty TNHH Cung Cấp Áo Quần', 'Nguyễn Cung Cấp', '02811112222', 'contact@aoquan.vn', 'KCN Tân Tạo, Bình Tân, TP.HCM', '0312345678'),
('Cửa hàng Sỉ Phụ Kiện', 'Trần Phụ Kiện', '02833334444', 'si@phukien.vn', 'Chợ Lớn, Q5, TP.HCM', '0387654321');

-- ================= 6. CATEGORY =================
INSERT INTO Category (category_name, description) VALUES
('Áo Thun', 'Các loại áo thun nam nữ'),
('Quần Jean', 'Quần Jean các loại'),
('Phụ Kiện', 'Nón, thắt lưng, ví');

-- ================= 7. PRODUCT =================
-- Liên kết với category_id
INSERT INTO Product (category_id, product_code, product_name, base_price, description, is_active) VALUES
(1, 'AT001', 'Áo thun basic nam', 150000.00, 'Áo thun cotton 100% thoáng mát', TRUE),
(1, 'AT002', 'Áo thun in logo nữ', 180000.00, 'Áo thun nữ form rộng', TRUE),
(2, 'QJ001', 'Quần Jean nam rách gối', 350000.00, 'Quần Jean xanh cá tính', TRUE),
(3, 'PK001', 'Thắt lưng da bò', 250000.00, 'Thắt lưng da thật nguyên miếng', TRUE),
(2, 'QJ002', 'Quần Jean Slimfit', 400000.00, 'Quần Jean nam Slimfit', TRUE);

-- ================= 8. PRODUCT ATTRIBUTE =================
-- Liên kết với product_id
INSERT INTO ProductAttribute (product_id, material, size, color, weight) VALUES
(1, 'Cotton', 'L', 'Trắng', 0.2),
(1, 'Cotton', 'XL', 'Đen', 0.2),
(2, 'Cotton blend', 'M', 'Hồng', 0.15),
(3, 'Denim', '32', 'Xanh dương', 0.5),
(4, 'Da thật', 'Free', 'Nâu', 0.3);

-- ================= 9. INVENTORY =================
-- Liên kết 1-1 với product_id
INSERT INTO Inventory (product_id, quantity_stock, min_stock_level, max_stock_level, last_updated) VALUES
(1, 100, 20, 500, NOW()),
(2, 50, 10, 300, NOW()),
(3, 30, 10, 200, NOW()),
(4, 80, 15, 400, NOW()),
(5, 2, 5, 100, NOW());

-- ================= 10. VOUCHER =================
INSERT INTO Voucher (code, discount_value, discount_type, start_date, end_date, usage_limit) VALUES
('GIAM50K', 50000.00, 'FIXED', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 100),
('GIAM10PT', 10.00, 'PERCENTAGE', '2024-01-01 00:00:00', '2024-12-31 23:59:59', 50),
('NAM2023', 100000.00, 'FIXED', '2023-01-01 00:00:00', '2023-12-31 23:59:59', 10);

-- ================= 11. ORDER =================
-- Liên kết với staff_id, customer_id, voucher_id
INSERT INTO `Order` (staff_id, customer_id, voucher_id, order_number, order_date, status, total_amount, discount_amount, final_total, note) VALUES
(1, 1, 1, 'ORD2024001', NOW(), 'COMPLETED', 650000.00, 50000.00, 600000.00, 'Khách hàng VIP, giao hàng cẩn thận'),
(1, 2, NULL, 'ORD2024002', NOW(), 'PENDING', 150000.00, 0.00, 150000.00, 'Giao giờ hành chính'),
(1, 3, 2, 'ORD2024003', 'COMPLETED', 360000.00, 36000.00, 324000.00),
(2, 1, NULL, 'ORD2024004', NOW(), 'CANCELLED', 250000.00, 0.00, 250000.00, 'Đơn hàng bị hủy');

-- ================= 12. ORDER DETAIL =================
-- Liên kết với order_id, product_id
-- Order 1: 2 Áo thun (150k) + 1 Quần Jean (350k) = 650k
INSERT INTO OrderDetail (order_id, product_id, quantity, unit_price, subtotal) VALUES
(1, 1, 2, 150000.00, 300000.00),
(1, 3, 1, 350000.00, 350000.00),
(3, 2, 2, 180000.00, 360000.00);

-- Order 2: 1 Áo thun (150k) = 150k
INSERT INTO OrderDetail (order_id, product_id, quantity, unit_price, subtotal) VALUES
(2, 1, 1, 150000.00, 150000.00);

-- ================= 13. PAYMENT =================
-- Liên kết 1-1 với order_id
INSERT INTO Payment (order_id, method, amount, payment_date, status) VALUES
(1, 'CASH', 600000.00, NOW(), 'PAID'),
(2, 'BANK_TRANSFER', 150000.00, NULL, 'UNPAID');

-- ================= 14. IMPORT RECEIPT =================
-- Liên kết với supplier_id, staff_id
INSERT INTO ImportReceipt (supplier_id, staff_id, import_date, total_cost, status, note) VALUES
(1, 2, '2024-05-01 10:00:00', 10000000.00, 'COMPLETED', 'Nhập lô hàng mùa hè đầu tiên'),
(2, 2, '2024-05-15 14:30:00', 4000000.00, 'COMPLETED', 'Nhập bổ sung thắt lưng');

-- ================= 15. IMPORT DETAIL =================
-- Liên kết với receipt_id, product_id
-- Receipt 1: Nhập 100 áo thun giá 100k
INSERT INTO ImportDetail (receipt_id, product_id, quantity, import_price, subtotal) VALUES
(1, 1, 100, 100000.00, 10000000.00);

-- Receipt 2: Nhập 20 thắt lưng giá 200k
INSERT INTO ImportDetail (receipt_id, product_id, quantity, import_price, subtotal) VALUES
(2, 4, 20, 200000.00, 4000000.00);