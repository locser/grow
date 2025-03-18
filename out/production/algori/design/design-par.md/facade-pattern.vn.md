### 6. Khi nào nên sử dụng Mẫu Facade

1. **Khi bạn cần cung cấp một giao diện đơn giản cho một hệ thống con phức tạp**: Nếu hệ thống của bạn có nhiều lớp và phương thức phức tạp, việc sử dụng mẫu Facade sẽ giúp người dùng dễ dàng tương tác mà không cần phải hiểu rõ về từng thành phần bên trong.

2. **Khi bạn muốn phân lớp các hệ thống con**: Mẫu Facade cho phép bạn tổ chức các thành phần trong hệ thống thành các lớp khác nhau, giúp quản lý và bảo trì dễ dàng hơn.

3. **Khi có nhiều phụ thuộc giữa Client và các lớp triển khai**: Nếu Client cần tương tác với nhiều lớp khác nhau, mẫu Facade giúp giảm bớt sự phụ thuộc này, từ đó giảm độ phức tạp trong mã nguồn.

4. **Khi bạn muốn cấu trúc một hệ thống con thành các lớp**: Mẫu Facade có thể giúp bạn tổ chức các thành phần trong hệ thống theo một cấu trúc rõ ràng, dễ hiểu, từ đó tạo điều kiện thuận lợi cho việc phát triển và bảo trì.

### 7. Ưu điểm của Mẫu Facade

1. **Tách biệt Client khỏi các thành phần của hệ thống con**: Mẫu Facade giúp người dùng không cần phải tương tác trực tiếp với các lớp phức tạp, từ đó giảm bớt sự phụ thuộc vào các thành phần bên trong.

2. **Thúc đẩy sự kết nối yếu**: Bằng cách sử dụng lớp Facade, các lớp bên trong có thể thay đổi mà không làm ảnh hưởng đến mã của Client, tạo điều kiện cho sự linh hoạt và bảo trì dễ dàng hơn.

3. **Đơn giản hóa các hệ thống phức tạp**: Mẫu này giúp cung cấp một giao diện đơn giản để làm việc với những hệ thống phức tạp, giúp người dùng dễ dàng hơn trong việc thực hiện các tác vụ.

4. **Cung cấp giao diện thống nhất cho một tập hợp các giao diện**: Thay vì phải làm việc với nhiều giao diện khác nhau, người dùng chỉ cần làm việc với một giao diện duy nhất, giúp giảm thiểu sự nhầm lẫn.

### 8. Nhược điểm của Mẫu Facade

1. **Facade có thể trở thành một đối tượng "thần thánh"**: Nếu không được thiết kế cẩn thận, lớp Facade có thể trở thành một đối tượng kết nối với tất cả các lớp khác, dẫn đến việc khó bảo trì và mở rộng.

2. **Dễ bị phá vỡ các quy tắc trong SOLID**

3. **Có thể ẩn đi sự phức tạp hữu ích mà một số Client có thể cần**: Mặc dù mẫu Facade giúp đơn giản hóa, nhưng đôi khi, một số Client cần truy cập vào các chức năng phức tạp mà Facade đã ẩn đi, dẫn đến việc không đáp ứng được nhu cầu của họ.
