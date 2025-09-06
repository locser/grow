## 🎯 **TÍNH TOÁN DUNG LƯỢNG DATABASE - QUICK GUIDE**

### **📊 Kích thước Data Types cơ bản:**

```sql
-- MySQL/PostgreSQL Data Types
TINYINT     = 1 byte   (0 - 255)
SMALLINT    = 2 bytes  (-32K - 32K)
MEDIUMINT   = 3 bytes  (-8M - 8M)     -- MySQL only
INT         = 4 bytes  (-2B - 2B)
BIGINT      = 8 bytes  (-9 × 10^18)

VARCHAR(50) = 50 bytes + 1-2 bytes overhead
TEXT        = variable + 2-4 bytes overhead
DECIMAL(10,2) = 5 bytes
DATE        = 3 bytes (MySQL) / 4 bytes (PostgreSQL)
DATETIME    = 8 bytes
BOOLEAN     = 1 byte
```

### **💡 Tính toán 1 Million Records:**

**Ví dụ bảng Users:**

```sql
CREATE TABLE users (
    id INT,           -- 4 bytes
    name VARCHAR(50), -- 52 bytes (50 + 2 overhead)
    email VARCHAR(100), -- 102 bytes
    age TINYINT,      -- 1 byte
    created_at DATETIME -- 8 bytes
);
-- Total per row: 167 bytes
```

**Calculation:**

- 1 record = 167 bytes
- 1M records = 167 × 1,000,000 = **167 MB**
- - Index overhead (~30%) = **217 MB**
- - MySQL overhead (~20%) = **260 MB total**

### **🚀 Optimization Strategies:**

#### **1. Chọn Data Type tối ưu:**

```sql
-- ❌ Lãng phí
user_id BIGINT        -- 8 bytes
status VARCHAR(20)    -- 22 bytes

-- ✅ Tối ưu
user_id INT           -- 4 bytes (đủ cho 2B users)
status ENUM('active','inactive') -- 1 byte
```

#### **2. Normalized vs Denormalized:**

```sql
-- Normalized (ít storage, nhiều JOIN)
users: id, name, email
orders: id, user_id, total
order_items: id, order_id, product_id, quantity

-- Denormalized (nhiều storage, ít JOIN)
order_summary: id, user_name, user_email, total, items_json
```

### **📈 Growth Planning Formula:**

```
Year 1: Base_Size × (1 + Growth_Rate)
Year 2: Year_1_Size × (1 + Growth_Rate)
Year 3: Year_2_Size × (1 + Growth_Rate)

Với safety buffer 30%:
Final_Size = Calculated_Size × 1.3
```

**Ví dụ thực tế:**

- Startup: 10K users/tháng → 120K users/năm
- Scale-up: 100K users/tháng → 1.2M users/năm
- Enterprise: 1M users/tháng → 12M users/năm

### **🛠 Best Practices thiết kế:**

#### **Table Design:**

```sql
-- ✅ Tối ưu storage
CREATE TABLE orders (
    id INT AUTO_INCREMENT,           -- 4 bytes
    user_id INT,                     -- 4 bytes
    status ENUM('pending','paid'),   -- 1 byte
    total DECIMAL(10,2),             -- 5 bytes
    created_at TIMESTAMP,            -- 4 bytes

    PRIMARY KEY (id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_created (created_at)
) ENGINE=InnoDB;
```

#### **Partitioning cho bảng lớn:**

```sql
-- Partition theo thời gian
CREATE TABLE user_activities (
    id BIGINT,
    user_id INT,
    activity_date DATE,
    action VARCHAR(50)
) PARTITION BY RANGE (YEAR(activity_date)) (
    PARTITION p2023 VALUES LESS THAN (2024),
    PARTITION p2024 VALUES LESS THAN (2025),
    PARTITION p2025 VALUES LESS THAN (2026)
);
```

### **⚡ Quick Sizing Cheat Sheet:**

| Records  | Basic Table | With Indexes | With Growth Buffer |
| -------- | ----------- | ------------ | ------------------ |
| **10K**  | 1.6 MB      | 2.1 MB       | 2.7 MB             |
| **100K** | 16 MB       | 21 MB        | 27 MB              |
| **1M**   | 160 MB      | 208 MB       | 270 MB             |
| **10M**  | 1.6 GB      | 2.1 GB       | 2.7 GB             |
| **100M** | 16 GB       | 21 GB        | 27 GB              |

### **🎯 Key Takeaways:**

1. **INT đủ cho hầu hết ID** (2 billion records)
2. **ENUM cho status** thay vì VARCHAR
3. **Index tăng 30% storage** nhưng cần thiết cho performance
4. **Buffer 30%** cho growth và overhead
5. **Partition bảng > 100M records**
6. **Monitor growth hàng quý** để adjust capacity

**Bottom line:** 1M records thường ~ 200-300 MB với thiết kế hợp lý! 🚀
