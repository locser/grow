**_<h1>Tôi sẽ giải thích sự khác biệt giữa Factory Pattern và Strategy Pattern với ví dụ cụ thể.</h1>_**

````markdown:/Users/loCser/Desktop/design-par/design-par.md/factory-vs-strategy.md
# So Sánh Factory Pattern và Strategy Pattern

## 1. Mục Đích Sử Dụng

### Factory Pattern
- Tập trung vào việc **TẠO** đối tượng
- Giấu đi logic khởi tạo đối tượng
- Tạo ra các đối tượng cùng họ hàng (cùng interface/abstract class)

### Strategy Pattern
- Tập trung vào việc **SỬ DỤNG** các thuật toán khác nhau
- Cho phép thay đổi hành vi của đối tượng trong thời gian chạy
- Định nghĩa một họ các thuật toán có thể hoán đổi cho nhau

## 2. Ví Dụ Minh Họa

### Ví dụ Factory Pattern - Xử lý thanh toán
```java:/Users/loCser/Desktop/design-par/design-par.md/payment/PaymentFactory.java
// Interface chung
interface PhuongThucThanhToan {
    void thanhToan(double soTien);
}

// Các class con cụ thể
class ThanhToanMomo implements PhuongThucThanhToan {
    public void thanhToan(double soTien) {
        System.out.println("Thanh toán " + soTien + "đ qua Momo");
    }
}

class ThanhToanVNPay implements PhuongThucThanhToan {
    public void thanhToan(double soTien) {
        System.out.println("Thanh toán " + soTien + "đ qua VNPay");
    }
}

// Factory để tạo đối tượng
class NhaMayDungThanhToan {
    public static PhuongThucThanhToan taoThanhToan(String loai) {
        switch (loai) {
            case "momo":
                return new ThanhToanMomo();
            case "vnpay":
                return new ThanhToanVNPay();
            default:
                throw new IllegalArgumentException("Không hỗ trợ phương thức này");
        }
    }
}

// Sử dụng
public class Main {
    public static void main(String[] args) {
        // Factory tạo đối tượng theo yêu cầu
        PhuongThucThanhToan thanhToan = NhaMaxyDungThanhToan.taoThanhToan("momo");
        thanhToan.thanhToan(100000);
    }
}
````

### Ví dụ Strategy Pattern - Tính giá vận chuyển

```java:/Users/loCser/Desktop/design-par/design-par.md/shipping/ShippingStrategy.java
// Interface chiến lược
interface ChienLuocVanChuyen {
    double tinhPhiVanChuyen(double khoiLuong);
}

// Các chiến lược cụ thể
class VanChuyenNhanh implements ChienLuocVanChuyen {
    public double tinhPhiVanChuyen(double khoiLuong) {
        return khoiLuong * 30000;
    }
}

class VanChuyenTietKiem implements ChienLuocVanChuyen {
    public double tinhPhiVanChuyen(double khoiLuong) {
        return khoiLuong * 15000;
    }
}

// Context sử dụng chiến lược
class DonHang {
    private ChienLuocVanChuyen chienLuocVanChuyen;
    private double khoiLuong;

    // Có thể thay đổi chiến lược trong runtime
    public void setChienLuocVanChuyen(ChienLuocVanChuyen chienLuoc) {
        this.chienLuocVanChuyen = chienLuoc;
    }

    public double tinhPhi() {
        return chienLuocVanChuyen.tinhPhiVanChuyen(khoiLuong);
    }
}

// Sử dụng
public class Main {
    public static void main(String[] args) {
        DonHang donHang = new DonHang();

        // Có thể thay đổi chiến lược tùy theo yêu cầu
        donHang.setChienLuocVanChuyen(new VanChuyenNhanh());
        System.out.println("Phí vận chuyển nhanh: " + donHang.tinhPhi());

        donHang.setChienLuocVanChuyen(new VanChuyenTietKiem());
        System.out.println("Phí vận chuyển tiết kiệm: " + donHang.tinhPhi());
    }
}
```

## 3. Những Điểm Khác Biệt Chính

1. **Thời Điểm Hoạt Động**

   - Factory: Hoạt động tại thời điểm khởi tạo đối tượng
   - Strategy: Hoạt động trong suốt vòng đời của đối tượng

2. **Mục Đích**

   - Factory: Đóng gói logic tạo đối tượng
   - Strategy: Đóng gói các thuật toán có thể thay thế cho nhau

3. **Tính Linh Hoạt**

   - Factory: Khó thay đổi cách tạo đối tượng khi đã khởi tạo
   - Strategy: Dễ dàng thay đổi thuật toán trong thời gian chạy

4. **Use Cases**
   - Factory: Tạo đối tượng phức tạp, nhiều điều kiện
   - Strategy: Cần nhiều cách xử lý khác nhau cho cùng một tác vụ

## 4. Khi Nào Sử Dụng

### Factory Pattern

- Khi logic tạo đối tượng phức tạp
- Khi muốn tập trung việc tạo đối tượng vào một nơi
- Khi cần che giấu chi tiết khởi tạo

### Strategy Pattern

- Khi có nhiều thuật toán tương tự nhau
- Khi cần thay đổi thuật toán trong runtime
- Khi muốn tránh điều kiện if-else phức tạp

các ví dụ thực tế trong cuộc sống:

```markdown:/Users/loCser/Desktop/design-par/design-par.md/real-life-examples.md
# Ví Dụ Thực Tế Về Factory và Strategy Pattern

## 1. Nhà Máy Sản Xuất Bánh Mì (Factory Pattern)
- **Tình huống**: Một tiệm bánh mì sản xuất nhiều loại bánh khác nhau
- **Cách hoạt động**:
  - Khách đặt loại bánh (mì, gối, hamburger)
  - Nhà máy (Factory) sẽ tạo ra đúng loại bánh theo yêu cầu
  - Sau khi bánh được tạo ra, quy trình làm bánh không thể thay đổi

## 2. Chiến Lược Nấu Ăn (Strategy Pattern)
- **Tình huống**: Nấu một món cơm gà
- **Cách hoạt động**:
  - Bạn có thể thay đổi cách chế biến (chiên, hấp, luộc)
  - Nguyên liệu vẫn là cơm và gà
  - Có thể thay đổi cách nấu bất cứ lúc nào

## 3. So Sánh Qua Ví Dụ Nhà Hàng

### Factory Pattern - Bếp Trưởng Tạo Món
- Khách gọi "Phở bò"
- Bếp sẽ tạo ra tô phở theo công thức chuẩn
- Sau khi món được làm xong, không thể thay đổi cách nấu

### Strategy Pattern - Cách Ăn Món
- Khách có tô phở
- Có thể thay đổi cách ăn:
  + Cho nhiều tương ớt
  + Cho nhiều rau
  + Ăn với quẩy
- Có thể thay đổi cách ăn trong suốt bữa ăn

## 4. Ví Dụ Khác Trong Cuộc Sống

### Factory Pattern
1. **Tiệm cà phê**:
   - Máy pha cà phê tạo ra các loại cà phê khác nhau
   - Mỗi nút bấm sẽ tạo ra một loại cà phê cụ thể
   - Sau khi pha xong không thể thay đổi

2. **Xưởng may**:
   - Nhận đơn đặt hàng áo (sơ mi, thể thao, vest)
   - Sản xuất đúng loại áo theo yêu cầu
   - Sau khi may xong không thể thay đổi kiểu áo

### Strategy Pattern
1. **Đi làm**:
   - Cùng một điểm đến
   - Có thể chọn nhiều cách đi:
     + Đi xe máy khi trời đẹp
     + Đi taxi khi trời mưa
     + Đi xe buýt khi muốn tiết kiệm
   - Có thể thay đổi phương tiện mỗi ngày

2. **Giặt quần áo**:
   - Cùng một mục đích giặt sạch
   - Có thể chọn:
     + Giặt tay cho đồ mới
     + Giặt máy cho đồ bình thường
     + Giặt khô cho đồ đặc biệt
   - Có thể thay đổi cách giặt tùy theo loại quần áo
```

Qua những ví dụ trên, ta có thể thấy:

- Factory Pattern tập trung vào việc "tạo ra cái gì" và sau khi tạo ra thì không thể thay đổi
- Strategy Pattern tập trung vào việc "làm như thế nào" và có thể thay đổi cách thức thực hiện bất cứ lúc nào

Bạn có muốn tôi giải thích thêm về các ví dụ khác không?
