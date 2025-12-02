# Address Book Application

A RESTful API service for managing multiple address books and contacts, built with Spring Boot 3.4.12 and Java 17.

---

## 📋 Table of Contents

1. [Quick Start](#quick-start)
2. [Overview](#overview)
3. [Features](#features)
4. [Tech Stack](#tech-stack)
5. [Project Structure](#project-structure)
6. [Architecture](#architecture)
7. [Getting Started](#getting-started)
8. [API Reference](#api-reference)
9. [Data Models](#data-models)
10. [Deployment](#deployment)
11. [Testing](#testing)
12. [Error Handling](#error-handling)
13. [Configuration](#configuration)
14. [Contributing](#contributing)

---

## 🚀 Quick Start

```powershell
# Clone repository
git clone https://github.com/sumanmondal1994/AddressBookApplication.git
cd AddressBookApplication

# Run locally with H2 database (default)
./mvnw spring-boot:run

# Access Swagger UI
http://localhost:9000/swagger-ui.html

# Run with Docker Compose (with PostgreSQL)
docker-compose up -d

# Deploy to Kubernetes
kubectl apply -f k8s/
```

**Endpoints**: 17 total (8 V1 AddressBook + 1 V2 + 8 Contact)  
**Database**: H2 (dev/test) | PostgreSQL (prod)  
**API Version**: 1.0.0 (OpenAPI 3.1.0)

---

## 📖 Overview

The Address Book Application is an enterprise-grade REST API for managing hierarchical contact data. It enables users to organize contacts into multiple address books while preventing duplicates and providing cross-book analytics.

### Use Cases
- Personal contact management
- Organizational directory systems
- Contact synchronization platforms
- Multi-tenant contact repositories

---

## ✨ Features

### Core Functionality
- ✅ **Multiple Address Books**: Independent contact collections per address book
- ✅ **Contact Management**: Full CRUD operations on contacts
- ✅ **Duplicate Prevention**: Prevents duplicate phone numbers within same address book
- ✅ **Cross-Book Contacts**: Same phone number allowed in different address books
- ✅ **Unique Contact Analytics**: Retrieve deduplicated contacts across all books
- ✅ **Advanced Search**: Case-insensitive partial name matching

### Developer Features
- ✅ **Pagination & Sorting**: All list endpoints support flexible pagination
- ✅ **API Versioning**: V1 (standalone) and V2 (with contacts) variants
- ✅ **Comprehensive Validation**: Request and entity-level validation
- ✅ **OpenAPI 3.1.0 Spec**: Full API documentation with Swagger UI
- ✅ **Exception Handling**: 6 specialized exception handlers + generic fallback
- ✅ **Health Checks**: Spring Boot Actuator endpoints for monitoring

### Deployment Features
- ✅ **Multi-Stage Docker Build**: Optimized 200MB images
- ✅ **Docker Compose**: Complete stack with PostgreSQL
- ✅ **Kubernetes Ready**: Production-grade manifests with health probes
- ✅ **Profile-Based Config**: dev, test, prod environments
- ✅ **Database Agnostic**: H2 and PostgreSQL support

---

## 🛠 Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.4.12 |
| **ORM** | Spring Data JPA | Included |
| **Validation** | Jakarta Validation | 3.0 |
| **Database** | H2 / PostgreSQL | Latest |
| **Build Tool** | Maven | 3.9+ |
| **Testing** | JUnit 5, Mockito, MockMvc | Latest |
| **API Docs** | SpringDoc OpenAPI | 2.8.14 |
| **Containerization** | Docker | Latest |
| **Orchestration** | Kubernetes | 1.28+ |
| **Monitoring** | Spring Boot Actuator | Included |

---

## 📁 Project Structure

### Main Source Code (`src/main/`)

```
src/main/
├── java/com/project/
│   ├── controller/           # REST Controllers (V1 & V2)
│   ├── dto/
│   │   ├── request/          # Request DTOs
│   │   └── response/         # Response DTOs + ApiResponse
│   ├── entity/               # JPA Entities
│   │   ├── AddressBook.java
│   │   └── Contact.java
│   ├── exception/            # Custom exceptions & global handler
│   │   └── GlobalExceptionHandler.java
│   ├── mapper/               # Entity ↔ DTO mappers
│   ├── repository/           # Spring Data JPA repositories
│   ├── services/             # Business logic layer
│   │   ├── AddressBookService
│   │   ├── AddressBookServiceImpl
│   │   ├── ContactService
│   │   └── ContactServiceImpl
│   └── util/                 # Utilities (PaginationHelper)
│
└── resources/
    ├── db/
    │   ├── schema/
    │   │   ├── h2-schema.sql
    │   │   └── postgresql-schema.sql
    │   └── seed/
    │       ├── h2-data.sql
    │       └── postgresql-data.sql
    ├── application.properties
    ├── application-dev.properties
    ├── application-test.properties
    └── application-prod.properties
```

### Test Structure (`src/test/`)

```
src/test/
├── java/com/addressbook/
│   ├── unit/
│   │   ├── addressbook/service/
│   │   │   └── AddressBookServiceTest.java       (17 tests)
│   │   └── contact/service/
│   │       └── ContactServiceTest.java           (18 tests)
│   ├── integration/
│   │   └── addressbook/
│   │       └── AddressBookIntegrationTest.java   (44 tests)
│   ├── fixture/
│   │   └── TestDataFactory.java
│   └── config/
│       └── AddressBookApplicationTests.java
```

### Infrastructure

```
root/
├── DockerFile               # Multi-stage Docker build
├── docker-compose.yml       # App + PostgreSQL stack
├── k8s/
│   ├── configmap.yaml       # Application config
│   ├── postgres-pvc.yaml    # 1Gi storage claim
│   ├── postgres-deployment.yaml
│   ├── deployment.yaml
│   └── service.yaml
└── pom.xml                  # Maven configuration
```

---

## 🏗 Architecture

### Layered Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                     API Layer                                 │
│    Controllers (V1/V2)  │  OpenAPI/Swagger Documentation    │
└────────────────┬─────────────────────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────────────────────┐
│                  Service Layer                                │
│  AddressBookService  │  ContactService  │  Business Logic    │
└────────────────┬─────────────────────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────────────────────┐
│                Repository Layer                               │
│   Spring Data JPA  │  Query Methods  │  DB Abstraction      │
└────────────────┬─────────────────────────────────────────────┘
                 │
┌────────────────▼─────────────────────────────────────────────┐
│                Database Layer                                 │
│     H2 (Dev/Test)  │  PostgreSQL (Production)               │
└──────────────────────────────────────────────────────────────┘
```

### Request/Response Flow

```
HTTP Request
    ↓
@RestController
    ↓
GlobalExceptionHandler (captures errors)
    ↓
Service Layer (business logic)
    ↓
Repository (data access)
    ↓
Database
    ↓
ApiResponse<T> Envelope
    ↓
HTTP Response (JSON)
```

### Exception Handling Strategy

```
Request Processing
    ├── Validation Exception (MethodArgumentNotValidException)
    │   └─→ 400 Bad Request (field errors)
    ├── Entity Validation (ConstraintViolationException)
    │   └─→ 400 Bad Request (constraint violations)
    ├── Business Logic Exception
    │   ├─→ ResourceNotFoundException → 404 Not Found
    │   ├─→ DuplicateContactException → 409 Conflict
    │   └─→ DuplicateAddressBookException → 409 Conflict
    └── Generic Exception
        └─→ 500 Internal Server Error
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (Amazon Corretto/OpenJDK)
- **Maven 3.8+**
- **Docker & Docker Compose** (for containerized deployment)
- **PostgreSQL 15+** (for production)
- **Kubernetes 1.28+** (for k8s deployment)

### Local Development

#### Setup

```powershell
# Clone repository
git clone https://github.com/sumanmondal1994/AddressBookApplication.git
cd AddressBookApplication

# Build project
./mvnw clean package -DskipTests

# Or run directly with Maven
./mvnw spring-boot:run
```

#### Access Points

```
Application:     http://localhost:9000
Swagger UI:      http://localhost:9000/swagger-ui.html
OpenAPI Spec:    http://localhost:9000/v3/api-docs
Health Check:    http://localhost:9000/actuator/health
```

### Development with Profiles

```powershell
# Development profile (H2 with seeding)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Test profile
./mvnw test

# Production profile (requires PostgreSQL)
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Docker Deployment

```powershell
# Build image
docker build -f DockerFile -t addressbook-app:latest .

# Run with Docker Compose (includes PostgreSQL)
docker-compose up -d

# Verify services
docker-compose ps

# View logs
docker-compose logs -f app

# Cleanup
docker-compose down -v
```

### Kubernetes Deployment

```powershell
# Apply all manifests
kubectl apply -f k8s/

# Monitor deployment
kubectl get pods -w
kubectl get svc

# View application logs
kubectl logs -f deployment/addressbook-app

# Port forward for local access
kubectl port-forward svc/addressbook-service 9000:80
```

---

## 🔌 API Reference

### Base URLs

| Environment | URL | Port |
|-------------|-----|------|
| **Local** | `http://localhost:9000` | 9000 |
| **Docker Compose** | `http://localhost:9000` | 9000 |
| **Kubernetes** | `http://<EXTERNAL-IP>` | 80 |
| **Swagger UI** | `/swagger-ui.html` | - |
| **OpenAPI Spec** | `/v3/api-docs` | - |

### API Overview

- **Total Endpoints**: 16
- **API Versions**: V1 (7 AddressBook + 8 Contact), V2 (1 CreateWithContacts)
- **Request Format**: JSON
- **Response Format**: Wrapped in `ApiResponse<T>` envelope
- **Authentication**: None (public API)
- **Rate Limiting**: None (application-level)

### Standard Response Format

```json
{
  "success": true,
  "message": "Operation successful",
  "response": { "data": "..." },
  "timestamp": "2025-12-01T10:30:00Z",
  "path": "/api/v1/addressbooks",
  "errors": null
}
```

### HTTP Status Codes

| Code | Meaning | Usage |
|------|---------|-------|
| **200** | OK | Successful GET, PUT, DELETE |
| **201** | Created | Successful POST |
| **400** | Bad Request | Validation/constraint errors |
| **404** | Not Found | Resource not found |
| **409** | Conflict | Duplicate data |
| **500** | Server Error | Unexpected errors |

### Address Book Endpoints (V1)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/v1/addressbooks` | Create address book | 201 |
| GET | `/api/v1/addressbooks` | Get all (paginated) | 200 |
| GET | `/api/v1/addressbooks/{id}` | Get by ID | 200 |
| GET | `/api/v1/addressbooks/name/{name}` | Get by exact name | 200 |
| GET | `/api/v1/addressbooks/search?name=...` | Search by partial name | 200 |
| PUT | `/api/v1/addressbooks/{id}` | Update address book | 200 |
| DELETE | `/api/v1/addressbooks/{id}` | Delete address book | 200 |

### Contact Endpoints (V1)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/v1/addressbooks/{addressBookId}/contacts` | Add contact | 201 |
| GET | `/api/v1/addressbooks/{addressBookId}/contacts` | Get all contacts | 200 |
| GET | `/api/v1/addressbooks/{addressBookId}/contacts/{contactId}` | Get contact | 200 |
| PUT | `/api/v1/addressbooks/{addressBookId}/contacts/{contactId}` | Update contact | 200 |
| DELETE | `/api/v1/addressbooks/{addressBookId}/contacts/{contactId}` | Remove contact | 200 |
| DELETE | `/api/v1/addressbooks/{addressBookId}/contacts` | Remove all contacts | 200 |
| DELETE | `/api/v1/addressbooks/{addressBookId}/contacts/bulk?ids=...` | Bulk delete | 200 |
| GET | `/api/v1/addressbooks/{addressBookId}/contacts/unique` | Get unique contacts | 200 |

### Address Book (V2)

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/api/v2/addressbooks` | Create with contacts | 201 |

---

## 📊 Data Models

### Request DTOs

#### AddressBookRequest
```json
{
  "name": "string (required, 2-100 chars, unique)",
  "description": "string (optional, max 200 chars)",
  "contacts": [ { "name": "...", "phoneNumber": "..." } ]
}
```

#### ContactRequest
```json
{
  "name": "string (required, min 1 char)",
  "phoneNumber": "string (required, min 1 char, unique per book)"
}
```

### Response DTOs

#### AddressBookResponse
```json
{
  "id": 1,
  "name": "Personal Contacts",
  "description": "...",
  "contactCount": 5,
  "contacts": [...],
  "createdAt": "2025-12-01T10:30:00Z",
  "updatedAt": "2025-12-01T10:30:00Z"
}
```

#### ContactResponse
```json
{
  "id": 1,
  "name": "John Doe",
  "phoneNumber": "+61412345678",
  "addressBookId": 1,
  "addressBookName": "Personal Contacts",
  "createdAt": "2025-12-01T10:30:00Z"
}
```

#### PagedResponse<T>
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false,
  "empty": false
}
```

### Database Schema

#### AddressBook Entity
| Field | Type | Constraints |
|-------|------|-------------|
| id | BIGINT | PK, auto-increment |
| name | VARCHAR(100) | NOT NULL, UNIQUE |
| description | VARCHAR(200) | Nullable |
| createdAt | TIMESTAMP | NOT NULL, auto-set |
| updatedAt | TIMESTAMP | NOT NULL, auto-update |

#### Contact Entity
| Field | Type | Constraints |
|-------|------|-------------|
| id | BIGINT | PK, auto-increment |
| name | VARCHAR(255) | NOT NULL |
| phoneNumber | VARCHAR(20) | NOT NULL |
| addressBookId | BIGINT | FK, NOT NULL |
| createdAt | TIMESTAMP | NOT NULL, auto-set |
| updatedAt | TIMESTAMP | NOT NULL, auto-update |
| **Unique** | (phoneNumber, addressBookId) | Prevents duplicates per book |

---

## 🧪 Testing

### Test Coverage

| Test Suite | Type | Count | Scope |
|-----------|------|-------|-------|
| **AddressBookServiceTest** | Unit | 17 | Service logic with Mockito |
| **ContactServiceTest** | Unit | 18 | Contact service with Mockito |
| **AddressBookIntegrationTest** | Integration | 44 | End-to-end with MockMvc |
| **Total** | - | 79 | - |

### Running Tests

```powershell
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=AddressBookIntegrationTest

# Run with coverage report
./mvnw test jacoco:report

# View coverage report
start target/site/jacoco/index.html
```

### Test Categories

**Unit Tests**: Service layer with mocked dependencies  
**Integration Tests**: Full request/response cycle with MockMvc  
**Fixtures**: TestDataFactory for consistent test data  

---

## 🚢 Deployment

### Docker

**Multi-Stage Build** (reduces image size by 70%)

```dockerfile
# Stage 1: Builder (Maven + dependencies)
FROM maven:3.9-amazoncorretto-17 AS builder

# Stage 2: Runtime (JRE only)
FROM amazoncorretto:17 AS runner
```

**Build & Run**

```powershell
# Build
docker build -f DockerFile -t addressbook-app:latest .

# Run standalone
docker run -p 9000:9000 addressbook-app:latest

# Run with Docker Compose
docker-compose up -d
```

### Docker Compose Stack

| Service | Image | Purpose |
|---------|-------|---------|
| **db** | `postgres:latest` | PostgreSQL database |
| **app** | `addressbook-app:latest` | Spring Boot application |

**Features**:
- Auto-restart on crash
- Health checks
- Persistent volumes
- Network isolation
- Environment variable configuration

### Kubernetes

**Manifests** (Production-ready)

| Manifest | Type | Purpose |
|----------|------|---------|
| `configmap.yaml` | ConfigMap | Application properties |
| `postgres-pvc.yaml` | PVC | 1Gi storage |
| `postgres-deployment.yaml` | Deployment | PostgreSQL pod |
| `deployment.yaml` | Deployment | App pod with probes |
| `service.yaml` | Service | LoadBalancer (80 → 9000) |

**Health Probes**:
- **Startup**: 5 min max (60 × 5s)
- **Readiness**: `/actuator/health/readiness`
- **Liveness**: `/actuator/health/liveness`

**Resource Limits**:
- App: 1Gi request, 2Gi limit
- DB: 256Mi request, 512Mi limit

**Deploy**

```powershell
kubectl apply -f k8s/
kubectl get pods,svc,pvc
```

---

## ⚙️ Error Handling

### Exception Handlers

| Exception | Status | Description |
|-----------|--------|-------------|
| `ResourceNotFoundException` | 404 | Resource not found |
| `DuplicateContactException` | 409 | Duplicate phone in book |
| `DuplicateAddressBookException` | 409 | Duplicate book name |
| `MethodArgumentNotValidException` | 400 | Request validation error |
| `ConstraintViolationException` | 400 | Entity validation error |
| `Exception` | 500 | Unexpected error |

### Example Error Responses

**Validation Error (400)**
```json
{
  "success": false,
  "message": "Validation failed for one or more fields",
  "response": {
    "errors": {
      "name": "Name must be between 2 and 100 characters",
      "phoneNumber": "Phone number must not be blank"
    }
  },
  "errors": { ... }
}
```

**Not Found (404)**
```json
{
  "success": false,
  "message": "Address book not found with id: 999",
  "response": null
}
```

**Conflict (409)**
```json
{
  "success": false,
  "message": "Address book with name 'Personal' already exists",
  "response": null
}
```

---

## 🔧 Configuration

### Application Profiles

| Profile | Database | Use Case | DDL |
|---------|----------|----------|-----|
| **default** | H2 | Quick dev | create-drop |
| **dev** | H2 | Dev with seed | create-drop |
| **test** | H2 | Testing | create-drop |
| **prod** | PostgreSQL | Production | validate |

### Environment Variables (Production)

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/addressbook
SPRING_DATASOURCE_USERNAME=addressbook
SPRING_DATASOURCE_PASSWORD=*****
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
```

### Connection Pool (HikariCP)

```properties
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=10
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT=20000
```

### Actuator Endpoints

```
/actuator/health           # Overall health
/actuator/health/readiness # Readiness probe
/actuator/health/liveness  # Liveness probe
/actuator/info            # Application info
/actuator/metrics         # Metrics
```

---

## 👨‍💼 Contributing

### Contact Information

- **Author**: Suman Mondal
- **Email**: mondal.suman0504@gmail.com
- **GitHub**: https://github.com/sumanmondal1994/AddressBookApplication

### API Documentation

- **OpenAPI Version**: 3.1.0
- **Swagger UI**: `/swagger-ui.html`
- **OpenAPI JSON**: `/v3/api-docs`
- **OpenAPI YAML**: `/v3/api-docs.yaml`

### Reporting Issues

Please include:
- Detailed description
- Steps to reproduce
- Expected vs. actual behavior
- Environment (Java, OS, Docker version)

---

## 📜 License

This project is licensed under the MIT License - see LICENSE file for details.

---

## 📞 Support

For issues, questions, or suggestions:
1. Check existing GitHub issues
2. Review API documentation at `/swagger-ui.html`
3. Contact: mondal.suman0504@gmail.com

---

**Last Updated**: December 1, 2025  
**Version**: 1.0.0  
**Status**: Active Development
