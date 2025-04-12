https://ant.ncc.asia/postgresql-kien-truc-va-chien-luoc-thuc-thi-sql/

### 3. Chiến lược thực thi SQL

#### 3.1. Query Processing Pipeline

1. **Parsing**

   - Kiểm tra cú pháp
   - Tạo parse tree
   - Validate các objects và permissions

2. **Transformation**

   - Chuyển parse tree thành query tree
   - Áp dụng các rule transformations
   - Resolve subqueries và views

3. **Planning/Optimization**

   - Generate các execution plans khả thi
   - Cost estimation cho mỗi plan
   - Chọn plan tối ưu nhất

4. **Execution**
   - Thực thi plan theo kiểu volcano/iterator model
   - Buffer management
   - Concurrency control

#### 3.2. Access Methods

- Sequential Scan
- Index Scan (B-tree, Hash, GiST, SP-GiST, GIN, BRIN)
- Bitmap Scan
- Index Only Scan

#### 3.3. Join Methods

1. **Nested Loop Join**

   - Phù hợp với small tables
   - Hiệu quả khi có index trên inner table

2. **Hash Join**

   - Tốt cho large tables
   - Requires memory để build hash table

3. **Merge Join**
   - Hiệu quả khi data đã được sort
   - Tốt cho large datasets

#### 3.4. Optimization Techniques

1. **Statistics-based optimization**

   - Sử dụng histogram và MCV lists
   - Ước tính selectivity

2. **Partition Pruning**

   - Loại bỏ các partitions không cần thiết
   - Cải thiện performance cho partitioned tables

3. **Parallel Query Execution**
   - Parallel Seq Scan
   - Parallel Index Scan
   - Parallel Joins

### Best Practices và Recommendations:

1. **Performance Tuning**

- Điều chỉnh shared_buffers phù hợp với RAM
- Optimize work_mem cho complex queries
- Sử dụng appropriate index types

2. **Monitoring**

- Track slow queries
- Monitor WAL generation
- Watch for bloat

3. **Maintenance**

- Regular VACUUM và ANALYZE
- Reindex khi cần thiết
- Monitor và manage disk space

4. **Security**

- Implement proper access controls
- Use SSL for encrypted connections
- Regular security audits

# 3. Chiến lược thực thi SQL trong PostgreSQL

## 3.1. Query Processing Pipeline - Chi tiết và ví dụ

### 1. Parsing Stage

PostgreSQL sẽ chuyển đổi SQL thành cây cú pháp (parse tree).

**Ví dụ:**

```sql
SELECT customers.name, orders.amount
FROM customers
JOIN orders ON customers.id = orders.customer_id
WHERE orders.amount > 1000;
```

**Parse Tree (Simplified):**

```
SelectStmt
├── targetList
│   ├── ResTarget(customers.name)
│   └── ResTarget(orders.amount)
├── fromClause
│   └── JoinExpr
│       ├── leftArg: RangeVar(customers)
│       ├── rightArg: RangeVar(orders)
│       └── joinType: JOIN_INNER
│           └── quals: A_Expr(customers.id = orders.customer_id)
└── whereClause
    └── A_Expr(orders.amount > 1000)
```

### 2. Transformation Stage

Chuyển đổi parse tree thành query tree, áp dụng các rule transformations.

**Ví dụ cho rule transformation:**
Nếu có view:

```sql
CREATE VIEW high_value_customers AS
SELECT c.* FROM customers c
JOIN orders o ON c.id = o.customer_id
WHERE o.amount > 1000;
```

Khi query:

```sql
SELECT * FROM high_value_customers;
```

PostgreSQL biến đổi thành:

```sql
SELECT c.* FROM customers c
JOIN orders o ON c.id = o.customer_id
WHERE o.amount > 1000;
```

### 3. Planning/Optimization

**Ví dụ query:**

```sql
SELECT c.name, SUM(o.amount)
FROM customers c
JOIN orders o ON c.id = o.customer_id
WHERE c.region = 'Europe'
GROUP BY c.name;
```

**Execution Plan khả thi:**

```
Plan A: (estimated cost: 850)
Hash Join
├── Hash Cond: (o.customer_id = c.id)
├── Seq Scan on orders o
└── Hash
    └── Seq Scan on customers c
        └── Filter: (region = 'Europe')

Plan B: (estimated cost: 650)
Hash Join
├── Hash Cond: (c.id = o.customer_id)
├── Seq Scan on customers c
│   └── Filter: (region = 'Europe')
└── Hash
    └── Seq Scan on orders o
```

PostgreSQL chọn Plan B vì chi phí ước tính thấp hơn.

### 4. Execution

Thực thi plan tối ưu theo volcano/iterator model, trong đó mỗi node trong plan tree triển khai một `next()` method để lấy tuple tiếp theo.

**Ví dụ thực tế với EXPLAIN ANALYZE:**

```sql
EXPLAIN ANALYZE
SELECT c.name, SUM(o.amount)
FROM customers c
JOIN orders o ON c.id = o.customer_id
WHERE c.region = 'Europe'
GROUP BY c.name;
```

```
HashAggregate  (cost=2630.59..2632.09 rows=150 width=40) (actual time=28.544..28.672 rows=120 loops=1)
  Group Key: c.name
  ->  Hash Join  (cost=104.50..2605.58 rows=5001 width=40) (actual time=1.234..24.567 rows=4850 loops=1)
        Hash Cond: (o.customer_id = c.id)
        ->  Seq Scan on orders o  (cost=0.00..1726.00 rows=100000 width=8) (actual time=0.021..9.142 rows=100000 loops=1)
        ->  Hash  (cost=78.50..78.50 rows=2080 width=36) (actual time=1.173..1.173 rows=2080 loops=1)
              Buckets: 4096  Batches: 1  Memory Usage: 158kB
              ->  Seq Scan on customers c  (cost=0.00..78.50 rows=2080 width=36) (actual time=0.011..0.834 rows=2080 loops=1)
                    Filter: (region = 'Europe'::text)
                    Rows Removed by Filter: 2920
Planning Time: 0.285 ms
Execution Time: 28.872 ms
```

## 3.2. Access Methods - Chi tiết

### 1. Sequential Scan

Đọc toàn bộ table từ đầu đến cuối.

**Khi nào được sử dụng:**

- Khi cần truy cập phần lớn bảng (>5-10%)
- Khi không có index phù hợp
- Khi bảng nhỏ

**Ví dụ:**

```sql
EXPLAIN ANALYZE SELECT * FROM products WHERE price < 1000;
```

```
Seq Scan on products  (cost=0.00..458.00 rows=9340 width=8) (actual time=0.008..4.567 rows=9268 loops=1)
  Filter: (price < 1000)
  Rows Removed by Filter: 732
```

### 2. Index Scan

**B-tree Index Scan:**

```sql
CREATE INDEX idx_customers_email ON customers(email);

EXPLAIN ANALYZE SELECT * FROM customers WHERE email = 'john@example.com';
```

```
Index Scan using idx_customers_email on customers  (cost=0.42..8.44 rows=1 width=36) (actual time=0.019..0.020 rows=1 loops=1)
  Index Cond: (email = 'john@example.com'::text)
```

**Hash Index:**

```sql
CREATE INDEX idx_customers_id_hash ON customers USING HASH (id);

EXPLAIN ANALYZE SELECT * FROM customers WHERE id = 12345;
```

```
Index Scan using idx_customers_id_hash on customers  (cost=0.00..8.27 rows=1 width=36) (actual time=0.009..0.010 rows=1 loops=1)
  Index Cond: (id = 12345)
```

**GIN Index (Full-text search):**

```sql
CREATE INDEX idx_docs_content ON documents USING GIN (to_tsvector('english', content));

EXPLAIN ANALYZE SELECT * FROM documents
WHERE to_tsvector('english', content) @@ to_tsquery('english', 'database & (postgresql | mysql)');
```

```
Bitmap Heap Scan on documents  (cost=12.01..28.02 rows=5 width=32) (actual time=0.156..0.158 rows=3 loops=1)
  Recheck Cond: (to_tsvector('english'::regconfig, content) @@ '''database'' & ( ''postgresql'' | ''mysql'' )'::tsquery)
  ->  Bitmap Index Scan on idx_docs_content  (cost=0.00..12.01 rows=5 width=0) (actual time=0.148..0.148 rows=3 loops=1)
        Index Cond: (to_tsvector('english'::regconfig, content) @@ '''database'' & ( ''postgresql'' | ''mysql'' )'::tsquery)
```

### 3. Bitmap Scan

Sử dụng khi truy vấn cần nhiều rows nhưng không đủ để sequential scan hiệu quả.

**Ví dụ:**

```sql
CREATE INDEX idx_products_price ON products(price);

EXPLAIN ANALYZE SELECT * FROM products WHERE price BETWEEN 50 AND 150;
```

```
Bitmap Heap Scan on products  (cost=11.80..220.15 rows=600 width=16) (actual time=0.137..1.234 rows=583 loops=1)
  Recheck Cond: ((price >= 50) AND (price <= 150))
  ->  Bitmap Index Scan on idx_products_price  (cost=0.00..11.65 rows=600 width=0) (actual time=0.121..0.121 rows=583 loops=1)
        Index Cond: ((price >= 50) AND (price <= 150))
```

### 4. Index Only Scan

Chỉ đọc dữ liệu từ index mà không cần truy cập table.

**Ví dụ:**

```sql
CREATE INDEX idx_customers_region_name ON customers(region, name);

EXPLAIN ANALYZE SELECT name FROM customers WHERE region = 'Europe';
```

```
Index Only Scan using idx_customers_region_name on customers  (cost=0.42..51.28 rows=2080 width=36) (actual time=0.028..0.756 rows=2080 loops=1)
  Index Cond: (region = 'Europe'::text)
  Heap Fetches: 0
```

## 3.3. Join Methods - Chi tiết

### 1. Nested Loop Join

**Nguyên lý:**

```
FOR each row in outer_table:
    FOR each row in inner_table:
        IF join_condition is TRUE:
            output combined row
```

**Ví dụ hiệu quả - Có index:**

```sql
CREATE INDEX idx_orders_customer_id ON orders(customer_id);

EXPLAIN ANALYZE
SELECT c.name, o.order_date
FROM customers c
JOIN orders o ON c.id = o.customer_id
WHERE c.id = 1001;
```

```
Nested Loop  (cost=0.42..16.47 rows=5 width=40) (actual time=0.016..0.096 rows=6 loops=1)
  ->  Index Scan using customers_pkey on customers c  (cost=0.42..8.44 rows=1 width=36) (actual time=0.010..0.011 rows=1 loops=1)
        Index Cond: (id = 1001)
  ->  Index Scan using idx_orders_customer_id on orders o  (cost=0.42..8.02 rows=5 width=12) (actual time=0.005..0.077 rows=6 loops=1)
        Index Cond: (customer_id = c.id)
```

**Trường hợp không hiệu quả - Table lớn không có index:**

```sql
EXPLAIN ANALYZE
SELECT c.name, o.order_date
FROM customers c
JOIN orders o ON c.id = o.customer_id;
```

```
Nested Loop  (cost=0.00..14030050.00 rows=100000 width=40) (actual time=0.026..25143.283 rows=100000 loops=1)
  ->  Seq Scan on customers c  (cost=0.00..145.00 rows=5000 width=36) (actual time=0.009..0.968 rows=5000 loops=1)
  ->  Seq Scan on orders o  (cost=0.00..2776.00 rows=20 width=12) (actual time=0.003..4.986 rows=20 loops=5000)
        Filter: (customer_id = c.id)
        Rows Removed by Filter: 99980
```

### 2. Hash Join

**Nguyên lý:**

```
# Build phase
hash_table = empty hash table
FOR each row in build_table:
    insert row into hash_table using join key as hash key

# Probe phase
FOR each row in probe_table:
    lookup rows in hash_table using join key
    FOR each matching row:
        output combined row
```

**Ví dụ hiệu quả cho bảng lớn:**

```sql
EXPLAIN ANALYZE
SELECT c.name, o.order_date
FROM customers c
JOIN orders o ON c.id = o.customer_id;
```

```
Hash Join  (cost=190.00..3356.00 rows=100000 width=40) (actual time=2.508..24.350 rows=100000 loops=1)
  Hash Cond: (o.customer_id = c.id)
  ->  Seq Scan on orders o  (cost=0.00..1726.00 rows=100000 width=12) (actual time=0.006..5.316 rows=100000 loops=1)
  ->  Hash  (cost=115.00..115.00 rows=5000 width=36) (actual time=2.482..2.482 rows=5000 loops=1)
        Buckets: 8192  Batches: 1  Memory Usage: 421kB
        ->  Seq Scan on customers c  (cost=0.00..115.00 rows=5000 width=36) (actual time=0.004..1.021 rows=5000 loops=1)
```

**So sánh với Nested Loop không hiệu quả:**
Nested Loop cần duyệt ~500M rows (5,000 x 100,000), while Hash Join chỉ cần ~105K rows (5,000 + 100,000).

### 3. Merge Join

**Nguyên lý:**

```
# Sort cả hai bảng theo join key
sorted_outer = sort(outer_table)
sorted_inner = sort(inner_table)

# Merge join
outer_pointer = first row of sorted_outer
inner_pointer = first row of sorted_inner

WHILE neither table exhausted:
    IF outer[outer_pointer].key = inner[inner_pointer].key:
        # output all matching rows
        output combined rows
        advance pointers
    ELSE IF outer[outer_pointer].key < inner[inner_pointer].key:
        advance outer_pointer
    ELSE:
        advance inner_pointer
```

**Ví dụ hiệu quả - Đã có index order các bảng:**

```sql
CREATE INDEX idx_customers_id ON customers(id);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);

EXPLAIN ANALYZE
SELECT c.name, o.order_date
FROM customers c
JOIN orders o ON c.id = o.customer_id
ORDER BY c.id;
```

```
Merge Join  (cost=0.85..5405.85 rows=100000 width=40) (actual time=0.026..30.534 rows=100000 loops=1)
  Merge Cond: (c.id = o.customer_id)
  ->  Index Scan using idx_customers_id on customers c  (cost=0.42..195.42 rows=5000 width=36) (actual time=0.010..2.348 rows=5000 loops=1)
  ->  Index Scan using idx_orders_customer_id on orders o  (cost=0.42..2630.42 rows=100000 width=12) (actual time=0.008..14.231 rows=100000 loops=1)
```

## 3.4. Optimization Techniques - Chi tiết

### 1. Statistics-based optimization

PostgreSQL sử dụng statistics để ước tính selectivity của các predicates.

**Ví dụ histogram:**

```sql
ANALYZE customers;
SELECT * FROM pg_stats
WHERE tablename = 'customers' AND attname = 'region';
```

```
histogram_bounds: {Africa,Asia,Europe,North America,Oceania,South America}
most_common_vals: {North America,Europe,Asia}
most_common_freqs: {0.4,0.3,0.2}
```

**Ảnh hưởng đến execution plan:**

```sql
-- Before ANALYZE
EXPLAIN SELECT * FROM customers WHERE region = 'Europe';
```

```
Seq Scan on customers  (cost=0.00..145.00 rows=1000 width=36)
  Filter: (region = 'Europe'::text)
```

```sql
-- After ANALYZE
ANALYZE customers;
EXPLAIN SELECT * FROM customers WHERE region = 'Europe';
```

```
Seq Scan on customers  (cost=0.00..145.00 rows=1500 width=36)
  Filter: (region = 'Europe'::text)
```

### 2. Partition Pruning

**Thiết lập partition:**

```sql
CREATE TABLE sales (
    sale_date DATE,
    amount NUMERIC,
    product_id INTEGER
) PARTITION BY RANGE (sale_date);

CREATE TABLE sales_2022 PARTITION OF sales
FOR VALUES FROM ('2022-01-01') TO ('2023-01-01');

CREATE TABLE sales_2023 PARTITION OF sales
FOR VALUES FROM ('2023-01-01') TO ('2024-01-01');
```

**Partition pruning example:**

```sql
EXPLAIN ANALYZE
SELECT * FROM sales WHERE sale_date BETWEEN '2023-05-01' AND '2023-06-30';
```

```
Append  (cost=0.00..123.52 rows=630 width=20) (actual time=0.016..0.876 rows=625 loops=1)
  ->  Seq Scan on sales_2023  (cost=0.00..123.52 rows=630 width=20) (actual time=0.016..0.858 rows=625 loops=1)
        Filter: ((sale_date >= '2023-05-01'::date) AND (sale_date <= '2023-06-30'::date))
```

**Chú ý**: PostgreSQL chỉ quét partition sales_2023, bỏ qua sales_2022.

### 3. Parallel Query Execution

**Thiết lập parallel workers:**

```sql
SET max_parallel_workers_per_gather = 4;
```

**Ví dụ parallel scan:**

```sql
EXPLAIN ANALYZE
SELECT * FROM large_table WHERE value > 1000;
```

```
Gather  (cost=1000.00..52151.52 rows=500000 width=16) (actual time=0.434..217.128 rows=498720 loops=1)
  Workers Planned: 4
  Workers Launched: 4
  ->  Parallel Seq Scan on large_table  (cost=0.00..51151.52 rows=125000 width=16) (actual time=0.085..197.282 rows=99744 loops=5)
        Filter: (value > 1000)
        Rows Removed by Filter: 100256
```

**Parallel aggregate:**

```sql
EXPLAIN ANALYZE
SELECT region, AVG(salary)
FROM employees
GROUP BY region;
```

```
Finalize GroupAggregate  (cost=3193.33..3193.85 rows=10 width=39) (actual time=23.245..23.254 rows=6 loops=1)
  Group Key: region
  ->  Gather Merge  (cost=3193.33..3193.68 rows=20 width=39) (actual time=23.219..23.237 rows=18 loops=1)
        Workers Planned: 4
        Workers Launched: 4
        ->  Sort  (cost=2193.33..2193.35 rows=5 width=39) (actual time=16.387..16.388 rows=5 loops=5)
              Sort Key: region
              Sort Method: quicksort  Memory: 25kB
              Worker 0:  Sort Method: quicksort  Memory: 25kB
              Worker 1:  Sort Method: quicksort  Memory: 25kB
              Worker 2:  Sort Method: quicksort  Memory: 25kB
              Worker 3:  Sort Method: quicksort  Memory: 25kB
              ->  Partial HashAggregate  (cost=2193.18..2193.28 rows=5 width=39) (actual time=16.358..16.362 rows=5 loops=5)
                    Group Key: region
                    ->  Parallel Seq Scan on employees  (cost=0.00..1876.67 rows=63333 width=9) (actual time=0.012..9.587 rows=50000 loops=5)
```

## 3.5. Case Studies - Tối ưu Query thực tế

### Case Study 1: Chuyển từ Nested Loop sang Hash Join

**Query ban đầu (chậm):**

```sql
EXPLAIN ANALYZE
SELECT c.name, o.order_id, p.product_name
FROM customers c
JOIN orders o ON c.id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE c.region = 'Europe';
```

**Execution plan không tối ưu (Nested Loops):**

```
Nested Loop  (cost=8.87..36275.38 rows=12500 width=76) (actual time=0.103..4325.721 rows=12684 loops=1)
  ->  Nested Loop  (cost=8.44..19707.67 rows=5000 width=44) (actual time=0.062..241.533 rows=5103 loops=1)
        ->  Seq Scan on customers c  (cost=0.00..145.00 rows=1500 width=36) (actual time=0.012..0.928 rows=1452 loops=1)
              Filter: (region = 'Europe'::text)
        ->  Index Scan using orders_customer_id_idx on orders o  (cost=8.44..13.01 rows=3 width=16) (actual time=0.042..0.163 rows=3 loops=1452)
              Index Cond: (customer_id = c.id)
  ->  Index Scan using order_items_order_id_idx on order_items oi  (cost=0.43..3.30 rows=2 width=16) (actual time=0.010..0.798 rows=2 loops=5103)
        Index Cond: (order_id = o.order_id)
```

**Tối ưu với Hash Joins:**

```sql
-- Tạo indexes
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- Set work_mem cao hơn để Hash Join hiệu quả
SET work_mem = '50MB';

-- Analyze tables
ANALYZE customers, orders, order_items, products;
```

**Query tối ưu:**

```sql
EXPLAIN ANALYZE
SELECT c.name, o.order_id, p.product_name
FROM customers c
JOIN orders o ON c.id = o.customer_id
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE c.region = 'Europe';
```

**Execution plan tối ưu (Hash Joins):**

```
Hash Join  (cost=648.00..1532.15 rows=12500 width=76) (actual time=6.214..32.456 rows=12684 loops=1)
  Hash Cond: (oi.product_id = p.id)
  ->  Hash Join  (cost=416.00..1047.15 rows=12500 width=52) (actual time=3.143..21.345 rows=12684 loops=1)
        Hash Cond: (oi.order_id = o.order_id)
        ->  Seq Scan on order_items oi  (cost=0.00..310.00 rows=20000 width=16) (actual time=0.007..2.153 rows=20000 loops=1)
        ->  Hash  (cost=362.50..362.50 rows=5000 width=44) (actual time=3.124..3.124 rows=5103 loops=1)
              ->  Hash Join  (cost=187.50..362.50 rows=5000 width=44) (actual time=1.246..2.621 rows=5103 loops=1)
                    Hash Cond: (o.customer_id = c.id)
                    ->  Seq Scan on orders o  (cost=0.00..155.00 rows=10000 width=16) (actual time=0.006..0.432 rows=10000 loops=1)
                    ->  Hash  (cost=145.00..145.00 rows=1500 width=36) (actual time=1.231..1.231 rows=1452 loops=1)
                          ->  Seq Scan on customers c  (cost=0.00..145.00 rows=1500 width=36) (actual time=0.009..0.809 rows=1452 loops=1)
                                Filter: (region = 'Europe'::text)
  ->  Hash  (cost=169.00..169.00 rows=5000 width=32) (actual time=3.051..3.051 rows=5000 loops=1)
        ->  Seq Scan on products p  (cost=0.00..169.00 rows=5000 width=32) (actual time=0.005..1.352 rows=5000 loops=1)
```

Kết quả: Thời gian thực thi giảm từ 4325ms xuống còn 32ms!

### Case Study 2: Sử dụng BRIN Index cho Time-Series Data

**Schema:**

```sql
CREATE TABLE sensor_data (
    id SERIAL PRIMARY KEY,
    sensor_id INTEGER,
    timestamp TIMESTAMPTZ,
    value NUMERIC
);

-- Tạo 10 triệu rows dữ liệu time-series
INSERT INTO sensor_data (sensor_id, timestamp, value)
SELECT
    (random()*100)::int,
    timestamp '2023-01-01' + (random()*365) * interval '1 day',
    random()*100
FROM generate_series(1, 10000000);
```

**Query chậm:**

```sql
EXPLAIN ANALYZE
SELECT * FROM sensor_data
WHERE timestamp BETWEEN '2023-06-01' AND '2023-06-30';
```

```
Seq Scan on sensor_data  (cost=0.00..245041.00 rows=833333 width=24) (actual time=0.015..1245.678 rows=821234 loops=1)
  Filter: ((timestamp >= '2023-06-01 00:00:00+00'::timestamptz) AND (timestamp <= '2023-06-30 00:00:00+00'::timestamptz))
```

**Tối ưu với BRIN Index:**

```sql
-- BRIN (Block Range Index) hiệu quả cho time-series data
CREATE INDEX idx_sensor_timestamp_brin ON sensor_data
USING BRIN (timestamp) WITH (pages_per_range = 128);

ANALYZE sensor_data;
```

**Query tối ưu:**

```sql
EXPLAIN ANALYZE
SELECT * FROM sensor_data
WHERE timestamp BETWEEN '2023-06-01' AND '2023-06-30';
```

```
Bitmap Heap Scan on sensor_data  (cost=412.84..29578.17 rows=833333 width=24) (actual time=4.562..215.873 rows=821234 loops=1)
  Recheck Cond: ((timestamp >= '2023-06-01 00:00:00+00'::timestamptz) AND (timestamp <= '2023-06-30 00:00:00+00'::timestamptz))
  Rows Removed by Index Recheck: 3456
  ->  Bitmap Index Scan on idx_sensor_timestamp_brin  (cost=0.00..204.00 rows=833333 width=0) (actual time=2.783..2.783 rows=824690 loops=1)
        Index Cond: ((timestamp >= '2023-06-01 00:00:00+00'::timestamptz) AND (timestamp <= '2023-06-30 00:00:00+00'::timestamptz))
```

Kết quả: Thời gian thực thi giảm từ 1245ms xuống còn 216ms!

## 3.6. Best Practices khi tối ưu query

1. **Sử dụng EXPLAIN ANALYZE** để hiểu execution plan và xác định bottleneck
2. **Tạo index thích hợp** cho các cột thường xuất hiện trong WHERE, JOIN, ORDER BY, GROUP BY
3. **Điều chỉnh các parameters**:
   - `work_mem`: Tăng cho complex sorts và hash operations
   - `maintenance_work_mem`: Tăng cho VACUUM, CREATE INDEX
   - `effective_cache_size`: Đặt ~70% RAM để planner biết bao nhiêu cache có sẵn
4. **Sử dụng JOIN thay vì subqueries** khi có thể
5. **Partition table** cho dữ liệu lớn, đặc biệt là time-series data
6. **Sử dụng partial và functional indexes** để giảm kích thước index
7. **VACUUM và ANALYZE thường xuyên** để cập nhật statistics và giảm bloat
8. **Viết SQL hiệu quả** - tránh LIKE '%string%', sử dụng EXISTS thay vì IN với subqueries

Các ví dụ và case studies trên giúp bạn hiểu cách PostgreSQL thực thi các câu query và làm thế nào để tối ưu chúng hiệu quả. Việc hiểu chiến lược thực thi SQL là cơ bản cho việc tối ưu performance trong các ứng dụng sử dụng PostgreSQL.
