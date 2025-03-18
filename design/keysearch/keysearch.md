1. [] Thiết kế cơ sở dữ liệu kiểm soát quyền RBAC với 4 bảng Mysql và 3 collections Mongodb
2. security filter chains
3. @EnableMethodSecurity(jsr250Enabled = true) link:[https://techmaster.vn/posts/37238/gioi-thieu-spring-method-security]
4. Distributed cache with Redis
5. config info client redis
6. resilience4j
   ### Giới thiệu về Circuit Breaker trong Resilience4j

**Circuit Breaker** (CB) là một mẫu thiết kế giúp cải thiện độ ổn định và độ tin cậy của ứng dụng. Nó được triển khai thông qua một máy trạng thái hữu hạn với ba trạng thái chính: **CLOSED**, **OPEN** và **HALF_OPEN**, cùng với ba trạng thái đặc biệt: **METRICS_ONLY**, **DISABLED** và **FORCED_OPEN**.

#### 1. **Các trạng thái chính**

- **CLOSED**:

  - Trong trạng thái này, Circuit Breaker cho phép tất cả các yêu cầu đi qua. Nó theo dõi các yêu cầu và phản hồi để xác định xem có cần mở mạch hay không. Nếu số lượng lỗi vượt qua một ngưỡng nhất định trong một khoảng thời gian, Circuit Breaker sẽ chuyển sang trạng thái OPEN.

- **OPEN**:

  - Khi Circuit Breaker ở trạng thái OPEN, nó sẽ từ chối tất cả các yêu cầu và không cho chúng đi qua. Điều này giúp ngăn ngừa việc ứng dụng liên tục cố gắng thực hiện các yêu cầu đến một dịch vụ không hoạt động hoặc có vấn đề, từ đó giảm thiểu áp lực lên dịch vụ đó.

- **HALF_OPEN**:
  - Sau một khoảng thời gian nhất định, Circuit Breaker sẽ vào trạng thái HALF_OPEN để kiểm tra xem dịch vụ đã hồi phục hay chưa. Trong trạng thái này, nó cho phép một số yêu cầu đi qua. Nếu các yêu cầu này thành công, Circuit Breaker sẽ chuyển lại sang trạng thái CLOSED. Nếu không, nó sẽ quay về trạng thái OPEN.

#### 2. **Các trạng thái đặc biệt**

- **METRICS_ONLY**:

  - Trong trạng thái này, Circuit Breaker không ngăn chặn bất kỳ yêu cầu nào, nhưng vẫn thu thập và ghi nhận các số liệu thống kê về thành công và lỗi. Trạng thái này hữu ích cho việc giám sát và phân tích mà không cần áp đặt bất kỳ hạn chế nào.

- **DISABLED**:

  - Khi Circuit Breaker ở trạng thái DISABLED, nó sẽ không thực hiện bất kỳ kiểm tra nào và cho phép tất cả các yêu cầu đi qua. Đây là trạng thái tạm thời có thể được sử dụng trong quá trình phát triển hoặc thử nghiệm.

- **FORCED_OPEN**:
  - Trong trạng thái này, Circuit Breaker sẽ từ chối tất cả các yêu cầu, giống như trạng thái OPEN, nhưng điều này có thể được thực hiện theo cách thủ công. Trạng thái này thường được sử dụng để xử lý các tình huống khẩn cấp.

### Tóm tắt

Circuit Breaker là một công cụ mạnh mẽ giúp cải thiện độ tin cậy của ứng dụng bằng cách quản lý và kiểm soát các yêu cầu đến các dịch vụ bên ngoài. Bằng cách sử dụng các trạng thái khác nhau, nó giúp ngăn ngừa tình trạng quá tải và tăng khả năng phục hồi của hệ thống trong trường hợp dịch vụ gặp sự cố.

7. Vì sao không sử dụng LUA Redis? Lua Redis là gì?
8. REDISon
9. SOLID: [https://anhtuank7c.dev/blog/open-close-principle]
10. guava cache: google cache
11. caffeine
12. wrk - http benchmarking tool
