# Address Book Application

A RESTful API service for managing address books and contacts, built with Spring Boot 3.4.12 and Java 17.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [API Reference](#api-reference)
- [Data Models](#data-models)
- [Test Coverage](#test-coverage)
- [Deployment](#deployment)
- [Configuration](#configuration)

---

## Overview

The Address Book Application provides a complete solution for managing multiple address books, each containing a collection of contacts. Key features include:

- **Multiple Address Books**: Create, read, update, and delete address books
- **Contact Management**: Add, update, remove contacts within address books
- **Duplicate Prevention**: Prevents duplicate contacts (by phone number) within the same address book
- **Cross-Book Contacts**: Same contact can exist in multiple address books
- **Unique Contact Retrieval**: Get unique contacts across all address books
- **Pagination & Sorting**: All list endpoints support pagination and sorting
- **Search Functionality**: Case-insensitive partial name search for address books
- **API Versioning**: V1 and V2 API endpoints

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.4.12 |
| **Persistence** | Spring Data JPA |
| **Database (Dev/Test)** | H2 (In-Memory) |
| **Database (Production)** | PostgreSQL 15 |
| **Validation** | Jakarta Validation (Bean Validation 3.0) |
| **API Documentation** | SpringDoc OpenAPI 2.8.14 |
| **Boilerplate Reduction** | Lombok |
| **Testing** | JUnit 5, Mockito, MockMvc, AssertJ |
| **Test Data** | DataFaker 2.0.2 |
| **Code Coverage** | JaCoCo |
| **Containerization** | Docker, Docker Compose |
| **Orchestration** | Kubernetes |
| **Build Tool** | Maven |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Controller Layer                         │
│  ┌─────────────────┐ ┌──────────────────┐ ┌──────────────────┐  │
│  │AddressBookCtrl  │ │AddressBookCtrlV2 │ │  ContactCtrl     │  │
│  │   (V1 API)      │ │   (V2 API)       │ │                  │  │
│  └────────┬────────┘ └────────┬─────────┘ └────────┬─────────┘  │
└───────────┼───────────────────┼────────────────────┼────────────┘
            │                   │                    │
            ▼                   ▼                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Service Layer                            │
│  ┌─────────────────────────────┐  ┌──────────────────────────┐  │
│  │   AddressBookServiceImpl    │  │   ContactServiceImpl     │  │
│  └─────────────────────────────┘  └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
            │                                        │
            ▼                                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Repository Layer                          │
│  ┌─────────────────────────────┐  ┌──────────────────────────┐  │
│  │  AddressBookRepository      │  │   ContactRepository      │  │
│  └─────────────────────────────┘  └──────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
            │                                        │
            ▼                                        ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Database Layer                           │
│            H2 (Dev/Test) │ PostgreSQL (Production)              │
└─────────────────────────────────────────────────────────────────┘
```

### Project Structure

#### Main Source Tree

```
src/main/
├── java/com/project/
│   ├── controller/          # REST Controllers (V1 & V2 endpoints)
│   ├── dto/                 # Data Transfer Objects
│   │   ├── request/         # Request DTOs
│   │   └── response/        # Response DTOs (ApiResponse wrapper)
│   ├── entity/              # JPA Entities
│   │   ├── AddressBook      # Address book entity
│   │   └── Contact          # Contact entity
│   ├── exception/           # Custom Exceptions & Global Handler
│   │   ├── ResourceNotFoundException
│   │   ├── DuplicateContactException
│   │   ├── DuplicateAddressBookException
│   │   └── GlobalExceptionHandler (catches ConstraintViolationException)
│   ├── mapper/              # Entity-DTO Mappers
│   ├── repository/          # Spring Data JPA Repositories
│   ├── services/            # Business Logic
│   │   ├── AddressBookService
│   │   ├── AddressBookServiceImpl
│   │   ├── ContactService
│   │   └── ContactServiceImpl
│   └── util/                # Utility Classes (PaginationHelper)
│
└── resources/
    ├── db/                  # Database scripts (organized by environment)
    │   ├── schema/
    │   │   ├── h2-schema.sql          # H2 DDL scripts
    │   │   └── postgresql-schema.sql  # PostgreSQL DDL scripts
    │   └── seed/
    │       ├── h2-data.sql            # H2 test data
    │       └── postgresql-data.sql    # PostgreSQL seed data
    ├── application.properties         # Default configuration
    ├── application-dev.properties     # Development profile
    ├── application-test.properties    # Test profile
    ├── application-prod.properties    # Production profile
    ├── templates/                     # Thymeleaf templates (if any)
    └── static/                        # Static resources (CSS, JS, etc.)
```

#### Test Source Tree (Hierarchical Organization)

```
src/test/
├── java/com/addressbook/
│   ├── unit/                       # Unit Tests (Mockito-based)
│   │   ├── addressbook/
│   │   │   └── service/
│   │   │       └── AddressBookServiceTest.java
│   │   └── contact/
│   │       └── service/
│   │           └── ContactServiceTest.java
│   │
│   ├── integration/                # Integration Tests (MockMvc with Spring context)
│   │   └── addressbook/
│   │       └── AddressBookIntegrationTest.java
│   │
│   ├── fixture/                    # Test Data Factory & Utilities
│   │   └── TestDataFactory.java    # Generates test data for unit & integration tests
│   │
│   └── config/                     # Spring Boot Test Configuration
│       └── AddressBookApplicationTests.java  # @SpringBootTest main class
│
└── resources/
    ├── application-test.properties  # Test-specific configuration
    ├── schema-*.sql                 # Test database schema
    └── data-*.sql                   # Test data (if needed)
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for containerized deployment)

### Local Development

```powershell
# Clone the repository
git clone <repository-url>
cd AddressBookApplication

# Run with H2 database (default)
./mvnw spring-boot:run

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build & Package

```powershell
# Build the JAR
./mvnw clean package -DskipTests

# Build with tests
./mvnw clean package
```

### Docker Deployment

```powershell
# Build Docker image
docker build -t addressbook-app .

# Run with Docker Compose (includes PostgreSQL)
docker-compose up -d
```

---

## API Reference

All API responses are wrapped in a standard `ApiResponse<T>` envelope:

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "response": { ... },
  "timestamp": "2025-12-01T10:30:00Z"
}
```

### Base URLs

| Environment | URL |
|-------------|-----|
| Local | `http://localhost:<port>` |
| Swagger UI | `http://localhost:<port>/swagger-ui.html` |
| OpenAPI Spec | `http://localhost:<port>/v3/api-docs` |
| **SwaggerHub** | [https://app.swaggerhub.com/apis/self-fed/address-book-application/1.0.0](https://app.swaggerhub.com/apis/self-fed/address-book-application/1.0.0) |

---

### Address Book APIs (V1)

#### Create Address Book

```http
POST /api/v1/addressbooks
Content-Type: application/json

{
  "name": "Personal Contacts",
  "description": "My personal address book"
}
```

| Field | Type | Constraints |
|-------|------|-------------|
| `name` | String | Required, 2-100 characters, unique |
| `description` | String | Optional, max 200 characters |

**Response:** `201 Created`

```json
{
  "success": true,
  "message": "Address book created successfully",
  "response": {
    "id": 1,
    "name": "Personal Contacts",
    "description": "My personal address book",
    "contactCount": 0,
    "contacts": [],
    "createdAt": "2025-12-01T10:30:00Z",
    "updatedAt": "2025-12-01T10:30:00Z"
  }
}
```

---

#### Get All Address Books (Paginated)

```http
GET /api/v1/addressbooks?page=0&size=20&sortBy=id&sortDir=asc
```

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | 0 | Page number (0-based) |
| `size` | int | 20 | Page size (max 100) |
| `sortBy` | String | "id" | Sort field |
| `sortDir` | String | "asc" | Sort direction (asc/desc) |

**Response:** `200 OK` with `PagedResponse<AddressBookResponse>`

---

#### Get Address Book by ID

```http
GET /api/v1/addressbooks/{id}
```

**Response:** `200 OK` or `404 Not Found`

---

#### Get Address Book by Name

```http
GET /api/v1/addressbooks/name/{name}
```

**Response:** `200 OK` or `404 Not Found`

---

#### Search Address Books by Partial Name

```http
GET /api/v1/addressbooks/search?name=personal&page=0&size=20&sortBy=name&sortDir=asc
```

| Parameter | Type | Description |
|-----------|------|-------------|
| `name` | String | Partial name to search (case-insensitive) |

**Response:** `200 OK` with `PagedResponse<AddressBookResponse>`

---

#### Update Address Book

```http
PUT /api/v1/addressbooks/{id}
Content-Type: application/json

{
  "name": "Updated Name",
  "description": "Updated description"
}
```

**Response:** `200 OK` or `404 Not Found`

---

#### Delete Address Book

```http
DELETE /api/v1/addressbooks/{id}
```

**Response:** `200 OK` or `404 Not Found`

---

### Address Book APIs (V2)

#### Create Address Book with Contacts

```http
POST /api/v2/addressbooks
Content-Type: application/json

{
  "name": "Work Contacts",
  "description": "Office colleagues",
  "contacts": [
    {
      "name": "John Doe",
      "phoneNumber": "+61412345678"
    },
    {
      "name": "Jane Smith",
      "phoneNumber": "+61498765432"
    }
  ]
}
```

**Response:** `201 Created` with address book and embedded contacts

---

### Contact APIs

All contact endpoints are scoped under an address book:

```
/api/v1/addressbooks/{addressBookId}/contacts
```

---

#### Add Contact

```http
POST /api/v1/addressbooks/{addressBookId}/contacts
Content-Type: application/json

{
  "name": "John Doe",
  "phoneNumber": "+61412345678"
}
```

| Field | Type | Constraints |
|-------|------|-------------|
| `name` | String | Required, not blank |
| `phoneNumber` | String | Required, not blank, unique per address book |

**Response:** `201 Created` or `409 Conflict` (duplicate phone number)

---

#### Get All Contacts (Paginated)

```http
GET /api/v1/addressbooks/{addressBookId}/contacts?page=0&size=20&sortBy=id&sortDir=asc
```

**Response:** `200 OK` with `PagedResponse<ContactResponse>`

---

#### Get Contact by ID

```http
GET /api/v1/addressbooks/{addressBookId}/contacts/{contactId}
```

**Response:** `200 OK` or `404 Not Found`

---

#### Update Contact

```http
PUT /api/v1/addressbooks/{addressBookId}/contacts/{contactId}
Content-Type: application/json

{
  "name": "Updated Name",
  "phoneNumber": "+61499999999"
}
```

**Response:** `200 OK`, `404 Not Found`, or `409 Conflict`

---

#### Delete Contact

```http
DELETE /api/v1/addressbooks/{addressBookId}/contacts/{contactId}
```

**Response:** `200 OK` or `404 Not Found`

---

#### Delete All Contacts

```http
DELETE /api/v1/addressbooks/{addressBookId}/contacts
```

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "All contacts deleted successfully",
  "response": {
    "deletedCount": 5
  }
}
```

---

#### Bulk Delete Contacts

```http
DELETE /api/v1/addressbooks/{addressBookId}/contacts/bulk?ids=1,2,3
```

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Contacts deleted successfully",
  "response": {
    "requestedCount": 3,
    "deletedCount": 2
  }
}
```

---

#### Get Unique Contacts (Across All Address Books)

```http
GET /api/v1/addressbooks/{addressBookId}/contacts/unique?page=0&size=20
```

Returns deduplicated contacts by phone number across all address books.

**Response:** `200 OK` with `PagedResponse<ContactResponse>`

---

#### Get Contact Count

```http
GET /api/v1/addressbooks/{addressBookId}/contacts/count
```

**Response:** `200 OK`

```json
{
  "success": true,
  "message": "Contact count retrieved successfully",
  "response": 15
}
```

---

## Data Models

### AddressBook Entity

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key, auto-generated |
| `name` | String | Unique address book name |
| `description` | String | Optional description |
| `contacts` | Set<Contact> | One-to-Many relationship |
| `createdAt` | LocalDateTime | Creation timestamp |
| `updatedAt` | LocalDateTime | Last update timestamp |

### Contact Entity

| Field | Type | Description |
|-------|------|-------------|
| `id` | Long | Primary key, auto-generated |
| `name` | String | Contact name |
| `phoneNumber` | String | Phone number (unique per address book) |
| `addressBook` | AddressBook | Many-to-One relationship |
| `createdAt` | LocalDateTime | Creation timestamp |
| `updatedAt` | LocalDateTime | Last update timestamp |

### Unique Constraint

A unique constraint exists on `(phone_number, address_book_id)` to prevent duplicate contacts within the same address book while allowing the same phone number to exist in different address books.

---

## Test Coverage

The application includes comprehensive test coverage with **3 test suites** and **44+ integration tests**.

### Test Suites

| Suite | Type | Test Count | Description |
|-------|------|------------|-------------|
| `AddressBookServiceTest` | Unit | 17 | Service layer unit tests with Mockito |
| `ContactServiceTest` | Unit | 18 | Contact service unit tests with Mockito |
| `AddressBookIntegrationTest` | Integration | 44 | End-to-end integration tests with MockMvc |

---

### Unit Tests: AddressBookServiceTest

#### Create Address Book Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 1 | `testCreateAddressBook` | Should create address book successfully |
| 2 | `testCreateAddressBookWithDuplicateName` | Should throw DuplicateAddressBookException when name exists |
| 3 | `testCreateAddressBookWithContacts` | Should create address book with contacts (V2) |
| 4 | `testCreateAddressBookWithEmptyContacts` | Should not call contactCreationService when contacts list is empty |

#### Get Address Book Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 5 | `testGetAddressBookById` | Should get address book by id |
| 6 | `testGetAddressBookByIdNotFound` | Should throw exception when address book not found |
| 7 | `testGetAddressBookByName` | Should get address book by name |
| 8 | `testGetAddressBookByNameNotFound` | Should throw exception when address book not found by name |
| 9 | `testSearchByName` | Should search address books by partial name |
| 10 | `testSearchByNameNoResults` | Should return empty result when no address books match search |

#### Get All Address Books Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 11 | `testGetAllAddressBooks` | Should get all address books (non-paginated) |
| 12 | `testGetAllAddressBooksEmpty` | Should return empty list when no address books exist |
| 13 | `testGetAllAddressBooksPaginated` | Should get all address books paginated |
| 14 | `testGetAllAddressBooksSanitizesPageable` | Should sanitize pageable with large page size |

#### Update Address Book Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 15 | `testUpdateAddressBook` | Should update address book successfully |
| 16 | `testUpdateAddressBookNotFound` | Should throw exception when updating non-existent address book |

#### Delete Address Book Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 17 | `testDeleteAddressBook` | Should delete address book successfully |
| 18 | `testDeleteAddressBookNotFound` | Should throw exception when deleting non-existent address book |

---

### Unit Tests: ContactServiceTest

#### Add Contact Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 1 | `testAddContact` | Should add contact successfully |
| 2 | `testAddDuplicateContactByPhoneNumber` | Should throw DuplicateContactException when phone number exists |
| 3 | `testAddDuplicateContactExactPhoneNumber` | Should throw DuplicateContactException with exact phone number message |
| 4 | `testAddContactToNonExistentAddressBook` | Should throw ResourceNotFoundException for non-existent address book |
| 5 | `testAddSamePhoneNumberDifferentAddressBook` | Should allow same phone number in different address books |

#### Get Contact Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 6 | `testGetContactById` | Should get contact by id |
| 7 | `testGetContactByIdNotFound` | Should throw ResourceNotFoundException when contact not found |
| 8 | `testGetContactFromWrongAddressBook` | Should throw ResourceNotFoundException for wrong address book |

#### Get All Contacts Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 9 | `testGetAllContacts` | Should get all contacts in address book (non-paginated) |
| 10 | `testGetAllContactsPaged` | Should get all contacts paginated |
| 11 | `testGetAllContactsFromNonExistentAddressBook` | Should throw ResourceNotFoundException |
| 12 | `testGetAllContactsPagedFromNonExistentAddressBook` | Should throw ResourceNotFoundException for paginated request |
| 13 | `testGetAllContactsEmpty` | Should return empty list when address book has no contacts |

#### Remove Contact Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 14 | `testRemoveContact` | Should remove contact successfully |
| 15 | `testRemoveContactNotFound` | Should throw ResourceNotFoundException when removing non-existent contact |
| 16 | `testRemoveContactFromWrongAddressBook` | Should throw ResourceNotFoundException for wrong address book |

#### Unique Contacts Tests
| # | Test Case | Description |
|---|-----------|-------------|
| 17 | `testGetUniqueContactsAcrossAllAddressBooks` | Should get unique contacts across all address books |
| 18 | `testGetUniqueContactsPaged` | Should get unique contacts paginated |
| 19 | `testGetUniqueContactsEmpty` | Should return empty list when no contacts exist |
| 20 | `testGetContactCount` | Should get contact count for address book |
| 21 | `testGetUniqueContactCount` | Should get unique contact count |

---

### Integration Tests: AddressBookIntegrationTest

#### CRUD Operations (Orders 1-7)
| Order | Test Case | Description |
|-------|-----------|-------------|
| 1 | `testCreateAddressBook` | Should create a new address book |
| 2 | `testGetAllAddressBooks` | Should get all address books |
| 3 | `testAddContact` | Should add contact to address book |
| 4 | `testPreventDuplicateContacts` | Should prevent duplicate contacts in same address book |
| 5 | `testPrintAllContactsInAddressBook` | Should print all contacts in address book |
| 6 | `testRemoveContact` | Should remove contact from address book |
| 7 | `testGetUniqueContactsAcrossMultipleAddressBooks` | Should get unique contacts across multiple address books |

#### Address Book Validation (Orders 8-17)
| Order | Test Case | Description |
|-------|-----------|-------------|
| 8 | `testCreateAddressBookWithBlankName` | Should reject address book creation with blank name |
| 9 | `testCreateAddressBookWithNullName` | Should reject address book creation with null name |
| 10 | `testCreateAddressBookWithNameTooShort` | Should reject name shorter than 2 characters |
| 11 | `testCreateAddressBookWithNameTooLong` | Should reject name longer than 100 characters |
| 12 | `testCreateAddressBookWithDescriptionTooLong` | Should reject description longer than 200 characters |
| 13 | `testCreateAddressBookWithNameAtMinLength` | Should accept name at minimum length (2 characters) |
| 14 | `testCreateAddressBookWithNameAtMaxLength` | Should accept name at maximum length (100 characters) |
| 15 | `testCreateAddressBookWithDescriptionAtMaxLength` | Should accept description at maximum length (200 characters) |
| 16 | `testCreateAddressBookWithNullDescription` | Should accept address book creation with null description |
| 17 | `testCreateAddressBookWithWhitespaceOnlyName` | Should reject whitespace-only name |

#### Contact Validation (Orders 18-25)
| Order | Test Case | Description |
|-------|-----------|-------------|
| 18 | `testAddContactWithBlankName` | Should reject contact creation with blank name |
| 19 | `testAddContactWithNullName` | Should reject contact creation with null name |
| 20 | `testAddContactWithWhitespaceOnlyName` | Should reject whitespace-only name |
| 21 | `testAddContactWithBlankPhoneNumber` | Should reject blank phone number |
| 22 | `testAddContactWithNullPhoneNumber` | Should reject null phone number |
| 23 | `testAddContactWithWhitespaceOnlyPhoneNumber` | Should reject whitespace-only phone number |
| 24 | `testAddContactWithBothFieldsBlank` | Should reject both fields blank |
| 25 | `testAddContactWithBothFieldsNull` | Should reject both fields null |

#### Multiple Address Books & Duplicates (Orders 26-30)
| Order | Test Case | Description |
|-------|-----------|-------------|
| 26 | `testCreateMultipleAddressBooks` | Should create multiple address books successfully |
| 27 | `testPreventDuplicateAddressBookName` | Should prevent creating address book with duplicate name |
| 28 | `testAddMultipleContactsToAddressBook` | Should add multiple contacts to an address book successfully |
| 29 | `testPreventDuplicateContactPhoneNumberInSameAddressBook` | Should prevent duplicate phone number in same address book |
| 30 | `testAllowSameContactPhoneNumberInDifferentAddressBooks` | Should allow same phone number in different address books |

#### Update Operations (Orders 31-36)
| Order | Test Case | Description |
|-------|-----------|-------------|
| 31 | `testUpdateContactSuccessfully` | Should update contact successfully |
| 32 | `testUpdateContactNameOnly` | Should update contact name only keeping same phone number |
| 33 | `testUpdateContactWithDuplicatePhoneNumber` | Should fail to update contact with duplicate phone number |
| 34 | `testUpdateNonExistentContact` | Should fail to update non-existent contact |
| 35 | `testUpdateContactInNonExistentAddressBook` | Should fail to update contact in non-existent address book |
| 36 | `testUpdateContactWithInvalidData` | Should fail to update contact with invalid data |

#### Bulk Delete Operations (Orders 37-40)
| Order | Test Case | Description |
|-------|-----------|-------------|
| 37 | `testBulkDeleteContacts` | Should delete multiple contacts by IDs |
| 38 | `testDeleteAllContacts` | Should delete all contacts in address book |
| 39 | `testBulkDeleteWithNonExistentIds` | Should handle bulk delete with non-existent IDs gracefully |
| 40 | `testDeleteAllContactsOnEmptyAddressBook` | Should handle delete all on empty address book |

#### Search Operations (Orders 41-44)
| Order | Test Case | Description |
|-------|-----------|-------------|
| 41 | `testSearchAddressBooksByPartialName` | Should search address books by partial name (case-insensitive) |
| 42 | `testSearchAddressBooksWithPagination` | Should search address books with pagination |
| 43 | `testSearchAddressBooksNoResults` | Should return empty result when search finds no matches |
| 44 | `testSearchAddressBooksCaseInsensitive` | Should search address books case-insensitively |

---

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

---

## Deployment

### Docker

```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/addressbook-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose

```powershell
# Start application with PostgreSQL
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all containers
docker-compose down
```

### Kubernetes

Kubernetes manifests are provided in the `k8s/` directory:

```powershell
#Build the docker Image using the DockerFile Script
docker build -f DockerFile -t addressbook-app:latest .
# Apply all manifests
kubectl apply -f k8s/

# Check deployment status
kubectl get pods
kubectl get services
```

| Manifest | Description |
|----------|-------------|
| `configmap.yaml` | Application configuration |
| `postgres-pvc.yaml` | Persistent volume claim for PostgreSQL |
| `postgres-deployment.yaml` | PostgreSQL deployment and service |
| `deployment.yaml` | Application deployment |
| `service.yaml` | Application service (LoadBalancer) |

---

## Configuration

### Application Profiles

| Profile | Database | Use Case |
|---------|----------|----------|
| `default` | H2 (in-memory) | Quick local development |
| `dev` | H2 (in-memory) | Development with data seeding |
| `test` | H2 (in-memory) | Automated testing |
| `prod` | PostgreSQL | Production deployment |

### Environment Variables (Production)

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `prod` |
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL | - |
| `SPRING_DATASOURCE_USERNAME` | Database username | - |
| `SPRING_DATASOURCE_PASSWORD` | Database password | - |

### Actuator Endpoints

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | Health check |
| `/actuator/info` | Application info |
| `/actuator/metrics` | Application metrics |

---

## Error Handling

All errors are returned in a consistent format:

```json
{
  "success": false,
  "message": "Address book not found with id: 999",
  "response": null,
  "timestamp": "2025-12-01T10:30:00Z"
}
```

### Exception Handlers (GlobalExceptionHandler)

The application uses a centralized exception handling strategy via `@RestControllerAdvice` with multiple `@ExceptionHandler` methods:

| Exception | HTTP Status | Response Type | Description |
|-----------|------------|---------------|-------------|
| `ResourceNotFoundException` | 404 NOT_FOUND | Error | Resource not found (address book or contact) |
| `DuplicateContactException` | 409 CONFLICT | Error | Duplicate phone number in same address book |
| `DuplicateAddressBookException` | 409 CONFLICT | Error | Duplicate address book name |
| `MethodArgumentNotValidException` | 400 BAD_REQUEST | Validation Error | Request parameter validation failure (e.g., `@RequestBody` fields) |
| `ConstraintViolationException` | 400 BAD_REQUEST | Validation Error | Entity-level validation failure (e.g., during JPA persist) |
| `Exception` (Generic) | 500 INTERNAL_SERVER_ERROR | Error | Unexpected runtime errors |

#### Validation Error Response Format

When validation fails, the response includes field-level error details:

```json
{
  "success": false,
  "message": "Entity validation failed",
  "response": {
    "errors": {
      "phoneNumber": "Phone number must not be blank",
      "name": "Name must not be blank"
    }
  },
  "timestamp": "2025-12-01T10:30:00Z"
}
```

#### Exception Handler Details

**Request Parameter Validation** (`MethodArgumentNotValidException`):
- Catches validation failures on `@RequestBody` and `@RequestParam` annotations
- Returns HTTP 400 with field-level validation errors
- Example: Missing required field in JSON request body

**Entity Validation** (`ConstraintViolationException`):
- Catches validation failures during JPA entity persistence operations
- Returns HTTP 400 with constraint violation details
- Example: Contact with blank phone number being persisted to database
- Leverages Jakarta Validation (Bean Validation 3.0) constraints on entity fields

