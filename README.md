# Techloom Software Engineer Intern Assessment

GitHub Repository: TODO - add public repository URL before submission
Task 01 Frontend: TODO - add Vercel URL
Task 01 Backend: TODO - add Render URL
Task 02 Frontend: TODO - add Vercel URL
Task 02 Backend: TODO - add Render URL

## Architecture

This monorepo contains two independently deployable applications:

```text
Task 01 React/Vite frontend -> Vercel -> Task 01 Spring Boot backend -> Supabase PostgreSQL
Task 02 React/Vite frontend -> Vercel -> Task 02 Spring Boot backend -> Supabase PostgreSQL
```

The backends use Java 21, Spring Boot 3.5.5, Spring Web, Spring Data JPA, Spring Validation, and PostgreSQL/H2 drivers. The frontends use React, Vite, Axios, React Router, and Material UI. Supabase is used only as PostgreSQL through JDBC/JPA; the React applications never connect directly to Supabase.

## Local Setup

Prerequisites: Java 21, Maven 3.9+, Node.js 20+, and a Supabase PostgreSQL project for persistent data.

Create local environment files from the committed templates. Never commit the resulting `.env` files:

```powershell
Copy-Item task-01/backend/.env.example task-01/backend/.env
Copy-Item task-02/backend/.env.example task-02/backend/.env
Copy-Item task-01/frontend/.env.example task-01/frontend/.env
Copy-Item task-02/frontend/.env.example task-02/frontend/.env
```

Run Task 01:

```powershell
cd task-01/backend
mvn spring-boot:run

cd ../frontend
npm install
npm run dev
```

Task 01 uses backend port `8080`, frontend port `5173`, and health path `/api/health`.

Run Task 02:

```powershell
cd task-02/backend
mvn spring-boot:run

cd ../frontend
npm install
npm run dev
```

Task 02 uses backend port `8082`, frontend port `5174`, and health path `/api/health`.

## Environment Variables

Backend variables for both tasks:

```text
DB_URL=jdbc:postgresql://<supabase-host>:5432/postgres
DB_USERNAME=<supabase-database-user>
DB_PASSWORD=<supabase-database-password>
DB_DRIVER_CLASS=org.postgresql.Driver
DDL_AUTO=update
PORT=<Render-injected-port>
FRONTEND_URL=https://<matching-vercel-project>.vercel.app
```

`PORT` is supplied by Render automatically. The applications fall back to `8080` for Task 01 and `8082` for Task 02 locally. `FRONTEND_URL` controls CORS and must contain the deployed Vercel origin without a trailing path. Local defaults are retained in `application.yml`.

Frontend variables:

```text
# Task 01 local
VITE_API_BASE_URL=http://localhost:8080/api

# Task 02 local
VITE_API_BASE_URL=http://localhost:8082/api
```

In Vercel, set each value to the matching Render backend URL ending in `/api`.

## Supabase Setup

1. Create or select a Supabase project.
2. Copy the PostgreSQL JDBC host, username, and password from Supabase Connect.
3. Put them only in local ignored `.env` files or Render secret environment variables.
4. Use the Supabase pooler connection if the hosting provider has connection limits.
5. Set `DDL_AUTO=update` in Render so the JPA schema is preserved between restarts.

Both `pom.xml` files already include `org.postgresql:postgresql` with runtime scope.

## A. Task 01 Backend - Render

```text
Name: techloom-task01-backend
Language: Docker
Branch: main
Region: Virginia (US East), or the same region used for Task 02
Root Directory: task-01/backend
Dockerfile Path: Dockerfile
Build Command: leave empty for Docker runtime
Start Command: leave empty for Docker runtime
Health Check Path: /api/health
```

Set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER_CLASS=org.postgresql.Driver`, `DDL_AUTO=update`, and `FRONTEND_URL` in Render Environment Variables. Render supplies `PORT`; the Docker image defaults to `8080` for local use.

Deploy steps: create a Render Web Service, connect the GitHub repository, select Docker, set the root directory, add the environment variables, deploy, and verify `https://<render-task01-domain>/api/health`.

## B. Task 01 Frontend - Vercel

```text
Project Name: techloom-task01-frontend
Framework Preset: Vite
Root Directory: task-01/frontend
Build Command: npm run build
Output Directory: dist
Install Command: npm install
Environment Variable: VITE_API_BASE_URL=https://<task01-render-domain>/api
```

Import the GitHub repository into Vercel, set the root directory and variable, deploy, then test `/products` and `/pos`. `vercel.json` rewrites all client routes to `index.html` for refresh support.

## C. Task 02 Backend - Render

```text
Name: techloom-task02-backend
Language: Docker
Branch: main
Region: Virginia (US East), or the same region used for Task 01
Root Directory: task-02/backend
Dockerfile Path: Dockerfile
Build Command: leave empty for Docker runtime
Start Command: leave empty for Docker runtime
Health Check Path: /api/health
```

Set the same database variables, `DDL_AUTO=update`, and `FRONTEND_URL=https://<task02-vercel-domain>.vercel.app`. Verify `https://<render-task02-domain>/api/health` after deployment.

## D. Task 02 Frontend - Vercel

```text
Project Name: techloom-task02-frontend
Framework Preset: Vite
Root Directory: task-02/frontend
Build Command: npm run build
Output Directory: dist
Install Command: npm install
Environment Variable: VITE_API_BASE_URL=https://<task02-render-domain>/api
```

Deploy from the GitHub repository with the task-02 root directory. `vercel.json` supports client-side route refreshes.

## Post-Deployment Configuration

1. Deploy each Render backend and copy its public HTTPS URL.
2. Set the matching `VITE_API_BASE_URL` in each Vercel project, including `/api`.
3. Deploy each Vercel frontend and copy its public HTTPS origin.
4. Set the matching `FRONTEND_URL` in each Render backend.
5. Redeploy the Render services so the CORS setting is loaded.
6. Check both health endpoints, product APIs, and browser network requests.

## Verification Checklist

Backend: startup succeeds, Supabase connection succeeds, `/api/health` returns `status=ok`, product APIs work, CORS permits only the configured frontend, and logs contain no secrets.

Task 01: product CRUD, cart checkout, locked stock reservation, expiry, payment success/failure/timeout, duplicate payment rejection, cancellation, refund status, order history, and concurrent limited-stock checkout.

Task 02: search, category filter, price filter, availability filter, cart, checkout reservation, payment outcomes, idempotency, ownership checks, cancellation/refund, and order history.

Frontend: Vercel site loads, API requests reach the correct Render URL, nested route refreshes do not return 404, and no production build contains a hard-coded backend URL.

## Git Safety and Deployment Changes

The root `.gitignore` excludes `.env`, `.env.*`, `target/`, `node_modules/`, and `dist/` while allowing `.env.example`. The local backend `.env` files are not tracked. Never stage them.

```powershell
git status
git add README.md .gitignore task-01 task-02
git diff --cached --check
git commit -m "Prepare applications for production deployment"
git push origin main
```

If a secret file was ever tracked, remove it from the index without deleting the local file, rotate the exposed credential, and push the removal:

```powershell
git rm --cached task-01/backend/.env task-02/backend/.env
git commit -m "Remove local environment files"
git push origin main
```

The repository currently contains no tracked `.env` files according to `git ls-files`; rotate any password that has been exposed outside the local machine before deployment.
