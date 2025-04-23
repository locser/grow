# Thiết Kế Cơ Sở Dữ Liệu MySQL - Nền Tảng Trao Đổi Đồ Chơi

## Nguyên tắc thiết kế

- **Rõ ràng:** Tên bảng và cột dễ hiểu.
- **Chuẩn hóa:** Giảm thiểu dư thừa dữ liệu (hướng tới 3NF khi hợp lý).
- **Mở rộng:** Dễ dàng thêm các tính năng mới.
- **Hiệu năng:** Sử dụng kiểu dữ liệu phù hợp và đánh index cho các truy vấn thường xuyên.

## Sơ đồ quan hệ thực thể (ERD - Mô tả logic)

```mermaid
erDiagram
    USERS ||--o{ TOYS : owns
    USERS ||--o{ EXCHANGES : initiates_request
    USERS ||--o{ EXCHANGES : receives_request
    USERS ||--o{ NOTIFICATIONS : receives
    USERS ||--o{ DISPUTES : reports
    USERS ||--o{ REVIEWS : writes
    USERS ||--o{ REVIEWS : receives_review
    USERS ||--o{ EXCHANGE_MESSAGES : sends

    CAMPAIGNS ||--o{ TOYS : belongs_to
    CAMPAIGNS ||--o{ EXCHANGES : belongs_to

    TOYS ||--|{ USERS : owned_by
    TOYS ||--o{ TOY_PHOTOS : has
    TOYS ||--o{ EXCHANGES : is_target_item
    TOYS ||--o{ EXCHANGES : is_offered_item
    TOYS ||--|{ CAMPAIGNS : part_of (nullable)

    EXCHANGES ||--|{ USERS : requested_by
    EXCHANGES ||--|{ USERS : owned_by_user
    EXCHANGES ||--|{ TOYS : target_toy
    EXCHANGES ||--o{ TOYS : offered_toy (nullable)
    EXCHANGES ||--|{ CAMPAIGNS : part_of
    EXCHANGES ||--o{ EXCHANGE_MESSAGES : has_messages
    EXCHANGES ||--o{ DISPUTES : can_have
    EXCHANGES ||--o{ REVIEWS : can_be_reviewed

    NOTIFICATIONS ||--|{ USERS : target_user

    DISPUTES ||--|{ EXCHANGES : related_to
    DISPUTES ||--|{ USERS : reported_by

    REVIEWS ||--|{ EXCHANGES : based_on
    REVIEWS ||--|{ USERS : reviewer
    REVIEWS ||--|{ USERS : reviewed_user

    TOY_PHOTOS ||--|{ TOYS : belongs_to

    EXCHANGE_MESSAGES ||--|{ EXCHANGES : belongs_to
    EXCHANGE_MESSAGES ||--|{ USERS : sent_by
```

## Chi tiết các bảng

### 1. `users`

Lưu trữ thông tin người dùng.

```sql
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL, -- Sử dụng bcrypt hoặc Argon2
    name VARCHAR(100) NOT NULL,
    location VARCHAR(255) NULL, -- Có thể cần cấu trúc chi tiết hơn (city, country)
    is_admin BOOLEAN DEFAULT FALSE,
    average_rating DECIMAL(3, 2) DEFAULT NULL, -- Cập nhật bằng trigger hoặc logic ứng dụng
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE users ADD INDEX idx_users_email (email);
```

### 2. `campaigns`

Lưu trữ thông tin các chiến dịch trao đổi.

```sql
CREATE TABLE campaigns (
    campaign_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    theme VARCHAR(100) NULL,
    rules JSON NULL, -- Lưu trữ các quy tắc phức tạp (số lượng đồ chơi tối đa, loại, điều kiện...)
    status ENUM('UPCOMING', 'ACTIVE', 'PAST', 'DELETED') NOT NULL DEFAULT 'UPCOMING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

ALTER TABLE campaigns ADD INDEX idx_campaigns_status (status);
ALTER TABLE campaigns ADD INDEX idx_campaigns_dates (start_date, end_date);
```

### 3. `toys`

Lưu trữ thông tin đồ chơi được đăng.

```sql
CREATE TABLE toys (
    toy_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    campaign_id BIGINT NULL, -- NULL nếu đồ chơi không thuộc chiến dịch nào (T-007)
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    category VARCHAR(100) NOT NULL, -- Có thể tách thành bảng categories nếu cần quản lý chi tiết
    `condition` ENUM('NEW', 'LIKE_NEW', 'GOOD', 'FAIR', 'POOR') NOT NULL,
    status ENUM('AVAILABLE', 'PENDING_EXCHANGE', 'IN_EXCHANGE', 'EXCHANGED', 'REMOVED', 'AWAITING_APPROVAL') NOT NULL DEFAULT 'AVAILABLE',
    desired_exchange_items TEXT NULL, -- Mô tả món đồ mong muốn đổi
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, -- Xóa đồ chơi nếu người dùng bị xóa
    FOREIGN KEY (campaign_id) REFERENCES campaigns(campaign_id) ON DELETE SET NULL -- Giữ lại đồ chơi nếu chiến dịch bị xóa?
);

ALTER TABLE toys ADD INDEX idx_toys_user_id (user_id);
ALTER TABLE toys ADD INDEX idx_toys_campaign_id (campaign_id);
ALTER TABLE toys ADD INDEX idx_toys_status (status);
ALTER TABLE toys ADD INDEX idx_toys_category (category);
ALTER TABLE toys ADD INDEX idx_toys_condition (`condition`);
-- Cân nhắc FULLTEXT index cho name và description nếu cần tìm kiếm hiệu quả
-- ALTER TABLE toys ADD FULLTEXT INDEX idx_toys_search (name, description);
```

### 4. `toy_photos`

Lưu trữ ảnh của đồ chơi (mối quan hệ 1-nhiều với `toys`).

```sql
CREATE TABLE toy_photos (
    photo_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    toy_id BIGINT NOT NULL,
    photo_url VARCHAR(512) NOT NULL, -- URL tới ảnh (lưu trữ trên S3, Cloudinary,...)
    is_primary BOOLEAN DEFAULT FALSE,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (toy_id) REFERENCES toys(toy_id) ON DELETE CASCADE
);

ALTER TABLE toy_photos ADD INDEX idx_toy_photos_toy_id (toy_id);
```

### 5. `exchanges`

Lưu trữ thông tin các giao dịch trao đổi.

```sql
CREATE TABLE exchanges (
    exchange_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    campaign_id BIGINT NOT NULL, -- Mọi trao đổi phải thuộc về 1 chiến dịch
    requester_id BIGINT NOT NULL, -- Người gửi yêu cầu
    requester_toy_id BIGINT NULL, -- Đồ chơi người gửi đề nghị (có thể null nếu chỉ muốn nhận)
    owner_id BIGINT NOT NULL, -- Chủ sở hữu món đồ được yêu cầu
    owner_toy_id BIGINT NOT NULL, -- Đồ chơi được yêu cầu trao đổi
    status ENUM(
        'REQUESTED',      -- Mới yêu cầu
        'ACCEPTED',       -- Chủ sở hữu chấp nhận
        'REJECTED',       -- Chủ sở hữu từ chối
        'CONFIRMED',      -- Cả hai xác nhận (sau ACCEPTED)
        'SHIPPED_BY_REQUESTER', -- Người yêu cầu đã gửi hàng
        'SHIPPED_BY_OWNER',   -- Chủ sở hữu đã gửi hàng
        'COMPLETED',      -- Hoàn thành (cả hai xác nhận nhận hàng)
        'CANCELLED',      -- Bị hủy (trước khi hoàn thành)
        'DISPUTED'        -- Đang có tranh chấp
    ) NOT NULL DEFAULT 'REQUESTED',
    request_message TEXT NULL, -- Tin nhắn ban đầu của người yêu cầu
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (campaign_id) REFERENCES campaigns(campaign_id) ON DELETE CASCADE,
    FOREIGN KEY (requester_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (requester_toy_id) REFERENCES toys(toy_id) ON DELETE SET NULL, -- Giữ lại exchange nếu đồ chơi bị xóa?
    FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (owner_toy_id) REFERENCES toys(toy_id) ON DELETE CASCADE, -- Xóa exchange nếu đồ chơi chính bị xóa

    -- Đảm bảo người dùng không tự trao đổi với chính mình
    CHECK (requester_id <> owner_id),
    -- Đảm bảo đồ chơi đề nghị (nếu có) và đồ chơi mục tiêu khác nhau
    CHECK (requester_toy_id IS NULL OR requester_toy_id <> owner_toy_id)
);

ALTER TABLE exchanges ADD INDEX idx_exchanges_campaign_id (campaign_id);
ALTER TABLE exchanges ADD INDEX idx_exchanges_requester_id (requester_id);
ALTER TABLE exchanges ADD INDEX idx_exchanges_owner_id (owner_id);
ALTER TABLE exchanges ADD INDEX idx_exchanges_owner_toy_id (owner_toy_id);
ALTER TABLE exchanges ADD INDEX idx_exchanges_status (status);
```

### 6. `exchange_messages`

Lưu trữ tin nhắn trao đổi giữa hai người dùng trong một giao dịch.

```sql
CREATE TABLE exchange_messages (
    message_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exchange_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    message_text TEXT NOT NULL,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (exchange_id) REFERENCES exchanges(exchange_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE
);

ALTER TABLE exchange_messages ADD INDEX idx_exchange_messages_exchange_id (exchange_id);
```

### 7. `notifications`

Lưu trữ thông báo cho người dùng.

```sql
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL, -- Người nhận thông báo
    type ENUM(
        'NEW_EXCHANGE_REQUEST',
        'REQUEST_ACCEPTED',
        'REQUEST_REJECTED',
        'EXCHANGE_CONFIRMED',
        'NEW_MESSAGE',
        'CAMPAIGN_STARTING',
        'CAMPAIGN_ENDING',
        'EXCHANGE_SHIPPED',
        'EXCHANGE_COMPLETED',
        'DISPUTE_FILED',
        'DISPUTE_UPDATED',
        'REVIEW_RECEIVED'
        -- Thêm các loại khác nếu cần
    ) NOT NULL,
    related_entity_type ENUM('EXCHANGE', 'CAMPAIGN', 'USER', 'TOY', 'DISPUTE', 'REVIEW') NULL,
    related_entity_id BIGINT NULL, -- ID của thực thể liên quan (vd: exchange_id, campaign_id)
    message TEXT NULL, -- Nội dung thông báo (có thể tạo động hoặc lưu sẵn)
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

ALTER TABLE notifications ADD INDEX idx_notifications_user_id_read (user_id, is_read);
```

### 8. `disputes`

Lưu trữ thông tin các tranh chấp phát sinh từ giao dịch.

```sql
CREATE TABLE disputes (
    dispute_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exchange_id BIGINT NOT NULL UNIQUE, -- Mỗi exchange chỉ có tối đa 1 dispute?
    reporter_id BIGINT NOT NULL, -- Người báo cáo tranh chấp
    reason TEXT NOT NULL,
    status ENUM('OPEN', 'UNDER_REVIEW', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'OPEN',
    resolution_details TEXT NULL, -- Chi tiết giải quyết bởi admin
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (exchange_id) REFERENCES exchanges(exchange_id) ON DELETE CASCADE,
    FOREIGN KEY (reporter_id) REFERENCES users(user_id) ON DELETE CASCADE
);

ALTER TABLE disputes ADD INDEX idx_disputes_status (status);
ALTER TABLE disputes ADD INDEX idx_disputes_exchange_id (exchange_id);
```

### 9. `reviews` (Optional)

Lưu trữ đánh giá của người dùng sau khi hoàn thành trao đổi.

```sql
CREATE TABLE reviews (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    exchange_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL, -- Người viết đánh giá
    reviewed_user_id BIGINT NOT NULL, -- Người được đánh giá
    rating INT NOT NULL, -- Từ 1 đến 5
    comment TEXT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (exchange_id) REFERENCES exchanges(exchange_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_user_id) REFERENCES users(user_id) ON DELETE CASCADE,

    -- Đảm bảo đánh giá hợp lệ
    CHECK (rating >= 1 AND rating <= 5),
    CHECK (reviewer_id <> reviewed_user_id),
    -- Đảm bảo một cặp (exchange, reviewer) là duy nhất
    UNIQUE KEY uq_review_exchange_reviewer (exchange_id, reviewer_id)
);

ALTER TABLE reviews ADD INDEX idx_reviews_reviewed_user_id (reviewed_user_id);
```

## Lưu ý thêm

- **Kiểu dữ liệu ENUM:** Tiện lợi nhưng khó mở rộng. Cân nhắc sử dụng bảng tra cứu (lookup table) nếu các trạng thái/loại có thể thay đổi thường xuyên.
- **JSON trong `campaigns.rules`:** Linh hoạt nhưng khó truy vấn trực tiếp vào các quy tắc cụ thể bằng SQL chuẩn. Cần xử lý ở tầng ứng dụng hoặc sử dụng các hàm JSON của MySQL nếu cần.
- **Cascade Deletes:** Cẩn thận khi sử dụng `ON DELETE CASCADE`. Đảm bảo rằng việc xóa một bản ghi cha không vô tình xóa mất dữ liệu quan trọng ở bảng con mà bạn muốn giữ lại.
- **Indexes:** Các index được đề xuất là cơ bản. Cần phân tích các truy vấn thực tế để tối ưu hóa index cho phù hợp.
- **Bảo mật:** `password_hash` cần được tạo bằng thuật toán hashing mạnh như bcrypt hoặc Argon2.
- **Phân quyền:** Logic phân quyền (admin, user thường) cần được xử lý ở tầng ứng dụng dựa trên cột `users.is_admin` và ngữ cảnh (ví dụ: chỉ chủ sở hữu mới được sửa/xóa đồ chơi).
- **Wishlist (S-003):** Nếu cần tính năng này, có thể thêm bảng `wishlists` (user_id, toy_id) hoặc `wishlist_items` (user_id, description, category,...).
