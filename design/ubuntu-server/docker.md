### ✅ **1. Cài đặt Docker**

- **Trên Ubuntu:**
  ```bash
  sudo apt update
  sudo apt install docker.io -y
  sudo systemctl enable --now docker
  ```
- **Kiểm tra Docker đã cài đặt thành công chưa**
  ```bash
  docker --version
  docker run hello-world
  ```

### ✅ **2. Các lệnh Docker cơ bản**

- **Xem danh sách container đang chạy:**
  ```bash
  docker ps
  ```
- **Xem danh sách tất cả container (kể cả đã dừng):**
  ```bash
  docker ps -a
  ```
- **Chạy một container (ví dụ Nginx):**
  ```bash
  docker run -d -p 8080:80 --name webserver nginx
  ```
- **Dừng container:**
  ```bash
  docker stop webserver
  ```
- **Xóa container:**
  ```bash
  docker rm webserver
  ```
- **Xóa toàn bộ container đã dừng:**
  ```bash
  docker system prune -a
  ```

### ✅ **3. Làm việc với Docker Images**

- **Tìm và tải image từ Docker Hub:**
  ```bash
  docker pull ubuntu
  ```
- **Xem danh sách image đã tải về:**
  ```bash
  docker images
  ```
- **Xóa image không cần thiết:**
  ```bash
  docker rmi ubuntu
  ```

### ✅ **4. Tạo Dockerfile & Build Image**

Tạo file **Dockerfile** để build một image Nginx tùy chỉnh:

```dockerfile
FROM nginx
COPY index.html /usr/share/nginx/html/index.html
CMD ["nginx", "-g", "daemon off;"]
```

Build image từ Dockerfile:

```bash
docker build -t my-nginx .
```

Chạy container từ image mới build:

```bash
docker run -d -p 8080:80 my-nginx
```
