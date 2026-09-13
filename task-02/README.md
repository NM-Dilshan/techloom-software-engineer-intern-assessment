## Task 02: Loom Market Checkout

Task 02 is a Spring Boot 3.5.5 + React/Vite e-commerce checkout slice. It uses H2 by default for local development and accepts PostgreSQL settings through environment variables.

## Run

Backend (Java 21, port 8082):

```powershell
cd task-02/backend
mvn spring-boot:run
```

Frontend (port 5174):

```powershell
cd task-02/frontend
npm install
npm run dev
```

The frontend uses `http://localhost:8082/api` by default. Set `VITE_API_BASE_URL` to override it. The demo UI sends `X-User-Id: demo-user`; production authentication can replace this header at the API boundary.

## Implemented behavior

- Seeded products with search (`GET /api/products?q=&category=`) and detail (`GET /api/products/{id}`).
- Cart reads and mutations through `GET /api/cart`, `POST /api/cart/items`, and `DELETE /api/cart/items/{productId}`.
- Transactional checkout (`POST /api/checkout`) uses pessimistic product locks, decrements stock, creates an order, and creates five-minute stock reservations.
- Mock payment (`POST /api/orders/{id}/payment`) supports `SUCCESS`, `FAILURE`, and `TIMEOUT`. Repeating a payment for a finalized order is idempotent and returns the existing result.
- A scheduled job expires pending reservations every 30 seconds and restores stock. Failed, timed-out, and cancelled orders release stock; paid orders can be cancelled and marked `REFUNDED`.
- Order history/detail: `GET /api/orders`, `GET /api/orders/{id}`, and cancellation via `POST /api/orders/{id}/cancel`.
- DTOs, validation, CORS, and a JSON global error response are included.

## Configuration

Defaults are in `backend/src/main/resources/application.yml`:

```text
DB_URL=jdbc:h2:mem:task02;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false
DB_USERNAME=sa
DB_PASSWORD=
DB_DRIVER_CLASS=org.h2.Driver
DDL_AUTO=create-drop
SERVER_PORT=8082
```

For PostgreSQL, set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and `DB_DRIVER_CLASS=org.postgresql.Driver`.

## Validation

```powershell
cd task-02/backend
mvn test
mvn -DskipTests compile

cd ../frontend
npm install
npm run build
```

Task 02 does not include a Maven wrapper, so the installed Maven executable is required for backend commands.
