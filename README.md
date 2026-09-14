# Techloom Software Engineer Intern Assessment

GitHub Repository: TODO - add public repository URL before submission
Task 01 Live URL: TODO - deploy frontend/backend
Task 02 Live URL: TODO - deploy frontend/backend

## Project Overview

This repository contains two independently runnable applications for the assessment. Task 01 is a POS order and inventory system. Task 02 is a customer-facing e-commerce checkout system.

Task 01 includes product CRUD, locked checkout, five-minute reservations, scheduled expiry, mock payment outcomes, duplicate payment protection, order history, cancellation, and refund status. Task 02 includes product discovery, category/price/availability filters, cart, locked checkout reservations, payment idempotency, ownership checks, cancellation/refund status, and order history.

## Step 1: Task 01 Setup

### Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 20+
- PostgreSQL 14+ or a Supabase PostgreSQL project

### PostgreSQL Database

For local development, create the database:

```sql
CREATE DATABASE techloom_task01;
```

For Supabase, copy `task-01/backend/.env.example` to `task-01/backend/.env` and set the project connection values. The backend loads this ignored `.env` file automatically. Never commit `.env` or put database credentials in the frontend environment file.

### Run the backend

```text
cd task-01/backend
mvn spring-boot:run
```

Health check: `http://localhost:8080/api/health`

### Run the frontend

In a second terminal:

```text
cd task-01/frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

## Testing and deployment

Run `mvn test` in each backend and `npm run build` in each frontend. Exercise concurrent limited-stock checkouts, reservation expiry, SUCCESS/FAILURE/TIMEOUT payments, repeated and conflicting idempotency keys, cross-user order access, cancellation/refund, and filters.

Local development defaults to H2. Production must set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER_CLASS`, `DDL_AUTO=update`, `CORS_ALLOWED_ORIGINS`, and each frontend's `VITE_API_BASE_URL`. Deploy both tasks to publicly accessible hosting and replace the TODO links above before submission; no provider credentials or live URLs can be created from this workspace.

## Architecture

The backends use Spring Web, Spring Data JPA, Spring Validation, transactional services, and database locking for critical inventory operations. The frontends use React, Vite, Axios, React Router, and Material UI.

Payment is simulated and accepts `SUCCESS`, `FAILURE`, or `TIMEOUT`. Repeating a finalized payment returns its existing result; a different key for the same pending order is rejected.
