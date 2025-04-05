https://devopsedu.vn/courses/devops-for-freshers/

1. tao file jenkins.sh

```
#!/bin/bash

apt install openjdk-17-jdk openjdk-17-jre -y
java --version
wget -p -O - https://pkg.jenkins.io/debian/jenkins.io.key | apt-key add -
sh -c 'echo deb http://pkg.jenkins.io/debian-stable binary/ > /etc/apt/sources.list.d/jenkins.list'
apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 5BA31D57EF5975CA
apt-get update
apt install jenkins -y
systemctl start jenkins
ufw allow 8080
```

sudo sh jenkins.sh

==================
kiểm tra trạng thái jenkins
`systemctl status jenkins`

If you want to access `http://172.16.0.138/` using `http://locnamhai.com` within your **local network (LAN)**, you need to configure **DNS resolution** or modify your **hosts file**. Here are two methods to achieve this:

---

## ✅ **Method 1: Modify the Hosts File (Local Only)**

This method works **only on your computer** and does not affect other devices on the network.

### **1️⃣ On Linux / macOS**

1. Open the terminal.
2. Edit the hosts file:
   ```bash
   sudo nano /etc/hosts
   ```
3. Add this line at the end:
   ```
   172.16.0.138  locnamhai.com
   ```
4. Save the file (`Ctrl + X`, then `Y`, then `Enter`).
5. Flush the DNS cache:
   ```bash
   sudo systemctl restart nscd  # For Linux (if nscd is installed)
   sudo dscacheutil -flushcache  # For macOS
   ```
6. Now, try accessing `http://locnamhai.com`.

### 🚀 **Install Nginx and Map Ports on Ubuntu Server**

### 🎯 **Summary**

✔ Installed Nginx
✔ Configured port mapping
✔ Allowed traffic in the firewall
✔ Set up optional reverse proxy

---

## ✅ **Step 1: Install Nginx**

Run the following commands to install Nginx on Ubuntu:

```bash
sudo apt update
sudo apt install nginx -y
```

Once installed, check if Nginx is running:

```bash
sudo systemctl status nginx
```

If it's not running, start and enable it to run on boot:

```bash
sudo systemctl enable --now nginx
```

---

## ✅ **Step 2: Open Firewall for Nginx (If Enabled)**

Check if **UFW (Uncomplicated Firewall)** is enabled:

```bash
sudo ufw status
```

If UFW is active, allow HTTP and HTTPS traffic:

```bash
sudo ufw allow 'Nginx Full'
```

Or allow specific ports:

```bash
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw reload
```

---

## ✅ **Step 3: Configure Port Mapping in Nginx**

By default, Nginx serves on **port 80** (HTTP) and **port 443** (HTTPS). If you want to change the port:

1️⃣ Open the Nginx default configuration file:

```bash
sudo nano /etc/nginx/sites-available/default
```

2️⃣ Find the `server` block and modify the `listen` directive:

```nginx
server {
    listen 80;
    server_name locnamhai.com;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection keep-alive;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

```

2025/03/31 04:14:03 [emerg] 4072#4072: host not found in upstream "locnamhai.com" in /etc/nginx/conf.d/jenkins.locnamhai.com.conf:6
nginx: configuration file /etc/nginx/nginx.conf test failed

3️⃣ Save and exit (`Ctrl + X`, then `Y`, then `Enter`).

4️⃣ Test the configuration:

```bash
sudo nginx -t
```

5️⃣ Restart Nginx:

```bash
sudo systemctl restart nginx
```

---

## ✅ **Step 4: Verify Nginx is Running on the New Port**

Check if Nginx is listening on the new port (e.g., 8080):

```bash
sudo netstat -tulnp | grep nginx
```

or

```bash
sudo ss -tulnp | grep nginx
```

Now, you can access your server using:

```
http://your-server-ip:8080
```

---

## ✅ **Optional: Enable Reverse Proxy (Forward Traffic to Another Port)**

If you want Nginx to forward requests from **port 80 to another port (e.g., 3000)**, update your configuration:

```nginx
server {
    listen 80;
    server_name your_domain_or_IP;

    location / {
        proxy_pass http://127.0.0.1:3000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

After saving, test and restart Nginx:

```bash
sudo nginx -t
sudo systemctl restart nginx
```

Now, all requests to `http://your-server-ip` will be forwarded to `http://127.0.0.1:3000`.

---
