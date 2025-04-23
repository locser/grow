# Database Indexing - Hướng Dẫn Chi Tiết

## 1. Khái Niệm Cơ Bản

### 1.1 Index là gì?

- Index trong database giống như mục lục của một cuốn sách
- Giúp tìm kiếm thông tin nhanh chóng mà không cần quét toàn bộ dữ liệu
- Là cặp key-value: key dùng để tìm kiếm, value là con trỏ đến row tương ứng

### 1.2 Tại sao cần Index?

- Cải thiện hiệu năng tìm kiếm dữ liệu
- Giảm thời gian truy xuất
- Tối ưu hóa hiệu suất tổng thể của hệ thống

## 2. Các Loại Index Phổ Biến

### 2.1 B-tree Index

- Loại index phổ biến nhất
- Hiệu quả cho dữ liệu đã sắp xếp
- Phù hợp với các phép so sánh:
  - Equality (=)
  - Range comparisons (>, <, >=, <=)
  - BETWEEN
  - LIKE

### 2.2 Hash Index

- Sử dụng hàm băm (hash function)
- Ưu điểm:
  - Cực kỳ nhanh cho tìm kiếm chính xác (=)
- Nhược điểm:
  - Không hiệu quả cho truy vấn phạm vi
  - Không hỗ trợ sắp xếp

### 2.3 Full-text Index

- Chuyên dụng cho trường văn bản lớn
- Hỗ trợ tìm kiếm từ khóa trong văn bản
- Tối ưu cho tìm kiếm ngôn ngữ tự nhiên

## 3. Composite Index (Index Kết Hợp)

### 3.1 Đặc điểm

- Tạo trên nhiều cột
- Kết hợp giá trị từ các cột theo thứ tự xác định
- Tạo khóa duy nhất từ nhiều cột

### 3.2 Khi nào sử dụng

- Các truy vấn thường xuyên sử dụng nhiều cột
- Cần tối ưu hiệu suất cho các join phức tạp
- Muốn giảm số lượng index riêng lẻ

## 4. Nguyên Tắc Tạo Index

### 4.1 Các Tiêu Chí Chọn Cột

- Cột thường xuất hiện trong mệnh đề WHERE
- Cột dùng trong JOIN
- Cột xuất hiện trong ORDER BY hoặc GROUP BY
- Cột có tính chọn lọc cao

### 4.2 Lưu Ý Quan Trọng

- Không tạo quá nhiều index
- Cân nhắc giữa hiệu suất đọc và ghi
- Theo dõi và bảo trì index thường xuyên
- Xóa các index không sử dụng
