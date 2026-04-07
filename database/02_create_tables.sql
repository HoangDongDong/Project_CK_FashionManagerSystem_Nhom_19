USE shop_management;

-- ================= ACCOUNT =================
CREATE TABLE Account (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role_name VARCHAR(20),
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ================= STAFF =================
CREATE TABLE Staff (
    staff_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT UNIQUE,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    hire_date DATETIME,

    FOREIGN KEY (account_id) REFERENCES Account(account_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ================= CUSTOMER =================

CREATE TABLE Customer (
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    loyalty_points INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ================= SUPPLIER =================
CREATE TABLE Supplier (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(100),
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    tax_code VARCHAR(50)
);

-- ================= CATEGORY =================
CREATE TABLE Category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100),
    description TEXT
);

-- ================= PRODUCT =================
CREATE TABLE Product (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    product_code VARCHAR(50),
    product_name VARCHAR(100),
    base_price DECIMAL(10,2),
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (category_id) REFERENCES Category(category_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ================= PRODUCT ATTRIBUTE =================
CREATE TABLE ProductAttribute (
    attribute_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    material VARCHAR(50),
    size VARCHAR(20),
    color VARCHAR(30),
    weight DECIMAL(10,2),

    FOREIGN KEY (product_id) REFERENCES Product(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ================= INVENTORY =================
CREATE TABLE Inventory (
    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT UNIQUE,
    quantity_stock INT DEFAULT 0,
    min_stock_level INT,
    max_stock_level INT,
    last_updated DATETIME,

    FOREIGN KEY (product_id) REFERENCES Product(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ================= VOUCHER =================
CREATE TABLE Voucher (
    voucher_id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) UNIQUE,
    discount_value DECIMAL(10,2),
    discount_type VARCHAR(20),
    start_date DATETIME,
    end_date DATETIME,
    usage_limit INT
);
-- ================= ORDER =================
CREATE TABLE `Order` (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    staff_id INT,
    customer_id INT,
    voucher_id INT NULL,
    order_number VARCHAR(50),
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20),
    total_amount DECIMAL(10,2),
    discount_amount DECIMAL(10,2),
    final_total DECIMAL(10,2),
    note TEXT,

    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    FOREIGN KEY (voucher_id) REFERENCES Voucher(voucher_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ================= ORDER DETAIL =================
CREATE TABLE OrderDetail (
    order_detail_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_id INT,
    quantity INT,
    unit_price DECIMAL(10,2),
    subtotal DECIMAL(10,2),

    FOREIGN KEY (order_id) REFERENCES `Order`(order_id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    FOREIGN KEY (product_id) REFERENCES Product(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ================= PAYMENT =================
CREATE TABLE Payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT UNIQUE,
    method VARCHAR(50),
    amount DECIMAL(10,2),
    payment_date DATETIME,
    status VARCHAR(20),

    FOREIGN KEY (order_id) REFERENCES `Order`(order_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ================= IMPORT RECEIPT =================
CREATE TABLE ImportReceipt (
    receipt_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_id INT,
    staff_id INT,
    import_date DATETIME,
    total_cost DECIMAL(10,2),
    status VARCHAR(20),
    note TEXT,

    FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id)
        ON DELETE SET NULL ON UPDATE CASCADE,

    FOREIGN KEY (staff_id) REFERENCES Staff(staff_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ================= IMPORT DETAIL =================
CREATE TABLE ImportDetail (
    detail_id INT AUTO_INCREMENT PRIMARY KEY,
    receipt_id INT,
    product_id INT,
    quantity INT,
    import_price DECIMAL(10,2),
    subtotal DECIMAL(10,2),

    FOREIGN KEY (receipt_id) REFERENCES ImportReceipt(receipt_id)
        ON DELETE CASCADE ON UPDATE CASCADE,

    FOREIGN KEY (product_id) REFERENCES Product(product_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT UNIQUE, -- Liên kết 1-1 với Account
    full_name VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    management_level INT DEFAULT 1, -- Cấp độ quản lý (nếu có nhiều admin)

    FOREIGN KEY (account_id) REFERENCES Account(account_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);
