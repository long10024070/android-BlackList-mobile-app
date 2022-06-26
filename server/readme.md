# Tổng quan
Đây là server tự động chặn số nếu người dùng bật tính năng nhận recommend từ hệ thống. Với điều kiện: sđt đó phải trong Blacklist trực tiếp của >= 2 người dùng và số lượng người dùng đồng ý đây là số Black > 2 lần số người dùng đồng ý đây là số White

# Requirement

```python
import firebase_admin
```

# Sử dụng

```sh
cd server
python main.py
```