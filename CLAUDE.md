# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Doppelkopf (German card game) cash register app — tracks per-player game results (jan, tim, ole, louisa, hannes) across evenings and semesters, with earnings, expenses, and cash/semester reports. Five fixed players defined in the `Player` enum.

## Commands

### Backend

```bash
# Start local MongoDB (required before running backend locally)
docker-compose up

# Run backend (local profile, uses MongoDB on localhost:27017)
./mvnw spring-boot:run -pl backend/domain -am

# Run all backend tests
./mvnw test -pl backend/domain

# Run a single test class
./mvnw test -pl backend/domain -Dtest=EarningServiceTest

# Full build (compiles frontend, copies to backend static resources)
./mvnw package
```

### Frontend

```bash
cd frontend

npm start          # dev server on :3000, proxies API to :8080
npm test           # run tests (jest + testing-library)
npm run lint       # eslint
npm run format     # prettier
```

## Architecture

### Backend (Spring Boot, Java 17, Maven multi-module)

```
backend/
  plugins/config/   — Spring config beans: WebConfig, AppConfig, CustomRequestLogFilter
  domain/           — Application code and entry point (Application.java)
```

Domain packages follow a flat feature structure: `earning/`, `evening/`, `expense/`, `semester/`, `report/`. Each feature contains an entity, repository (spring-data-mongodb), service, controller (HATEOAS), model assembler, DTO(s), and exception classes.

**Database:** MongoDB. Local profile connects to `mongodb://localhost:27017/dokokasse` (Docker). Prod profile connects via `MONGODB_URI` env var. Collections are named with `doko-` prefix (e.g., `doko-abende`, `doko-semester`).

**Spring profiles:** `local` (default), `prod`. Active profile set at build time via Maven profile, then interpolated into `application.yml`.

**API docs:** Swagger UI at `/api-ui`, OpenAPI JSON at `/api-docs`.

**Tests:** Extend `BaseTest` (Mockito lifecycle). Tests are pure unit tests using mocks — no embedded MongoDB.

### Database Migrations

Managed by [migrate-mongo](https://github.com/seppevs/migrate-mongo) in the top-level `migrations/` directory (Node 18+, independent of the frontend).

```bash
cd migrations
npm install

npm run migrate:status        # show applied / pending migrations
npm run migrate:create -- <name>  # scaffold a new migration
npm run migrate:up            # apply pending migrations
npm run migrate:down          # roll back the last migration
```

Migration scripts live in `migrations/migrations/`. Applied migrations are tracked in the `doko-changelog` MongoDB collection. For prod, set `MONGODB_URI` before running.

### Frontend (React 17, JSX)

```
frontend/src/
  api/              — Axios calls per domain (eveningsApi, earningsApi, expensesApi, reportsApi)
  components/
    base/           — Shared table/form primitives
    HOC/            — withModal, withSpinner, withToasts
    navigation/     — NavigationBar, NavigationDrawer, NavigationContent
    evenings/       — Evening CRUD + list/table views
    earnings/       — Earning CRUD
    expenses/       — Expense CRUD
    overview/       — Cash overview
    reports/        — Semester report view
  constants/        — navigation.js, player.js, semester.js (semester list lives here)
  services/utils/   — apiUtils, baseUtils, sortUtils
```

**UI libraries:** `@salesforce/design-system-react` (SLDS) as primary component library, `@material-ui/core` v4 for layout utilities and responsive breakpoints.

**State pattern:** Custom hooks (`useEvenings`, `useEarnings`, `useExpenses`, `useCashReport`, `useReport`) own data fetching state and expose `[data, reload, spinner]`. HOCs wrap components to inject modal, spinner, and toast behavior.

**Import aliases:** `babel-plugin-root-import` maps `src/` as root, so `import { eveningsAPI } from 'api'` resolves to `src/api/index.js`.

**Routing:** No React Router — navigation is a single `activeContent` state string in `App`. `MobileContext` provides a boolean for responsive rendering.

**Frontend note:** This codebase uses `.jsx` files throughout. New frontend files should follow the existing JSX pattern rather than the global TypeScript default.
