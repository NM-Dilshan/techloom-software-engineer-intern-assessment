# Techloom Software Engineer Intern Assessment

GitHub Repository: [Add URL]
Task 01 Frontend: [Add URL]
Task 01 Backend: [Add URL]
Task 02 Frontend: [Add URL]
Task 02 Backend: [Add URL]

## Project Overview

This repository contains two independently runnable applications for the assessment. Task 01 is a POS order and inventory system. Task 02 will be a customer-facing e-commerce checkout system.

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

## Architecture

The backend will use Spring Web, Spring Data JPA, Spring Validation, MySQL, transactional services, and database locking for critical inventory operations. The frontend uses React, Vite, Axios, React Router, and Material UI.

Detailed API, reservation, concurrency, payment, testing, and deployment documentation will be completed as the two tasks are implemented.
