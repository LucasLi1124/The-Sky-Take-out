# Sky Take-Out (苍穹外卖)

A full-stack food delivery system built with Java Spring Boot. This project simulates a typical take-out platform with features such as user ordering, dish and set meal management, shopping cart, and order processing. Ideal for learning backend development and system architecture design.

## 🚀 Tech Stack

- **Backend Framework**: Spring Boot 2.7.3
- **Database**: MySQL + MyBatis
- **Cache**: Redis
- **Security**: JWT Authentication + Custom Interceptors
- **Logging**: SLF4J + Logback
- **API Testing**: Postman / Apifox
- **Utilities**: Lombok, BeanUtils, etc.
- **Documentation**: Swagger3
- **Deployment**: Docker / Alibaba Cloud / Nginx (extendable)

## 🧩 Project Structure

- `sky-common`: Shared constants and utility classes
- `sky-pojo`: Entity classes, DTOs, and VOs
- `sky-server`: Core business logic (controllers, services, mappers)

## 🌟 Features

### User Side
- WeChat-style login (simulated)
- Address management
- Browse and search dishes/set meals
- Shopping cart
- Order submission and status tracking

### Admin Side
- CRUD for dishes and set meals
- Category management
- Employee and user management
- Data dashboard and statistics

### System Level
- Custom distributed ID generator
- Global exception handling
- Unified API response wrapper
- Role-based permission control with annotations

