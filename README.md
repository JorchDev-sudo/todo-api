✅ Todo API

📌 Overview

Todo API is a production-style REST API built with Spring Boot for managing tasks (To-Do), featuring JWT-based authentication, role-aware access control, clean layered architecture, and unit-tested business logic.

This project is part of my backend portfolio and is intentionally designed to reflect real-world Spring Boot practices, focusing on:

Security

Maintainability

Testability

Clear separation of concerns

Professional configuration and documentation

🚀 Key Features

🔐 Stateless authentication & authorization using JWT

👤 User management

📝 Task management 

🔗 One User → Many Tasks relationship

📄 Pagination, sorting and filtering

📦 DTO-based API design (request / response separation)

🧠 Centralized global exception handling

📑 Interactive API documentation (Swagger / OpenAPI)

🧪 Unit testing with Mockito (standalone)

🌍 Environment-based configuration (dev / prod)

🏗️ Architecture

The application follows a layered architecture, commonly used in professional Spring Boot projects:

controllers

 └── UserController
 └── TaskController

services

 └── UserService
 └── TaskService

repositories

 └── UserRepository
 └── TaskRepository

entities

 └── User
 └── Task

dto

 └── request
 └── response

security

 └── JwtService
 └── JwtAuthenticationFilter
 └── SecurityConfig

exceptions

 └── GlobalExceptionHandler
 └── custom exceptions


✔ Clear responsibility boundaries
✔ Business logic isolated from controllers
✔ Easily testable services

🛠️ Tech Stack

Java 17

Spring Boot 3.2.5

Spring Ecosystem

Spring Web

Spring Data JPA

Spring Security

Security

JWT

Custom security filters

Stateless session management

Persistence

Hibernate

H2 (development)

PostgreSQL (production)

Flyway migrations

Tooling & Quality

Maven

JUnit 5

Mockito (standalone)

Swagger / OpenAPI (springdoc)

🔐 Security Design

JWT-based authentication (stateless)

Custom authentication filter

Endpoint protection per authenticated user

Proper HTTP status handling:

401 Unauthorized

403 Forbidden

Centralized security error handling

This setup mirrors how authentication is typically implemented in real backend systems.

📑 API Documentation

Interactive API documentation is available via Swagger:

http://localhost:8080/swagger-ui.html


or

http://localhost:8080/swagger-ui/index.html

⚙️ Running the Application:

1️⃣ Clone the repository
git clone https://github.com/JorchDev-sudo/todo-api.git
cd todo-api

2️⃣ Required Environment Variables
JWT_SECRET=your_secret_key


This variable can be configured via:

System environment variables

IDE Configurations

Deployment environment

3️⃣ Run the application
mvn spring-boot:run

🌍 Spring Profiles:
Profile	Database
dev	H2 (in-memory)
prod	PostgreSQL + Flyway

Activate a profile with:

spring.profiles.active=dev

🧪 Testing Strategy

Focus on unit tests for controllers and services

Mockito standalone

Coverage includes:

Successful flows

Validation errors

Authorization constraints

Exception handling paths

This testing approach emphasizes business logic reliability rather than heavy integration tests.

📬 Contact

If you’d like to discuss this project or my backend experience:

💼 LinkedIn:www.linkedin.com/in/jorge-cotera-lópez-24180438a

📧 Email: jorgecoteralopez@gmail.com
