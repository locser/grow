# Tips Javascript

[KHI NÀO SHARDING TABLE LỚN trong MYSQL? Đây là 2 cách, VẤN ĐỀ phiền toái bắt đầu từ đây](https://www.youtube.com/watch?v=xya2ClZWbNM)

- Vấn đề: Bảng có dữ liệu lớn 10 triệu record
- cách 1: Chia ra thành 2 bảng nhỏ, mỗi bảng 5 triệu record
- cách 2: Chia dữ liệu cột thuộc về 1 bảng, chia dữ liệu cột khác về 1 bảng

[MYSQL BACKEND: Tối ưu hoá phân trang từ 7s còn 1s với Table có 10.000.000 dữ liệu, SẾP tăng lương...](https://www.youtube.com/watch?v=tjT4O5HGIEU)

## Deferred Joins

Deferred joins is a pagination optimization technique that helps improve query performance when dealing with large datasets. Instead of fetching all columns in one heavy query, it breaks the query into two steps:

1. First query: Fetch only the IDs using the original filtering and sorting
2. Second query: Join back to get the full data using the IDs from step 1

### Example:

Consider a table with millions of records:

```sql
CREATE TABLE orders (
    id BIGINT PRIMARY KEY,
    customer_name VARCHAR(255),
    order_date DATETIME,
    status VARCHAR(50),
    total_amount DECIMAL(10,2),
    -- many more columns
);
```

#### Traditional Pagination (Slow):

```sql
SELECT *
FROM orders
WHERE status = 'completed'
ORDER BY order_date DESC
LIMIT 10 OFFSET 1000000;
```

#### Deferred Join Approach (Fast):

```sql
-- Step 1: Get only IDs first
SELECT id
FROM orders
WHERE status = 'completed'
ORDER BY order_date DESC
LIMIT 10 OFFSET 1000000;

-- Step 2: Use these IDs to get full data
SELECT o.*
FROM orders o
JOIN (
    SELECT id
    FROM orders
    WHERE status = 'completed'
    ORDER BY order_date DESC
    LIMIT 10 OFFSET 1000000
) AS tmp ON o.id = tmp.id;
```

### Benefits:

1. Reduces the amount of data scanned in the first query
2. Makes better use of indexes
3. Significantly improves performance for deep pagination
4. Memory efficient as it processes less data

### Best Used When:

- Table has many columns
- Dealing with deep pagination (large OFFSET values)
- Table has millions of records
- Query involves complex JOINs or heavy columns

// ... rest of the content ...

```

The key advantage of deferred joins is that it minimizes the data that needs to be sorted and processed during the pagination operation. Instead of sorting and processing all columns for all rows that match the WHERE clause, it first gets just the IDs, which is much lighter and faster, then uses these IDs to fetch the complete rows in a second step.

This technique can turn a query that takes several seconds into one that completes in milliseconds, especially when dealing with deep pagination (large OFFSET values) in tables with millions of records.
```

(MySQL - Thực tế INDEX được sử dụng sai nhiều? Người phỏng vấn chia sẻ CÔNG THỨC TỐI ƯU rất tốt.)[https://www.youtube.com/watch?v=UYmCFPVx-XY]

key: composite index , type of index and how to use it

## Composite Index

A composite index (also called compound index or multi-column index) is an index that includes multiple columns. The order of columns in a composite index is crucial for query optimization.

### Example:

```sql
CREATE TABLE users (
    id INT PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(100),
    city VARCHAR(50)
);

-- Creating a composite index
CREATE INDEX idx_name_city ON users(last_name, first_name, city);
```

### Key Points:

1. **Column Order Matters**

   - The index works for queries in left-to-right order
   - Can be used for:
     ```sql
     WHERE last_name = 'Smith'
     WHERE last_name = 'Smith' AND first_name = 'John'
     WHERE last_name = 'Smith' AND first_name = 'John' AND city = 'New York'
     ```
   - Won't use index efficiently for:
     ```sql
     WHERE first_name = 'John'  -- Skips first column
     WHERE city = 'New York'    -- Skips first two columns
     ```

2. **Best Practices**

   - Put most frequently used columns first
   - Put columns with high selectivity first
   - Consider query patterns in your application
   - Maximum 4-5 columns (typically)

3. **Common Use Cases**

   ```sql
   -- Good use of composite index
   SELECT * FROM users
   WHERE last_name = 'Smith'
   AND first_name = 'John';

   -- Sorting can also use composite index
   SELECT * FROM users
   ORDER BY last_name, first_name;
   ```

4. **Benefits**

   - Reduces number of indexes needed
   - Improves query performance
   - Optimizes WHERE clauses and ORDER BY statements
   - Saves storage space compared to multiple single-column indexes

5. **Limitations**
   - Cannot be used if leading columns are not in the query
   - Takes more space than single-column indexes
   - Updates on indexed columns are slower

````

Composite indexes are particularly useful when your queries frequently filter or sort by multiple columns together. They can significantly improve query performance when used correctly, but their effectiveness depends heavily on the column order and how your queries are structured.

I'll explain the main types of indexes in MySQL and how to use them effectively.

```markdown:/Users/loCser/Desktop/algori/design/database/mysql.md
## Types of MySQL Indexes

### 1. B-Tree Index (Default)
- Most common type of index
- Good for equality, range, and prefix searches
```sql
CREATE TABLE products (
    id INT PRIMARY KEY,
    name VARCHAR(100)
);
CREATE INDEX idx_name ON products(name);
````

- Best for: `=`, `>`, `<`, `>=`, `<=`, `BETWEEN`, `LIKE 'prefix%'`

### 2. Hash Index

- Only exact equality comparisons
- Faster than B-tree for exact lookups
- Memory storage engine only

```sql
CREATE TABLE cache (
    key_id INT,
    value VARCHAR(100),
    INDEX USING HASH (key_id)
);
```

- Best for: `=`, `<=>`

### 3. Fulltext Index

- Designed for text searching in text columns

```sql
CREATE TABLE articles (
    id INT PRIMARY KEY,
    title VARCHAR(200),
    content TEXT,
    FULLTEXT INDEX ft_content (content)
);

-- Usage
SELECT * FROM articles
WHERE MATCH(content) AGAINST('search terms');
```

### 4. Spatial Index (R-Tree)

- For geometric data types

```sql
CREATE TABLE locations (
    id INT PRIMARY KEY,
    location POINT NOT NULL,
    SPATIAL INDEX idx_location (location)
);
```

### Usage Guidelines

1. **Primary Key Index**
   - Automatically created
   - Usually auto-incrementing integer

```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100)
);
```

2. **Unique Index**
   - Ensures column values are unique

```sql
CREATE TABLE employees (
    id INT PRIMARY KEY,
    email VARCHAR(100),
    UNIQUE INDEX idx_email (email)
);
```

3. **Covering Index**
   - Includes all columns needed by query

```sql
CREATE INDEX idx_covering ON orders(order_date, status, total);
-- Query that can use covering index
SELECT order_date, status, total
FROM orders
WHERE order_date > '2023-01-01';
```

### Best Practices

1. **When to Create Index**

   - Columns used in WHERE clauses
   - JOIN conditions
   - ORDER BY or GROUP BY columns
   - High selectivity columns

2. **When to Avoid Index**

   - Small tables
   - Columns with low selectivity
   - Frequently updated columns
   - Wide indexes (many columns)

3. **Monitoring**

```sql
-- Check index usage
SHOW INDEX FROM table_name;

-- Analyze query performance
EXPLAIN SELECT * FROM table_name WHERE condition;
```

4. **Maintenance**

```sql
-- Analyze table
ANALYZE TABLE table_name;

-- Optimize table
OPTIMIZE TABLE table_name;
```

````

These different types of indexes serve different purposes and should be chosen based on your specific use case, data type, and query patterns. The key is to balance between query performance improvement and maintenance overhead.


I'll explain about the EXPLAIN command in MySQL and its different types of query execution methods.

```markdown:/Users/loCser/Desktop/algori/design/database/mysql.md
## MySQL EXPLAIN Types

When using EXPLAIN to analyze queries, the 'type' column shows how MySQL joins the tables. Here are the types from best to worst performance:

### 1. system
- Table has only one row
- Best possible type
- Very rare
```sql
EXPLAIN SELECT * FROM single_row_table;
````

### 2. const

- One matching row found using index
- Used with PRIMARY KEY or UNIQUE index

```sql
EXPLAIN SELECT * FROM users WHERE id = 1;
```

### 3. eq_ref

- One row read from this table for each combination of rows from previous tables
- Used in joins with PRIMARY KEY or UNIQUE NOT NULL index

```sql
EXPLAIN SELECT * FROM orders o
JOIN users u ON u.id = o.user_id;
```

### 4. ref

- All matching rows from index are read
- Used with non-unique indexes

```sql
EXPLAIN SELECT * FROM users
WHERE status = 'active';
```

### 5. range

- Index used to retrieve rows within a specific range
- Common with <, >, BETWEEN, IN, etc.

```sql
EXPLAIN SELECT * FROM products
WHERE price BETWEEN 10 AND 20;
```

### 6. index

- Full index scan
- Better than full table scan

```sql
EXPLAIN SELECT name FROM users
ORDER BY name;
```

### 7. ALL

- Full table scan
- Worst performance
- Should be avoided for large tables

```sql
EXPLAIN SELECT * FROM users
WHERE non_indexed_column = 'value';
```

### How to Use EXPLAIN

1. **Basic Usage**

```sql
EXPLAIN SELECT * FROM users WHERE email = 'test@example.com';
```

2. **Detailed Analysis**

```sql
EXPLAIN FORMAT=JSON SELECT * FROM users
JOIN orders ON users.id = orders.user_id;
```

3. **Analyzing Index Usage**

```sql
EXPLAIN SELECT * FROM products
WHERE category = 'electronics'
AND price > 100;
```

### Key Metrics to Watch

1. **rows**: Estimated number of rows examined
2. **filtered**: Percentage of rows filtered by table condition
3. **Extra**: Additional information about query execution
4. **key**: Index actually chosen by MySQL
5. **key_len**: Length of the key used

### Best Practices

1. Look for:

   - Full table scans (type = ALL)
   - Missing indexes
   - High number of rows examined

2. Optimize when you see:
   - type = ALL on large tables
   - No indexes being used (key = NULL)
   - High rows count relative to result set

```

Understanding EXPLAIN types helps in query optimization by showing how MySQL executes your queries and where improvements can be made through proper indexing or query restructuring.
```

Rất hay, đây là phần **cốt lõi** trong việc tối ưu hiệu suất cho hệ thống có nhiều truy cập song song. Dưới đây là giải thích rõ ràng và dễ nhớ về **4 mức độ cô lập trong Transaction** (Isolation Levels) trong MySQL, kèm theo so sánh về **hiệu suất** và **độ an toàn dữ liệu**.

---

## 📊 Bảng tổng quan 4 mức độ Isolation

| Mức độ isolation     | Hiện tượng tránh được                                      | Hiệu suất     | Ghi chú                             |
| -------------------- | ---------------------------------------------------------- | ------------- | ----------------------------------- |
| **READ UNCOMMITTED** | ❌ Dirty Read                                              | 🔥 Rất nhanh  | Không an toàn                       |
| **READ COMMITTED**   | ✅ Dirty Read<br>❌ Non-repeatable Read                    | ⚡ Nhanh      | Mức phổ biến (Oracle dùng mặc định) |
| **REPEATABLE READ**  | ✅ Dirty + Non-repeatable<br>❌ Phantom Read (trong MySQL) | 🟡 Trung bình | Mặc định của MySQL InnoDB           |
| **SERIALIZABLE**     | ✅ Mọi hiện tượng                                          | 🐢 Chậm nhất  | Khóa toàn bảng, ít dùng thực tế     |

---

## 🔍 Giải thích chi tiết từng mức

---

### 1. 🔴 **READ UNCOMMITTED** (Đọc chưa commit)

- **Cho phép đọc dữ liệu chưa commit** từ transaction khác.
- **Rất nhanh** → gần như không có khóa.
- Rất **dễ lỗi logic, tính toán sai**.

🧠 Ví dụ: Bạn đọc số dư từ A = 1,000, nhưng ngay sau đó transaction kia rollback → thực tế số dư không thay đổi.

---

### 2. 🟠 **READ COMMITTED** (Đọc đã commit)

- **Không đọc dữ liệu chưa commit.**
- Nhưng có thể **đọc dữ liệu khác nhau giữa 2 lần truy vấn** nếu transaction khác commit vào giữa.

🧠 Ví dụ: Bạn đọc số dư = 1,000, sau đó đọc lại thấy = 500 vì transaction khác đã commit thay đổi.

📈 **Tốt cho hiệu suất, an toàn hơn**, phổ biến trong các hệ quản trị như Oracle.

---

### 3. 🟡 **REPEATABLE READ** (Đọc lặp lại được)

- **Mỗi lần đọc cùng 1 bản ghi → kết quả giống nhau**, ngay cả khi transaction khác thay đổi và commit.
- Tránh **Dirty Read + Non-repeatable Read**.
- MySQL có thêm cơ chế ngăn **Phantom Read** bằng **Next-Key Locking** → khá mạnh.

🧠 Ví dụ: Bạn đọc danh sách đơn hàng 2 lần → không thay đổi dù người khác thêm đơn hàng.

📌 **Đây là mặc định của MySQL InnoDB.**

---

### 4. 🔵 **SERIALIZABLE** (Tuần tự hóa)

- Giao dịch chạy **theo thứ tự** như thể không có transaction đồng thời.
- **Khóa toàn bộ bảng**, không chỉ từng dòng.
- **An toàn tuyệt đối**, nhưng **rất chậm**.

🧠 Ví dụ: Mọi transaction thực hiện **lần lượt từng cái một** → như xếp hàng.

---

## ✅ Tóm tắt so sánh hiệu suất & độ an toàn

| Isolation Level  | An toàn       | Hiệu suất     |
| ---------------- | ------------- | ------------- |
| READ UNCOMMITTED | ❌ Tệ         | ✅ Tốt nhất   |
| READ COMMITTED   | 🟠 Trung bình | ✅ Tốt        |
| REPEATABLE READ  | ✅ Tốt        | 🟡 Trung bình |
| SERIALIZABLE     | ✅ Tốt nhất   | ❌ Tệ nhất    |

---

### ⚙️ Cách đặt isolation level trong MySQL:

```sql
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;
START TRANSACTION;
-- các câu lệnh SQL ở đây
COMMIT;
```

---

Tuyệt vời, nếu bạn muốn **"làm chủ MySQL với JSON"**, thì dưới đây là **tổng hợp đầy đủ, thực chiến và dễ hiểu nhất** về cách làm việc với **kiểu dữ liệu `JSON` trong MySQL** (từ MySQL 5.7 trở lên).

---

## 🧱 1. Khai báo cột JSON

```sql
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100),
  profile JSON
);
```

---

## 📥 2. Chèn dữ liệu JSON

```sql
INSERT INTO users (name, profile)
VALUES ('Alice', JSON_OBJECT('age', 25, 'city', 'Hanoi'));
```

Hoặc dùng chuỗi JSON trực tiếp:

```sql
INSERT INTO users (name, profile)
VALUES ('Bob', '{"age":30, "city":"HCM"}');
```

---

## 🔍 3. Truy vấn dữ liệu JSON

### ✅ Truy xuất giá trị trong JSON

```sql
SELECT
  name,
  profile->'$.age' AS age
FROM users;
```

- `->` trả về giá trị JSON
- `->>` trả về **giá trị dạng văn bản (text)**

```sql
SELECT
  name,
  profile->>'$.city' AS city_text
FROM users;
```

---

### 📌 Cú pháp đường dẫn JSON

| Cú pháp          | Ý nghĩa                |
| ---------------- | ---------------------- |
| `$`              | Gốc của JSON           |
| `$.age`          | Truy cập trường `age`  |
| `$.address.city` | Truy cập nested object |
| `$[0]`           | Truy cập mảng          |

---

## 🛠 4. Cập nhật giá trị trong JSON

```sql
UPDATE users
SET profile = JSON_SET(profile, '$.age', 28)
WHERE name = 'Alice';
```

| Hàm            | Ý nghĩa                       |
| -------------- | ----------------------------- |
| `JSON_SET`     | Thêm hoặc cập nhật giá trị    |
| `JSON_REPLACE` | Chỉ cập nhật nếu khóa tồn tại |
| `JSON_REMOVE`  | Xóa khóa khỏi JSON            |

---

## 🔍 5. Tìm kiếm trong JSON

### ✅ Tìm người có tuổi > 25:

```sql
SELECT * FROM users
WHERE JSON_EXTRACT(profile, '$.age') > 25;
```

### ✅ Hoặc dạng chuỗi:

```sql
SELECT * FROM users
WHERE CAST(profile->>'$.age' AS UNSIGNED) > 25;
```

---

## 🔎 6. Chỉ mục cho JSON

Bạn **không thể index trực tiếp cột JSON**, nhưng có thể dùng **generated columns**:

```sql
ALTER TABLE users
ADD age INT GENERATED ALWAYS AS (JSON_UNQUOTE(profile->'$.age')) STORED,
ADD INDEX idx_age(age);
```

➡️ Từ đó truy vấn nhanh hơn.

---

## 🧠 7. Một số hàm JSON hữu ích

| Hàm               | Mục đích                              |
| ----------------- | ------------------------------------- |
| `JSON_OBJECT()`   | Tạo object JSON                       |
| `JSON_ARRAY()`    | Tạo mảng JSON                         |
| `JSON_EXTRACT()`  | Truy xuất giá trị                     |
| `JSON_SET()`      | Thêm / cập nhật giá trị               |
| `JSON_REMOVE()`   | Xóa trường                            |
| `JSON_CONTAINS()` | Kiểm tra có chứa giá trị hay không    |
| `JSON_KEYS()`     | Trả về danh sách các key trong object |

---

## ✅ Tổng kết

JSON trong MySQL cực kỳ mạnh khi:

- Bạn cần lưu dữ liệu **có cấu trúc linh hoạt** (vd: cấu hình, metadata, quyền,...)
- Kết hợp với **generated columns** để tối ưu hiệu suất.

---

## Deadlock trong MySQL

Deadlock là tình trạng hai hoặc nhiều transaction bị khóa lẫn nhau, mỗi transaction đang chờ tài nguyên mà transaction khác đang giữ, dẫn đến tất cả đều không thể tiếp tục.

### 1. Cơ chế Deadlock

- **Định nghĩa**: Hai transaction không thể tiếp tục vì mỗi bên đang chờ tài nguyên mà bên kia đang giữ
- **Hậu quả**: MySQL sẽ tự động phát hiện và hủy một transaction để giải phóng deadlock
- **Thông báo lỗi**: `ERROR 1213 (40001): Deadlock found when trying to get lock; try restarting transaction`

### 2. Ví dụ minh họa Deadlock

#### Kịch bản: Hai transaction cập nhật hai hàng theo thứ tự khác nhau

**Chuẩn bị dữ liệu:**

```sql
CREATE TABLE accounts (
  id INT PRIMARY KEY,
  name VARCHAR(100),
  balance DECIMAL(10,2)
);

INSERT INTO accounts VALUES (1, 'Alice', 1000);
INSERT INTO accounts VALUES (2, 'Bob', 2000);
```

**Transaction 1:**

```sql
-- Session 1
START TRANSACTION;
-- Bước 1: Cập nhật tài khoản Alice
UPDATE accounts SET balance = balance - 100 WHERE id = 1;
-- Đợi một chút...
-- Bước 2: Cập nhật tài khoản Bob
UPDATE accounts SET balance = balance + 100 WHERE id = 2;
COMMIT;
```

**Transaction 2 (chạy đồng thời):**

```sql
-- Session 2
START TRANSACTION;
-- Bước 1: Cập nhật tài khoản Bob
UPDATE accounts SET balance = balance - 200 WHERE id = 2;
-- Đợi một chút...
-- Bước 2: Cập nhật tài khoản Alice
UPDATE accounts SET balance = balance + 200 WHERE id = 1;
COMMIT;
```

**Diễn biến deadlock:**

1. Transaction 1 khóa hàng id=1
2. Transaction 2 khóa hàng id=2
3. Transaction 1 cố gắng khóa hàng id=2 → phải chờ vì Transaction 2 đang giữ
4. Transaction 2 cố gắng khóa hàng id=1 → phải chờ vì Transaction 1 đang giữ
5. Deadlock xảy ra! MySQL phát hiện và hủy một trong hai transaction

### 3. Cách phòng tránh Deadlock

1. **Thống nhất thứ tự truy cập**

   ```sql
   -- Luôn cập nhật tài khoản có id nhỏ hơn trước
   START TRANSACTION;
   UPDATE accounts SET balance = balance - 100 WHERE id = 1;
   UPDATE accounts SET balance = balance + 100 WHERE id = 2;
   COMMIT;
   ```

2. **Sử dụng khóa với SELECT ... FOR UPDATE**

   ```sql
   START TRANSACTION;
   -- Khóa cả hai hàng ngay từ đầu
   SELECT * FROM accounts WHERE id IN (1, 2) FOR UPDATE;
   -- Thực hiện cập nhật an toàn
   UPDATE accounts SET balance = balance - 100 WHERE id = 1;
   UPDATE accounts SET balance = balance + 100 WHERE id = 2;
   COMMIT;
   ```

3. **Giảm kích thước transaction**

   - Giữ transaction ngắn gọn
   - Tránh chờ đợi input từ người dùng trong transaction

4. **Sử dụng timeout và retry**

   ```sql
   SET innodb_lock_wait_timeout = 50; -- Đặt timeout (giây)

   -- Trong ứng dụng, thêm logic retry
   START TRANSACTION;
   -- Nếu gặp deadlock, thử lại transaction
   COMMIT;
   ```

### 4. Kiểm tra và phân tích Deadlock

```sql
-- Xem thông tin deadlock gần nhất
SHOW ENGINE INNODB STATUS;

-- Tìm kiếm trong log
-- Phần "LATEST DETECTED DEADLOCK" chứa thông tin chi tiết
```

### 5. Deadlock trong các tình huống phức tạp

- **Khóa gap**: Xảy ra khi sử dụng isolation level REPEATABLE READ
- **Khóa ẩn**: Từ các ràng buộc như foreign key
- **Hot spots**: Nhiều transaction cạnh tranh cùng một hàng (ví dụ: bộ đếm)

## Debezium

# Đồng bộ dữ liệu MySQL to Kafka sử dụng Debezium

## 1. Tổng quan về Debezium

### 1.1 Debezium là gì?

Debezium là một nền tảng CDC (Change Data Capture) mã nguồn mở, theo dõi thay đổi trong cơ sở dữ liệu và phát các sự kiện thay đổi đến Kafka theo thời gian thực.

### 1.2 Luồng dữ liệu

```
[MySQL] --binlog--> [Debezium Connector] --> [Kafka Topic] --> [Consumer/Sink]
```

### 1.3 Ưu điểm

- **Realtime**: Phát hiện và truyền thay đổi gần như ngay lập tức
- **Không xâm lấn**: Không cần sửa đổi ứng dụng nguồn
- **Đáng tin cậy**: Đảm bảo không mất dữ liệu, xử lý lỗi tốt
- **Khả năng mở rộng**: Xử lý hàng triệu sự kiện mỗi giây

## 2. Chuẩn bị môi trường

### 2.1 Yêu cầu hệ thống

- MySQL 5.7+ với binlog định dạng ROW
- Apache Kafka và Zookeeper
- Kafka Connect framework
- Debezium MySQL Connector

### 2.2 Cấu hình MySQL

```sql
-- Đảm bảo binlog được bật và đúng định dạng
[mysqld]
server-id         = 1
log_bin           = mysql-bin
binlog_format     = ROW
binlog_row_image  = FULL
expire_logs_days  = 10
```

### 2.3 Tạo user MySQL cho Debezium

```sql
CREATE USER 'debezium'@'%' IDENTIFIED BY 'dbz_password';
GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'debezium'@'%';
FLUSH PRIVILEGES;
```

## 3. Triển khai Debezium với Docker Compose

### 3.1 Docker Compose file

```yaml
version: "3"
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1

  connect:
    image: debezium/connect:latest
    depends_on:
      - kafka
      - mysql
    ports:
      - "8083:8083"
    environment:
      GROUP_ID: 1
      CONFIG_STORAGE_TOPIC: connect_configs
      OFFSET_STORAGE_TOPIC: connect_offsets
      STATUS_STORAGE_TOPIC: connect_statuses
      BOOTSTRAP_SERVERS: kafka:29092
      KEY_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      VALUE_CONVERTER: org.apache.kafka.connect.json.JsonConverter
      KEY_CONVERTER_SCHEMAS_ENABLE: "true"
      VALUE_CONVERTER_SCHEMAS_ENABLE: "true"

  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_USER: debezium
      MYSQL_PASSWORD: dbz_password
      MYSQL_DATABASE: inventory
    command: --server-id=1 --log-bin=mysql-bin --binlog-format=ROW
```

## 4. Cấu hình Debezium Connector cho N Tables

### 4.1 Cấu hình cơ bản cho một database

```json
{
  "name": "inventory-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "dbz_password",
    "database.server.id": "1",
    "database.server.name": "mysql-server-1",
    "database.include.list": "inventory",
    "database.history.kafka.bootstrap.servers": "kafka:29092",
    "database.history.kafka.topic": "schema-changes.inventory",
    "include.schema.changes": "true",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter"
  }
}
```

### 4.2 Cấu hình cho nhiều tables cụ thể

```json
{
  "name": "multi-table-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "dbz_password",
    "database.server.id": "1",
    "database.server.name": "mysql-server-1",
    "database.include.list": "inventory,customers,orders",
    "table.include.list": "inventory.products,customers.users,orders.order_items",
    "database.history.kafka.bootstrap.servers": "kafka:29092",
    "database.history.kafka.topic": "schema-changes.multi-db",
    "include.schema.changes": "true",
    "transforms": "unwrap",
    "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
    "transforms.unwrap.drop.tombstones": "false"
  }
}
```

### 4.3 Triển khai connector

```bash
curl -X POST -H "Content-Type: application/json" --data @connector-config.json http://localhost:8083/connectors
```

## 5. Tối ưu hiệu suất cho hệ thống lớn

### 5.1 Phân vùng Connector

- Tạo nhiều connector cho các database/table khác nhau
- Phân chia tải trên nhiều Kafka Connect worker

### 5.2 Cấu hình Kafka

```properties
# Tăng số lượng partition cho topic
num.partitions=16

# Tăng kích thước batch
batch.size=131072

# Tăng bộ nhớ đệm
buffer.memory=67108864

# Tăng thời gian linger để gom batch
linger.ms=5
```

### 5.3 Cấu hình MySQL

```ini
# Tăng kích thước binlog cache
binlog_cache_size=4M

# Tăng thời gian lưu trữ binlog
expire_logs_days=7

# Tối ưu InnoDB
innodb_buffer_pool_size=4G
innodb_log_file_size=1G
```

## 6. Giám sát và xử lý lỗi

### 6.1 Giám sát Debezium

- Sử dụng JMX metrics từ Kafka Connect
- Theo dõi lag giữa binlog và Kafka
- Kiểm tra trạng thái connector:

```bash
curl -X GET http://localhost:8083/connectors/inventory-connector/status
```

### 6.2 Xử lý lỗi phổ biến

- **Schema change**: Cấu hình `include.schema.changes=true`
- **Binlog purged**: Đảm bảo binlog không bị xóa trước khi xử lý
- **Connection timeout**: Cấu hình `connect.timeout.ms` và `connection.attempts`

## 7. Mô hình triển khai cho hệ thống production

### 7.1 Kiến trúc High Availability

```
[MySQL Master] <--> [MySQL Replica]
      |                   |
[Debezium 1]         [Debezium 2] (standby)
      |                   |
[Kafka Cluster] <--> [Kafka Connect Cluster]
      |
[Consumers/Applications]
```

### 7.2 Chiến lược backup và khôi phục

- Lưu trữ offset Kafka
- Backup cấu hình connector
- Kế hoạch khôi phục từ điểm checkpoint

## 8. Ví dụ thực tế: Đồng bộ dữ liệu từ MySQL sang Elasticsearch

### 8.1 Cấu hình Kafka Connect Elasticsearch Sink

```json
{
  "name": "elasticsearch-sink",
  "config": {
    "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
    "topics": "mysql-server-1.inventory.products",
    "connection.url": "http://elasticsearch:9200",
    "type.name": "_doc",
    "key.ignore": "false",
    "schema.ignore": "true",
    "behavior.on.null.values": "delete",
    "transforms": "unwrap,key",
    "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
    "transforms.key.type": "org.apache.kafka.connect.transforms.ExtractField$Key",
    "transforms.key.field": "id"
  }
}
```

### 8.2 Kiểm tra luồng dữ liệu end-to-end

1. Thêm/sửa/xóa dữ liệu trong MySQL
2. Xác nhận sự kiện trong Kafka topic
   ```bash
   kafka-console-consumer --bootstrap-server localhost:9092 --topic mysql-server-1.inventory.products --from-beginning
   ```
3. Kiểm tra dữ liệu đã được cập nhật trong Elasticsearch
   ```bash
   curl -X GET "http://localhost:9200/mysql-server-1.inventory.products/_search?pretty"
   ```
