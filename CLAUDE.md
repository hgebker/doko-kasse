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

### Frontend (React — legacy)

```bash
cd frontend

npm start          # dev server on :3000, proxies API to :8080
npm test           # run tests (jest + testing-library)
npm run lint       # eslint
npm run format     # prettier
```

### Frontend (SvelteKit — active migration)

```bash
cd frontend-svelte

npm run dev        # dev server on :5173, proxies /api to :8080
npm run build      # static build → build/
npm run check      # svelte-check type checking
npm run lint       # prettier + eslint check
npm run format     # prettier + eslint fix
npm run test       # vitest (one-shot)
npm run test:unit  # vitest (watch mode)
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

### Frontend SvelteKit (Svelte 5, TypeScript)

```
frontend-svelte/src/
  lib/
    api/            — fetch-based API clients (evenings, earnings, expenses, reports)
    components/
      layout/       — NavigationRail, SplitPane, ContextPane (canonical layout shells)
      ui/           — Table, FormField, PageHeader, SemesterNav, Spinner, Toast
      dialogs/      — EveningDialog, EarningDialog, ExpenseDialog (bits-ui Dialog)
    constants/      — players.ts (PLAYERS array), semesters.ts (SEMESTER_LABEL_MAPPING)
    utils/          — format.ts, parse.ts, sort.ts
    types.ts        — shared domain types (Player, Evening, EveningDto, CashReport, …)
  routes/
    +layout.ts      — SSR/prerender disabled (SPA mode)
    +layout.svelte  — NavigationRail shell
    evenings/       — evening CRUD page
    earnings/       — earning CRUD page
    expenses/       — expense CRUD page
    reports/        — semester report page
```

**Tech stack:** Svelte 5 (runes mode — `$state`, `$derived`, `$effect`, `$props`), SvelteKit with `adapter-static` (SPA fallback), Tailwind CSS v4, bits-ui v2 for headless components, Vitest for unit tests.

**Styling:** Tailwind v4 with a custom dark-mode design token system defined in `app.css` under `@theme`. Use semantic tokens (`bg-surface-base`, `text-text-primary`, `border-border-default`, etc.) — never hardcode colors. Icons via Material Symbols Rounded loaded from Google Fonts.

**Data flow:** Each route has a `+page.ts` `load()` function for initial data fetch; pages manage local state with `$state` and re-fetch in `$effect` on filter changes. No shared store — data is component-local. API modules in `src/lib/api/` call `/api/v1/*` (proxied to the Spring backend in dev).

**SPA mode:** `+layout.ts` exports `ssr = false; prerender = false`. Static adapter produces `build/index.html` as fallback.

### Frontend React (legacy, kept for rollback)

```
frontend/src/
  api/              — Axios calls per domain
  components/
    HOC/            — withModal, withSpinner, withToasts
    navigation/     — NavigationBar, NavigationDrawer
  constants/        — navigation.js, player.js, semester.js
  services/utils/   — apiUtils, baseUtils, sortUtils
```

Files use `.jsx` throughout.
