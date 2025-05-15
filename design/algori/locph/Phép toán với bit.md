Rất hay! Mình sẽ giải thích cách **tìm `x`** trong các phép toán **bit khác** (AND, OR, XOR...) khi biết kết quả và một trong hai toán hạng. Nhưng lưu ý: **chỉ XOR** mới dễ dàng **tìm lại x** một cách chắc chắn. Các phép khác thì **không luôn ngược lại được hoàn toàn**.

---

## ✅ 1. **XOR (`^`)**

### Công thức:

```plaintext
x ^ a = b  →  x = a ^ b
```

Vì XOR có tính chất "đảo ngược".

---

## ✅ 2. **AND (`&`)**

### Ví dụ:

```
x & 3 = 1  → tìm x?
```

Giải thích:

- `x & 3 = 1` → nghĩa là **chỉ những bit nào trùng với 1 trong số 3 mới có thể là 1**
- Nhưng không thể **xác định x duy nhất**, vì có nhiều x thỏa điều kiện.

### Cách giải:

- Biết: `x & a = b`
- Điều kiện: `b` chỉ có thể có **1** tại những vị trí mà `a` cũng có **1**
- Ta có thể suy ra: `x` có thể là **b | (\~a)\`**

Nhưng x có **nhiều giá trị thỏa mãn**, không duy nhất.

---

## ✅ 3. **OR (`|`)**

### Ví dụ:

```
x | 3 = 7  → tìm x?
```

Giải thích:

- `x | 3 = 7` → x phải có 1 ở những vị trí mà 7 có 1 và 3 không có.
- Nhưng giống với AND, không thể tìm duy nhất x.

### Tổng quát:

- Biết: `x | a = b`
- Điều kiện: `b` phải có 1 ở **ít nhất** một trong hai x hoặc a
- Không có công thức đảo ngược duy nhất như XOR

---

## ✅ 4. **NOT (`~`)**

Nếu:

```plaintext
~x = b  →  x = ~b
```

→ Phép NOT **có thể đảo ngược dễ dàng**

---

## ✅ 5. **Dịch trái (`<<`) và dịch phải (`>>`)**

### Ví dụ:

```plaintext
x << 1 = 8 → x = 8 >> 1 = 4 ✅
```

Nhưng:

- **Dịch trái (`<<`)**: **có thể đảo lại bằng `>>` nếu không có mất bit bên trái**
- **Dịch phải (`>>`)**: **có thể mất bit bên phải**, nên không đảo lại được nguyên vẹn.

Ví dụ:

```plaintext
x = 5 →  x >> 1 = 2
Nhưng 2 << 1 = 4 ≠ 5 → mất bit!
```

---

## ✅ Tóm tắt:

| Phép toán | Có đảo được không?          | Công thức đảo (nếu có)         |                        |
| --------- | --------------------------- | ------------------------------ | ---------------------- |
| XOR (`^`) | ✅ Có                       | `x = a ^ b`                    |                        |
| AND (`&`) | ❌ Không duy nhất           | Chỉ biết `x & a = b` → nhiều x |                        |
| OR (\`    | \`)                         | ❌ Không duy nhất              | Không có công thức đảo |
| NOT (`~`) | ✅ Có                       | `x = ~b`                       |                        |
| <<        | ⚠️ Có thể nếu không mất bit | `x = b >> n`                   |                        |
| >>        | ❌ Không chắc chắn          | Có thể mất bit khi đảo lại     |                        |
