# Project Requirements - Toy Exchange Platform

This document outlines the functional and non-functional requirements for the platform.

## Functional Requirements (User Stories)

### User Management

- **U-001:** As a new user, I want to register for an account using my email and a password so that I can list toys and make exchange requests.
- **U-002:** As a registered user, I want to log in to my account so that I can access the platform's features.
- **U-003:** As a logged-in user, I want to view and edit my profile information (e.g., name, location).
- **U-004:** As a logged-in user, I want to view my listed toys (both inside and outside campaigns).
- **U-005:** As a logged-in user, I want to view my exchange history (initiated, received, completed).

### Campaign Management (Mới)

- **C-001:** As an administrator, I want to create a new exchange campaign with specific settings (e.g., name, description, start date, end date, theme, specific rules/conditions).
- **C-002:** As an administrator, I want to view, edit, and delete existing campaigns.
- **C-003:** As any user, I want to view active and past campaigns.
- **C-004:** As any user, I want to see the specific settings and rules for each campaign.

### Advanced Campaign Management (Mới)

- **C-A01:** As an administrator, I want the ability to define more complex campaign rules (e.g., limit the number of toys per user, require specific toy categories, set minimum/maximum item conditions, potentially define value tiers or point systems).
- **C-A02:** The system should automatically enforce defined campaign rules during toy listing and exchange initiation.
- **C-A03:** (Optional) Consider mechanisms for campaign templates or recurring campaigns.

### Toy Management (Cập nhật)

- **T-001:** As a logged-in user, I want to list a toy for exchange **within a specific active campaign**, including uploading photos, providing a description, selecting a category, indicating its condition, and specifying my desired exchange items/preferences **relevant to the campaign rules**.
- **T-002:** As any user, I want to browse and view available toy listings **within a specific campaign**.
- **T-003:** As any user, I want to search for toys **within a campaign** based on keywords.
- **T-004:** As any user, I want to filter toy listings **within a campaign** by category, condition, and potentially location or user preferences.
- **T-005:** As the owner of a listing, I want to edit the details of my toy listing **within the campaign context**.
- **T-006:** As the owner of a listing, I want to remove my toy listing **from a campaign**.
- **T-007:** (Optional) As a logged-in user, I might be able to list toys outside of any specific campaign (general listing).

### Exchange Process (Cập nhật)

#### State Management

- **E-S01:** Define clear states for a toy listing (e.g., `AVAILABLE`, `PENDING_EXCHANGE`, `IN_EXCHANGE`, `EXCHANGED`, `REMOVED`, `AWAITING_APPROVAL` if applicable).
- **E-S02:** Define clear states for an exchange transaction (e.g., `REQUESTED`, `ACCEPTED`, `REJECTED`, `CONFIRMED`, `SHIPPED`, `COMPLETED`, `CANCELLED`, `DISPUTED`).
- **E-S03:** Detail how state transitions occur based on user actions and system events.

- **E-001:** As a logged-in user, I want to initiate an exchange request for a toy listed by another user **within the same campaign**, potentially offering one of my listed toys (in the same campaign) in return.
- **E-002:** As a toy owner, I want to receive notifications (in-app or email) when someone requests an exchange for my toy **within a campaign**.
- **E-003:** As a toy owner, I want to view pending exchange requests for my toys **within a campaign**.
- **E-004:** As a toy owner, I want to accept or reject an exchange request.
- **E-005:** As users involved in a potential exchange, I want a way to communicate (e.g., simple messaging) to discuss details.
- **E-006:** As users involved in an exchange, **once both parties agree**, I want the system to confirm the agreement and potentially perform pre-shipment calculations or checks (e.g., display estimated shipping info if addresses are provided, confirm both items meet campaign criteria if applicable).
- **E-007:** As users involved in an agreed exchange, I want to mark the exchange as "shipped" or "completed" once the physical swap occurs (triggering state changes).

### Notification System (Mới)

- **N-001:** As a user, I want to receive timely notifications (in-app and/or email) for key events related to my activities.
- **N-002:** Define specific notification triggers: new exchange request received, request accepted/rejected, message received in exchange chat, campaign starting/ending soon, exchange marked as shipped/completed, dispute filed/updated.
- **N-003:** As a user, I want basic controls over which notifications I receive (optional).

### Search Optimization & Recommendations (Mới)

- **S-001:** Implement advanced search filters beyond basic keywords (e.g., user rating, distance if location is available).
- **S-002:** (Optional) Develop a recommendation engine to suggest relevant toys based on user's browsing history, wishlist, or past exchanges.
- **S-003:** (Optional) Allow users to create and manage a "wishlist" of desired toys.

### Dispute Resolution (Mới)

- **D-001:** As a user involved in an exchange, I want to report an issue (e.g., item not as described, item not received) if a dispute arises.
- **D-002:** As an administrator, I want to view and manage reported disputes.
- **D-003:** As an administrator, I want tools to help mediate or resolve disputes (e.g., view communication history, update exchange status based on resolution).

### (Optional) Reviews & Ratings

- **R-001:** As a user who completed an exchange, I want to leave a rating and a short review for the other user involved.
- **R-002:** As a user, I want to view the average rating and reviews for other users on their profile.

## Non-Functional Requirements

- **Performance:**
  - Page load times for viewing listings should be under 3 seconds on a standard broadband connection.
  - The system should support at least 50 concurrent users without significant performance degradation.
- **Security:**
  - User passwords must be securely hashed (e.g., using bcrypt).
  - All user input must be validated and sanitized to prevent XSS attacks.
  - Implement protection against CSRF attacks.
  - Sensitive data transmission should use HTTPS.
- **Usability:**
  - The user interface should be intuitive and easy to navigate.
  - The application should be responsive and display correctly on common desktop and mobile browser sizes.
- **Reliability:**
  - The application should aim for 99.5% uptime.
  - Regular database backups should be performed.
- **Maintainability:**
  - Code should follow standard Java and Spring Boot conventions.
  - Key business logic should have unit tests.

### Thiết kế API Cơ bản

Dưới đây là một số gợi ý về các endpoint API RESTful cho các tính năng mới. Lưu ý rằng đây chỉ là thiết kế ban đầu và cần được chi tiết hóa thêm (ví dụ: cấu trúc request/response body, xử lý lỗi, phân quyền).

Campaign API Endpoints

- POST /api/v1/admin/campaigns
  - Mô tả: Tạo một chiến dịch mới (yêu cầu quyền admin).
  - Request Body: Dữ liệu chiến dịch (tên, mô tả, ngày bắt đầu/kết thúc, quy tắc...).
  - Response: Dữ liệu chiến dịch vừa tạo.
- GET /api/v1/campaigns
  - Mô tả: Lấy danh sách các chiến dịch (có thể lọc theo trạng thái: active, past, upcoming).
  - Response: Danh sách các chiến dịch.
- GET /api/v1/campaigns/{campaignId}
  - Mô tả: Lấy thông tin chi tiết của một chiến dịch cụ thể.
  - Response: Dữ liệu chi tiết của chiến dịch.
- PUT /api/v1/admin/campaigns/{campaignId}
  - Mô tả: Cập nhật thông tin chiến dịch (yêu cầu quyền admin).
  - Request Body: Dữ liệu cập nhật.
  - Response: Dữ liệu chiến dịch đã cập nhật.
- DELETE /api/v1/admin/campaigns/{campaignId}

  - Mô tả: Xóa một chiến dịch (yêu cầu quyền admin, có thể chỉ cho phép xóa nếu chưa có hoạt động).
  - Response: 204 No Content.
    Toy Listing API Endpoints (Cập nhật/Mới)

- POST /api/v1/campaigns/{campaignId}/toys
  - Mô tả: Đăng một đồ chơi mới vào một chiến dịch cụ thể (yêu cầu đăng nhập).
  - Request Body: Dữ liệu đồ chơi (mô tả, ảnh, điều kiện, tùy chọn trao đổi...).
  - Response: Dữ liệu đồ chơi vừa tạo.
- GET /api/v1/campaigns/{campaignId}/toys
  - Mô tả: Lấy danh sách đồ chơi trong một chiến dịch (có thể có phân trang, lọc, tìm kiếm).
  - Query Params: page , size , category , condition , search .
  - Response: Danh sách đồ chơi trong chiến dịch.
- GET /api/v1/toys/{toyId}
  - Mô tả: Lấy thông tin chi tiết một đồ chơi (cần biết nó thuộc chiến dịch nào hoặc là đồ chơi chung).
  - Response: Dữ liệu chi tiết đồ chơi.
- PUT /api/v1/toys/{toyId}
  - Mô tả: Cập nhật thông tin đồ chơi (chỉ chủ sở hữu mới được làm).
  - Request Body: Dữ liệu cập nhật.
  - Response: Dữ liệu đồ chơi đã cập nhật.
- DELETE /api/v1/toys/{toyId}

  - Mô tả: Xóa một đồ chơi khỏi danh sách (chỉ chủ sở hữu).
  - Response: 204 No Content.
    Exchange API Endpoints (Cập nhật/Mới)

- POST /api/v1/campaigns/{campaignId}/exchanges
  - Mô tả: Tạo một yêu cầu trao đổi cho một món đồ chơi trong chiến dịch (yêu cầu đăng nhập).
  - Request Body: targetToyId , offeredToyId (tùy chọn, nếu người yêu cầu đề nghị món đồ của họ).
  - Response: Dữ liệu yêu cầu trao đổi vừa tạo.
- GET /api/v1/exchanges
  - Mô tả: Lấy danh sách các yêu cầu trao đổi liên quan đến người dùng hiện tại (đã gửi, đã nhận).
  - Query Params: status (pending, accepted, rejected, completed), type (sent, received).
  - Response: Danh sách các yêu cầu trao đổi.
- GET /api/v1/exchanges/{exchangeId}
  - Mô tả: Lấy chi tiết một yêu cầu trao đổi.
  - Response: Dữ liệu chi tiết yêu cầu trao đổi.
- PUT /api/v1/exchanges/{exchangeId}/respond
  - Mô tả: Phản hồi một yêu cầu trao đổi (chấp nhận/từ chối - chỉ người nhận yêu cầu).
  - Request Body: action ("accept" hoặc "reject").
  - Response: Dữ liệu yêu cầu trao đổi đã cập nhật.
- PUT /api/v1/exchanges/{exchangeId}/confirm
  - Mô tả: Xác nhận đồng ý trao đổi sau khi cả hai bên đã chấp nhận (có thể kích hoạt "tính toán trước vận chuyển").
  - Request Body: (Có thể trống hoặc chứa thông tin xác nhận).
  - Response: Dữ liệu trao đổi với trạng thái đã xác nhận và thông tin tính toán (nếu có).
- PUT /api/v1/exchanges/{exchangeId}/complete
  - Mô tả: Đánh dấu trao đổi đã hoàn thành (một hoặc cả hai bên).
  - Request Body: (Có thể trống).
  - Response: Dữ liệu trao đổi với trạng thái hoàn thàn
