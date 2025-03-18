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
