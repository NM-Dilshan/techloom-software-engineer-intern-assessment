# STEP 2: Product Management — Completion Guide

## What Was Built

A complete product CRUD system for Task 01 with:
- **Backend**: Spring Boot REST API with product entity, service, and controller layers
- **Frontend**: React component for listing, creating, editing, and deleting products with live stock visibility
- **Database**: PostgreSQL entity with optimistic locking (version field) and stock tracking
- **Testing**: Unit tests for ProductService

## Backend Files Created

| File Path | Purpose |
|-----------|---------|
| `task-01/backend/src/main/java/com/techloom/task01/entity/Product.java` | JPA entity with availableStock, reservedStock, and version (for optimistic locking) |
| `task-01/backend/src/main/java/com/techloom/task01/dto/ProductDTO.java` | Data transfer object for API requests/responses |
| `task-01/backend/src/main/java/com/techloom/task01/repository/ProductRepository.java` | JPA repository for database access |
| `task-01/backend/src/main/java/com/techloom/task01/service/ProductService.java` | Service layer with CRUD business logic |
| `task-01/backend/src/main/java/com/techloom/task01/controller/ProductController.java` | REST controller with 5 endpoints (POST, GET all, GET by id, PUT, DELETE) |
| `task-01/backend/src/main/java/com/techloom/task01/exception/ProductNotFoundException.java` | Custom exception |
| `task-01/backend/src/main/java/com/techloom/task01/exception/GlobalExceptionHandler.java` | Global exception handler with validation error support |
| `task-01/backend/src/test/java/com/techloom/task01/service/ProductServiceTest.java` | Unit tests for ProductService |

## Frontend Files Created

| File Path | Purpose |
|-----------|---------|
| `task-01/frontend/src/api/productAPI.js` | Axios wrapper for product API calls |
| `task-01/frontend/src/pages/ProductList.jsx` | React component: list, create, edit, delete products |
| `task-01/frontend/src/App.jsx` | Updated with routing to /products |

## Configuration Changes

**Backend Dependencies** (`task-01/backend/pom.xml`):
- Added `org.postgresql:postgresql` (JDBC driver)
- Added `org.projectlombok:lombok` (annotations)

**Frontend Dependencies** (`task-01/frontend/package.json`):
- Added `@mui/icons-material` (Edit, Delete icons)

**Spring Configuration** (`task-01/backend/src/main/resources/application.yml`):
- Added `.env` file import: `spring.config.import: optional:file:.env[.properties]`
- Changed datasource to PostgreSQL
- Added logging configuration

**Backend Environment** (`task-01/backend/.env` and `.env.example`):
- `.env` contains actual Supabase credentials (ignored by Git)
- `.env.example` is the shareable template

---

## REST API Endpoints

### Implemented

```
POST   /api/products                 Create a new product
GET    /api/products                 List all products
GET    /api/products/{id}            Get a product by ID
PUT    /api/products/{id}            Update a product
DELETE /api/products/{id}            Delete a product
GET    /api/health                   Health check
```

### Request/Response Examples

**Create Product (POST /api/products)**
```json
Request:
{
  "name": "Laptop",
  "price": 999.99,
  "availableStock": 50
}

Response (201 Created):
{
  "id": 1,
  "name": "Laptop",
  "price": 999.99,
  "availableStock": 50,
  "reservedStock": 0,
  "totalStock": 50,
  "version": 0,
  "createdAt": "2026-09-13T10:00:00",
  "updatedAt": "2026-09-13T10:00:00"
}
```

**Get All Products (GET /api/products)**
```json
Response (200 OK):
[
  {
    "id": 1,
    "name": "Laptop",
    "price": 999.99,
    "availableStock": 50,
    "reservedStock": 0,
    "totalStock": 50,
    ...
  }
]
```

**Error Response (404 Not Found)**
```json
{
  "timestamp": "2026-09-13T10:05:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999"
}
```

---

## Stock Tracking Design

Each product tracks two quantities:

- **availableStock**: Quantity available for immediate purchase
- **reservedStock**: Quantity reserved by pending orders (held for checkout)
- **totalStock** = availableStock + reservedStock

This design allows:
- Accurate visibility of what's available vs. what's reserved
- Safe concurrent checkout (later steps)
- Automatic restoration when reservations expire

---

## Optimistic Locking Strategy

The Product entity includes a `@Version` field (database version column). This enables optimistic locking:

- When a product is loaded, its version is read.
- If two requests try to modify the same product simultaneously, one will succeed and increment the version.
- The other will receive a `StaleObjectStateException` (handled as HTTP 409 Conflict in real scenarios).

This approach prevents lost updates during concurrent product edits.

---

## Validation

**ProductDTO validation** (via @Valid in controller):
- `@NotBlank name`: Product name is required
- `@Positive price`: Price must be > 0
- `@Positive availableStock`: Available stock must be >= 0

**Global exception handler** catches:
- Validation errors → HTTP 400 with field-level messages
- ProductNotFoundException → HTTP 404
- All other exceptions → HTTP 500

---

## How to Test Locally

### 1. Set up the database

```bash
# Create a local PostgreSQL database
psql -U postgres
CREATE DATABASE techloom_task01;
```

Or use the Supabase credentials in `task-01/backend/.env`.

### 2. Start the backend

```bash
cd task-01/backend
mvn spring-boot:run
```

Verify health: `http://localhost:8080/api/health`

### 3. Start the frontend

In another terminal:
```bash
cd task-01/frontend
npm install
npm run dev
```

Open: `http://localhost:5173`

### 4. Test the UI

1. Click "Products" in the top nav
2. Click "Add Product" → fill in name, price, stock → Save
3. View the product in the table (shows available + reserved stock)
4. Click Edit → modify the product → Save
5. Click Delete → confirm removal

### 5. Run Backend Tests

```bash
cd task-01/backend
mvn test
```

Expected: All ProductServiceTest tests should pass (7 tests).

---

## Key Points for Next Steps

- **Reservations** (STEP 3): Checkout will modify `reservedStock` and track expiry time
- **Concurrency** (STEP 4): Pessimistic locking (@Lock) will prevent overselling during simultaneous checkouts
- **Transactions**: @Transactional ensures stock updates are atomic

---

## Files Modified

- `pom.xml`: Added PostgreSQL and Lombok
- `package.json`: Added @mui/icons-material
- `application.yml`: PostgreSQL config, .env import, logging
- `.env` and `.env.example`: Created backend secrets management
- `App.jsx`: Added /products route

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Maven not found | Add Maven to PATH or use `mvn.cmd` on Windows |
| Database connection error | Check `.env` file credentials and PostgreSQL/Supabase availability |
| Port 8080 already in use | Change `SERVER_PORT` in `.env` or kill existing process |
| CORS errors | Ensure `CORS_ALLOWED_ORIGINS` in `.env` matches frontend URL |
| npm install hangs | Use `npm install --verbose` or delete `node_modules` and `package-lock.json` |

---

**Next Step**: STEP 3 — Cart Management (add cart entity, cart items, add-to-cart API, cart view UI)

Confirm when ready.
