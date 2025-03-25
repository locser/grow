Để kết nối **SSH từ máy Mac tới Ubuntu Server** trong cùng một mạng Wi-Fi, bạn cần làm theo các bước sau:

---

## 🛠 **1. Kiểm Tra Địa Chỉ IP Của Ubuntu Server**

Trên Ubuntu Server, chạy:

```bash
ip a
```

Tìm dòng có **"wlan0"** hoặc **"eth0"**, bạn sẽ thấy địa chỉ IP như `192.168.x.x`.

📌 **Ví dụ**: Nếu IP của Ubuntu Server là **192.168.1.100**, bạn sẽ dùng địa chỉ này để SSH.

---

## 🔑 **2. Cài Đặt & Kích Hoạt SSH Trên Ubuntu Server**

Nếu Ubuntu chưa có SSH, cài đặt nó bằng:

```bash
sudo apt update
sudo apt install openssh-server -y
```

Sau đó, khởi động SSH:

```bash
sudo systemctl enable --now ssh
sudo systemctl status ssh
```

Nếu thấy **active (running)** là SSH đang chạy.

---

## 💻 **3. Kết Nối SSH Từ MacOS**

Trên máy Mac, mở Terminal và chạy:

```bash
ssh username@192.168.1.100
```

📌 **Thay `username` bằng user trên Ubuntu Server**, ví dụ:

```bash
ssh ubuntu@192.168.1.100
```

Nhập mật khẩu khi được yêu cầu.

---

## 🔑 **4. Thiết Lập SSH Không Cần Mật Khẩu (SSH Key)**

Nếu bạn muốn **kết nối mà không cần nhập mật khẩu**, thực hiện như sau:

### 📌 **4.1. Tạo SSH Key Trên Mac**

Trên máy Mac, chạy:

```bash
ssh-keygen -t rsa -b 4096
```

Nhấn **Enter** nhiều lần để dùng cài đặt mặc định.

---

### 📌 **4.2. Copy SSH Key Sang Ubuntu**

Dùng lệnh sau để copy SSH key từ Mac sang Ubuntu:

```bash
ssh-copy-id ubuntu@192.168.1.100
```

(Nhập mật khẩu lần đầu để xác thực).

Nếu `ssh-copy-id` không hoạt động, có thể copy thủ công:

```bash
cat ~/.ssh/id_rsa.pub | ssh ubuntu@192.168.1.100 "mkdir -p ~/.ssh && cat >> ~/.ssh/authorized_keys"
```

Sau đó, thử SSH lại:

```bash
ssh ubuntu@192.168.1.100
```

Nếu vào được mà **không cần mật khẩu**, bạn đã thiết lập thành công!

---

## 🔄 **5. Kiểm Tra & Khắc Phục Lỗi Nếu Không Kết Nối Được**

1️⃣ **Kiểm tra SSH có chạy không**

```bash
sudo systemctl status ssh
```

Nếu không chạy, khởi động lại:

```bash
sudo systemctl restart ssh
```

2️⃣ **Kiểm tra firewall (nếu có)**
Nếu dùng **UFW** (Uncomplicated Firewall), mở cổng SSH:

```bash
sudo ufw allow ssh
sudo ufw enable
```

Kiểm tra trạng thái UFW:

```bash
sudo ufw status
```

3️⃣ **Kiểm tra kết nối mạng**
Trên máy Mac, thử ping Ubuntu Server:

```bash
ping 192.168.1.100
```

Nếu không có phản hồi, có thể hai máy chưa cùng mạng hoặc Ubuntu có lỗi cấu hình mạng.

---

## 🚀 **Tóm Lại**

✔ **Lấy địa chỉ IP của Ubuntu (`ip a`)**.
✔ **Cài SSH Server trên Ubuntu (`sudo apt install openssh-server`)**.
✔ **Kết nối từ Mac bằng `ssh user@192.168.x.x`**.
✔ **Thiết lập SSH Key để không cần nhập mật khẩu**.
✔ **Mở cổng SSH trong firewall nếu cần (`sudo ufw allow ssh`)**.

Tốt! Giờ bạn có thể tiếp tục với việc thiết lập user và firewall.

### 1️⃣ **Thiết lập User**

Mặc định, bạn thường đăng nhập với user `root`, nhưng tốt hơn là tạo một user thường và cấp quyền sudo để tăng bảo mật.

#### **Tạo user mới**

Thay `youruser` bằng tên bạn muốn:

```bash
sudo adduser youruser
```

Nhập mật khẩu và thông tin cần thiết.

#### **Cấp quyền sudo cho user**

```bash
sudo usermod -aG sudo youruser
```

Giờ bạn có thể đăng nhập bằng:

```bash
su - youruser
```

#### **Cấu hình SSH để chỉ cho phép user mới** _(Tùy chọn, nếu muốn bảo mật hơn)_

Mở file cấu hình SSH:

```bash
sudo nano /etc/ssh/sshd_config
```

- Tìm dòng `PermitRootLogin` và đổi thành `no`.
- Thêm dòng:
  ```bash
  AllowUsers youruser
  ```
- Lưu lại (`Ctrl + X`, `Y`, `Enter`).

Khởi động lại SSH:

```bash
sudo systemctl restart ssh
```

---

### 2️⃣ **Thiết lập Firewall (UFW - Uncomplicated Firewall)**

Ubuntu dùng `ufw` để quản lý firewall.

#### **Cài đặt UFW (nếu chưa có)**

```bash
sudo apt update && sudo apt install ufw -y
```

#### **Cấu hình cơ bản**

- **Mở SSH** _(nếu không, bạn sẽ bị khóa ngoài!)_

  ```bash
  sudo ufw allow OpenSSHx   # Mặc định cổng SSH là 22
  ```

  Hoặc nếu dùng cổng SSH tùy chỉnh:

  ```bash
  sudo ufw allow 2222/tcp  # Thay 2222 bằng cổng SSH bạn đã đặt
  ```

- **Mở HTTP/HTTPS (nếu bạn chạy web server)**

  ```bash
  sudo ufw allow http
  sudo ufw allow https
  ```

- **Chặn tất cả các kết nối không mong muốn**

  ```bash
  sudo ufw default deny incoming
  sudo ufw default allow outgoing
  ```

- **Bật UFW**

  ```bash
  sudo ufw enable
  ```

- **Kiểm tra trạng thái**
  ```bash
  sudo ufw status verbose
  ```

---

### 🎯 **Tiếp theo làm gì?**

- Nếu bạn đã xong user và firewall, có thể tiếp tục với Docker & Kubernetes (Bước 3).
- Nếu muốn bảo mật hơn, có thể bật `fail2ban` để chống brute-force SSH.

🔥 **Firewall nâng cao trong Linux (UFW, iptables, nftables)**

Bạn đã biết cách dùng **UFW** (Uncomplicated Firewall), nhưng nếu muốn kiểm soát mạnh hơn, bạn có thể tìm hiểu về **iptables** và **nftables**.

---

## 🛡 **1. UFW nâng cao**

Mặc dù UFW là giao diện đơn giản của `iptables`, nhưng nó vẫn có nhiều tính năng nâng cao.

### 1️⃣ **Chặn một địa chỉ IP cụ thể**

Nếu bạn phát hiện một IP đáng ngờ, có thể chặn như sau:

```bash
sudo ufw deny from 192.168.1.100
```

### 2️⃣ **Chặn theo phạm vi IP (CIDR)**

Ví dụ, chặn toàn bộ dải IP **192.168.1.0 - 192.168.1.255**

```bash
sudo ufw deny from 192.168.1.0/24
```

### 3️⃣ **Chặn theo quốc gia**

Dùng `xtables-addons` để chặn tất cả IP từ một quốc gia (ví dụ: Trung Quốc):

```bash
sudo apt install xtables-addons-common
sudo ipset create china hash:net
sudo iptables -A INPUT -m set --match-set china src -j DROP
```

(Lấy danh sách IP từ các nguồn như [IPdeny](http://www.ipdeny.com/)).

### 4️⃣ **Giới hạn số lần kết nối SSH để chống brute-force**

```bash
sudo ufw limit OpenSSH
```

Điều này sẽ giới hạn số lần kết nối SSH trong một khoảng thời gian ngắn, giúp bảo vệ khỏi các cuộc tấn công thử mật khẩu.

---

## 🔥 **2. iptables – Firewall mạnh mẽ hơn**

**iptables** là công cụ mạnh hơn UFW, cho phép kiểm soát chi tiết hơn lưu lượng mạng.

### 1️⃣ **Kiểm tra danh sách luật hiện tại**

```bash
sudo iptables -L -v -n
```

### 2️⃣ **Chặn một địa chỉ IP cụ thể**

```bash
sudo iptables -A INPUT -s 192.168.1.100 -j DROP
```

### 3️⃣ **Chặn tất cả lưu lượng ngoại trừ các cổng cụ thể**

```bash
sudo iptables -P INPUT DROP
sudo iptables -P FORWARD DROP
sudo iptables -P OUTPUT ACCEPT
sudo iptables -A INPUT -p tcp --dport 22 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 443 -j ACCEPT
```

Điều này chặn tất cả kết nối trừ SSH, HTTP và HTTPS.

### 4️⃣ **Chặn Ping (ICMP)** để tránh bị quét mạng

```bash
sudo iptables -A INPUT -p icmp --icmp-type echo-request -j DROP
```

### 5️⃣ **Lưu lại luật iptables** (vì khi reboot, các luật sẽ bị mất)

```bash
sudo iptables-save | sudo tee /etc/iptables/rules.v4
```

---

## ⚡ **3. nftables – Công cụ firewall hiện đại hơn iptables**

_nftables_ là hệ thống firewall mới thay thế iptables trên các bản Linux mới.

### 1️⃣ **Cài đặt nftables**

```bash
sudo apt install nftables
```

### 2️⃣ **Kiểm tra bảng nftables**

```bash
sudo nft list ruleset
```

### 3️⃣ **Tạo luật mới chặn một IP**

```bash
sudo nft add table inet my_firewall
sudo nft add chain inet my_firewall input { type filter hook input priority 0 \; }
sudo nft add rule inet my_firewall input ip saddr 192.168.1.100 drop
```

---

## 🏆 **Khi nào nên dùng UFW, iptables hay nftables?**

| Công cụ      | Độ dễ dùng | Hiệu suất        | Chức năng nâng cao |
| ------------ | ---------- | ---------------- | ------------------ |
| **UFW**      | Dễ nhất    | Tốt              | Hạn chế            |
| **iptables** | Trung bình | Tốt              | Rất mạnh           |
| **nftables** | Khó hơn    | Tốt hơn iptables | Mạnh nhất          |

- **Dùng UFW** nếu bạn chỉ cần bảo mật cơ bản mà không cần tùy chỉnh nhiều.
- **Dùng iptables** nếu bạn cần bảo mật chi tiết và linh hoạt hơn.
- **Dùng nftables** nếu bạn làm hệ thống lớn hoặc muốn hiệu suất cao hơn.

Bạn muốn thử nghiệm phần nào tiếp theo? 🚀

🔥 **Chỉ cho phép một số IP truy cập vào server (Chặn tất cả IP khác)**

Bạn có thể dùng **UFW** hoặc **iptables** để thiết lập danh sách **whitelist IP**.

---

## ✅ **Cách 1: Dùng UFW (Dễ nhất)**

Giả sử bạn muốn **chỉ cho phép** 2 địa chỉ IP sau truy cập vào server:

- **192.168.1.10**
- **203.0.113.50**

### 📌 **Các bước thực hiện**

1️⃣ **Chặn tất cả các kết nối đến**

```bash
sudo ufw default deny incoming
```

2️⃣ **Cho phép IP cụ thể truy cập**

```bash
sudo ufw allow from 192.168.1.10
sudo ufw allow from 203.0.113.50
```

3️⃣ **Nếu chỉ muốn cho phép IP truy cập vào SSH (cổng 22)**

```bash
sudo ufw allow from 192.168.1.10 to any port 22
sudo ufw allow from 203.0.113.50 to any port 22
```

4️⃣ **Bật lại UFW để áp dụng thay đổi**

```bash
sudo ufw enable
sudo ufw reload
```

5️⃣ **Kiểm tra lại firewall**

```bash
sudo ufw status
```

Nếu đúng, bạn sẽ thấy danh sách IP được phép truy cập.

---

## ✅ **Cách 2: Dùng iptables (Mạnh hơn, chi tiết hơn)**

Nếu bạn muốn kiểm soát nhiều hơn, dùng `iptables` là một lựa chọn tốt.

1️⃣ **Chặn tất cả kết nối đến**

```bash
sudo iptables -P INPUT DROP
```

2️⃣ **Cho phép IP cụ thể truy cập vào tất cả dịch vụ**

```bash
sudo iptables -A INPUT -s 192.168.1.10 -j ACCEPT
sudo iptables -A INPUT -s 203.0.113.50 -j ACCEPT
```

3️⃣ **Nếu chỉ muốn cho phép IP truy cập SSH (cổng 22)**

```bash
sudo iptables -A INPUT -p tcp --dport 22 -s 192.168.1.10 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 22 -s 203.0.113.50 -j ACCEPT
```

4️⃣ **Lưu lại luật để không bị mất sau khi reboot**

```bash
sudo iptables-save | sudo tee /etc/iptables/rules.v4
```

5️⃣ **Kiểm tra lại firewall**

```bash
sudo iptables -L -v -n
```

---

## 🚀 **Lựa chọn nào phù hợp?**

| **Tình huống**                              | **Dùng UFW** | **Dùng iptables** |
| ------------------------------------------- | ------------ | ----------------- |
| Dễ thiết lập, không cần tùy chỉnh nhiều     | ✅           | ❌                |
| Kiểm soát sâu hơn, chặn theo nhiều tiêu chí | ❌           | ✅                |
| Cần bảo mật nhanh, hiệu quả                 | ✅           | ✅                |

👉 Nếu bạn chỉ cần chặn đơn giản **=> dùng UFW**
👉 Nếu bạn cần kiểm soát nâng cao **=> dùng iptables**

Bạn muốn áp dụng với dịch vụ nào (SSH, Web, Database...)? 🚀
