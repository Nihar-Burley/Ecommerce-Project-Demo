🚀 E-Commerce Microservices Platform
⚡ Reactive | Scalable | Production-Grade Backend System










📌 Overview

A production-grade microservices backend system built using Spring Boot + WebFlux, designed to simulate a real-world e-commerce platform.

🚀 This project demonstrates:
Reactive Programming (Spring WebFlux)
Microservices Architecture
API Gateway Routing
Service Discovery (Eureka)
Inter-Service Communication
Advanced Testing (BDD + Unit Testing)
Centralized Exception Handling
🧱 System Architecture
Client
   ↓
API Gateway (Spring Cloud Gateway)
   ↓
--------------------------------------------------
|     User     |    Product    |     Cart        |
|   Service    |    Service    |    Service      |
--------------------------------------------------
   ↓
Eureka Server (Service Registry)
🔄 End-to-End Flow
User → Add to Cart
   ↓
Cart Service
   ↓
Fetch Product → Product Service
   ↓
Validate / Adjust Stock
   ↓
Update Cart
   ↓
Return Response
🧩 Microservices
👤 User Service
Register User
Login (Token Simulation)
Get User by ID
Get All Users
Delete User
📦 Product Service
Create Product
Update Product
Get Product by ID
Get All Products
Delete Product
Increase Stock
Reduce Stock
🛒 Cart Service (Core Business Logic 🔥)
Add Item to Cart
Update Cart Quantity
Remove Item
Get Cart
Sync Stock with Product Service
⚙️ Tech Stack
Layer	Technology
Language	Java 11
Framework	Spring Boot
Reactive	Spring WebFlux
Gateway	Spring Cloud Gateway
Discovery	Eureka
Testing	JUnit 5, Mockito, Cucumber
Build	Maven
API Docs	Swagger (OpenAPI)
🌐 API Gateway
Responsibilities:
Central routing
Entry point for all APIs
Logging
Future Enhancements:
JWT Authentication
Rate limiting
Circuit breaker
🧭 Service Discovery (Eureka)
All services register with Eureka
Gateway dynamically resolves services
Enables scalability and failover
🔐 Security

Header-based authentication:

X-User-Id: 1
X-User-Role: USER
Roles:
USER
ADMIN
Behavior:
Invalid role → 403 FORBIDDEN
📡 API Documentation (Swagger / OpenAPI)

Swagger is integrated using SpringDoc OpenAPI.

Dependency
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.x.x</version>
</dependency>
🌐 Swagger URLs
Service	URL
User Service	http://localhost:9001/swagger-ui/index.html

Product Service	http://localhost:9002/swagger-ui/index.html

Cart Service	http://localhost:9003/swagger-ui/index.html
🌐 Via API Gateway
http://localhost:8080/<service>/swagger-ui/index.html

Example:

http://localhost:8080/product-service/swagger-ui/index.html
🔁 Inter-Service Communication
productClient.getProduct();
productClient.reduceStock();
productClient.increaseStock();
📊 API Endpoints Overview
👤 User APIs
Method	Endpoint	Description
POST	/api/v1/users/register	Register user
POST	/api/v1/users/login	Login
GET	/api/v1/users/{id}	Get user
GET	/api/v1/users	Get all users
DELETE	/api/v1/users/{id}	Delete user
📦 Product APIs
Method	Endpoint	Description
POST	/api/v1/products	Create product
GET	/api/v1/products	Get all products
GET	/api/v1/products/{id}	Get product
PUT	/api/v1/products/{id}	Update product
DELETE	/api/v1/products/{id}	Delete product
PUT	/api/v1/products/{id}/reduce/{qty}	Reduce stock
PUT	/api/v1/products/{id}/increase/{qty}	Increase stock
🛒 Cart APIs
Method	Endpoint	Description
POST	/api/v1/cart/add	Add item
PUT	/api/v1/cart/update	Update item
DELETE	/api/v1/cart/remove	Remove item
GET	/api/v1/cart	Get cart
⚠️ Exception Handling

Centralized via GlobalExceptionHandler

Standard Response:
{
  "message": "Error message",
  "errorCode": "ERROR_CODE",
  "status": 400,
  "timestamp": "..."
}
🧪 Testing Strategy
✅ Unit Testing
Mockito
StepVerifier
✅ Controller Testing
WebTestClient
✅ BDD Testing (Cucumber)
User Service
Product Service
Cart Service
🧠 BDD Approach
No hardcoded values
Dynamic test data
Context-driven testing
External dependencies mocked
📁 BDD Structure
bdd/
 ├── config/
 ├── steps/
 ├── runner/
 └── features/
🧪 Sample Scenario
Scenario: Add item to cart successfully
  Given a valid user with role "USER"
  And a product exists with stock 10
  And a valid add to cart request with quantity 2
  When the client calls add to cart API
  Then the response status should be 200
⚡ Reactive Programming
Type	Usage
Mono	Single response
Flux	Multiple responses
🧩 Design Patterns
DTO Pattern
Mapper Pattern
Service Layer
Builder Pattern
Global Exception Handling
📦 Project Structure
com.company.<service>
 ├── controller
 ├── service
 ├── repository
 ├── dto
 ├── entity
 ├── mapper
 ├── exception
 ├── config
 ├── client
 ├── bdd
 └── unit tests
🚀 How to Run
1️⃣ Start Eureka
http://localhost:8761
2️⃣ Start Services
User Service
Product Service
Cart Service
3️⃣ Start API Gateway
http://localhost:8080
4️⃣ Access APIs
http://localhost:8080/api/v1/...
📊 Logging
API: Add to cart | userId=1 role=USER
🔥 Key Highlights
Reactive microservices architecture
API Gateway + Eureka integration
Clean and scalable design
Inter-service communication
Production-level exception handling
BDD + Unit testing
Dynamic test data
🚀 Future Enhancements
JWT Authentication
OAuth2
Docker + Kubernetes
Kafka
Distributed tracing
Circuit breaker
💡 What This Project Proves
Microservices architecture expertise
Reactive backend development
Real-world business logic implementation
Advanced testing strategy
Scalable system design
