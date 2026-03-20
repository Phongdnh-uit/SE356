delivery/
├── src/main/java/com/uit/se356/delivery/
│   │
│   ├── domain/                         # TẦNG 1: Core Nghiệp Vụ (Không phụ thuộc framework)
│   │   ├── entities/                   # Chứa model nghiệp vụ: Route, DeliveryTask, Vehicle...
│   │   ├── exceptions/                 # Các lỗi đặc thù: RouteNotFoundException...
│   │   └── repositories/               # Interfaces định nghĩa việc lưu trữ (Dip - Dependency Inversion)
│   │
│   ├── application/                    # TẦNG 2: Use Cases (Điều phối logic)
│   │   ├── dto/                        # Các object truyền tải dữ liệu (Request, Response, Event payload)
│   │   ├── ports/                      # Interfaces để giao tiếp với các tầng ngoài
│   │   │   ├── in/                     # Input Ports: Interfaces cho Use Cases (vd: OptimizeRouteUseCase)
│   │   │   └── out/                    # Output Ports: Interfaces gọi ra ngoài (vd: DeliveryEventPublisher)
│   │   └── services/                   # Implements các Input Ports (Chứa logic orchestrate)
│   │
│   ├── presentation/                   # TẦNG 3: Entrypoints (Điểm vào của hệ thống)
│   │   ├── api/                        # REST Controllers (Giao tiếp đồng bộ với App/Web)
│   │   │   └── DeliveryController.java
│   │   ├── messaging/                  # Cổng vào bất đồng bộ
│   │   │   └── consumers/              # Kafka Listeners (vd: TestListener của bạn nằm ở đây)
│   │   └── websocket/                  # Cổng mở kết nối real-time đẩy data xuống client
│   │
│   └── infrastructure/                 # TẦNG 4: Adapters (Công nghệ cụ thể)
│       ├── persistence/                # Implements các interfaces ở domain.repositories (JPA, Hibernate)
│       ├── messaging/                  # Công nghệ xử lý Message
│       │   ├── config/                 # Cấu hình KafkaTopic, KafkaTemplate
│       │   └── producers/              # Implements DeliveryEventPublisher (Đẩy message vào Kafka)
│       ├── routing/                    # Implements interface tính khoảng cách (Gọi GraphHopper/Google Maps)
│       └── optimization/               # Engine tối ưu thuật toán (OptaPlanner, ALNS)
│
└── build.gradle.kts                    # File quản lý thư viện riêng của module delivery