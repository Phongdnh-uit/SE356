# FlashMile – Hệ thống quản lý giao hàng chặng cuối (Last-Mile Delivery)

Dự án môn học SE356. FlashMile giải quyết bài toán giao hàng chặng cuối cho thương mại điện tử, tập trung vào tốc độ, độ tin cậy và khả năng mở rộng.

## 🚀 Tính năng chính
- **Xác thực đa kênh:** Hỗ trợ Email, Phone OTP và OAuth2 (Google).
- **Phân quyền nâng cao (RBAC):** Quản lý Role và Permission linh hoạt, quét annotation tự động.
- **Theo dõi thời gian thực:** Cập nhật trạng thái đơn hàng real-time qua WebSocket/SSE.
- **Ví điện tử & Thanh toán:** Tích hợp hệ thống ví nội bộ.

## 🛠 Tech Stack

### Backend
- **Language:** Java 21
- **Framework:** Spring Boot 4.0.2
- **Architecture:** Clean Architecture + CQRS
- **Database:** PostgreSQL (Flyway for migration)
- **Caching:** Redis

### Infrastructure & DevOps
- **Object Storage:** MinIO (tương thích S3)
- **Mail Testing:** Mailpit
- **Containerization:** Docker & Docker Compose
- **CI/CD:** GitHub Actions
- **Quality:** SonarCloud, JaCoCo, Spotless

## 🏗 Kiến trúc dự án

Dự án áp dụng **Clean Architecture** kết hợp **CQRS** để đảm bảo tính bảo trì và mở rộng:

- `common/`: Thư viện dùng chung, Base classes (Entity, DTO), và Utils.
- `core/`: Module chính triển khai logic nghiệp vụ.
    - `domain/`: Business entities và repository interfaces.
    - `application/`: Use cases, Command/Query handlers.
    - `infrastructure/`: Implementations (JPA, Redis, S3, v.v.).
    - `presentation/`: REST Controllers và Security configuration.
- `docker/`: Cấu hình môi trường chạy Local và Production.
- `docs/`: Tài liệu ADR, API, và Specs.

## 📋 Yêu cầu hệ thống
- Java 21 (JDK)
- Docker & Docker Compose
- Git

## ⚡ Khởi chạy nhanh (Quick Start)

### 1. Thiết lập môi trường
Sao chép tệp biến môi trường mẫu:
```bash
cp example.env .env
```
*(Cập nhật các giá trị trong `.env` nếu cần thiết)*

### 2. Chạy hạ tầng (Infrastructure)
Sử dụng Docker Compose để khởi động Database, Redis, Mailpit, MinIO:
```bash
docker compose -f docker/local/compose.yaml up -d
```

### 3. Build và Chạy ứng dụng
```bash
# Build dự án
./gradlew build

# Chạy module core
./gradlew :core:bootRun
```

### 4. Các cổng truy cập
- **API:** `http://localhost:8080`
- **Mailpit Dashboard:** `http://localhost:8025`
- **MinIO Console:** `http://localhost:9001`

## 🛠 Quy trình phát triển

### Định dạng Code (Formatting)
Dự án sử dụng plugin **Spotless** với Google Java Format.
```bash
# Kiểm tra định dạng
./gradlew spotlessCheck

# Tự động định dạng lại code
./gradlew spotlessApply
```

### Kiểm thử & Độ bao phủ (Testing)
```bash
# Chạy test và tạo báo cáo JaCoCo
./gradlew test jacocoTestReport
```
Báo cáo coverage sẽ nằm tại `core/build/reports/jacoco/test/html/index.html`.

### Nguyên tắc kiến trúc
- **Domain layer** KHÔNG được phụ thuộc vào bất kỳ framework nào (không Spring, không JPA, không Lombok).
- Tất cả các thay đổi dữ liệu (Command) phải đi qua Application layer.

## 📚 Tài liệu tham khảo
- [API Documentation](docs/documentation.md)
- [Architecture Decision Records (ADR)](docs/adr/ADR-001-architecture.md)
