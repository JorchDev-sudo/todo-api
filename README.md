✅ Todo API

Todo API es una API REST desarrollada con Spring Boot para la gestión de tareas (To-Do), con soporte para autenticación segura mediante JWT, documentación automática con Swagger/OpenAPI, paginación, manejo global de errores y tests unitarios.

Este proyecto forma parte de mi portafolio backend y está orientado a demostrar buenas prácticas en arquitectura, seguridad, testing y configuración profesional de una API moderna en Java.

🚀 Funcionalidades:
🔐 Autenticación y autorización con JWT (stateless)
👤 Gestión de usuarios
📝 Gestión de tareas (Tasks)
🔗 Relación One User → Many Tasks
📄 Paginación y ordenamiento
📦 Uso de DTOs para requests y responses
🧠 Manejo global de excepciones
📑 Documentación interactiva con Swagger
🧪 Tests unitarios con Mockito (standalone)
🌍 Configuración por perfiles (dev / prod)

Arquitectura en capas bien definida:
controllers
 ├── UserController
 └── TaskController

services
 ├── UserService
 └── TaskService

repositories
 ├── UserRepository
 └── TaskRepository

entities
 ├── User
 └── Task

dto
 ├── request
 └── response

security
 ├── JwtService
 ├── JwtAuthenticationFilter
 └── SecurityConfig

exceptions
 ├── GlobalExceptionHandler
 └── custom exceptions

🛠️ Stack tecnológico
Java 17
Spring Boot 3.5.8
Spring Web
Spring Data JPA
Spring Security
JWT (jjwt)
Hibernate
Flyway
H2 (dev)
PostgreSQL (prod)
Swagger / OpenAPI (springdoc)
JUnit 5
Mockito (standalone)
Maven

🔐 Seguridad
Autenticación basada en JWT
Filtros personalizados de seguridad
Configuración stateless
Protección de endpoints por usuario autenticado
Manejo correcto de errores 401 / 403

⚙️ Configuración y ejecución

1️⃣ Clonar el repositorio
git clone https://github.com/tu-usuario/todo-api.git
cd todo-api

2️⃣ Variables de entorno requeridas
JWT_SECRET

3️⃣ Ejecutar la aplicación
mvn spring-boot:run

🌍 Perfiles de ejecución
Perfil	Base de datos
dev	H2 
prod	PostgreSQL y Flyway

Activación del perfil:
spring.profiles.active=dev
