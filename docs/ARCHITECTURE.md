# Architecture

## High-Level Overview

This repository contains one Android application module (`app`) using Kotlin + Compose, with clean boundaries between UI, domain, data, and DI layers.

Backend dependency is external: Django (or compatible service) must implement the mobile contract described in `docs/MOBILE_API_CONTRACT.md`.

## Module and Package Boundaries

### `ui/` (presentation boundary)

- Screens and composables (`ui/screen/...`)
- Navigation (`ui/navigation/...`)
- ViewModels and state (`ui/viewmodel/...`, `ui/common/...`)

Rules:
- `ui` should call domain use cases/repositories, not raw DB or networking clients.
- UI state should remain platform-friendly and testable.

### `domain/` (business boundary)

- Models (`domain/model/...`)
- Repository interfaces (`domain/repository/...`)
- Use cases (`domain/usecase/...`)
- Result wrappers (`domain/common/...`)

Rules:
- `domain` must not depend on Android framework or Retrofit/Room classes.
- API/domain mapping belongs to `data` layer adapters.

### `data/` (integration boundary)

- Repositories (`data/repository/...`)
- API interfaces and DTOs (`data/api/...`)
- Local persistence (`data/db/...`, `data/datastore/...`)
- Content generation services (`data/generation/...`)

Rules:
- External integrations (OpenAI, Room, DataStore, backend APIs) are implemented here.
- Convert persistence/network DTOs to domain models at this layer boundary.

### `di/` (wiring boundary)

- Hilt modules for network/database/repositories.

Rules:
- Keep binding/provision logic centralized.
- Enforce singleton/resource lifetime explicitly.

## Build and Runtime Flow

1. App launch initializes Hilt graph and Compose host.
2. ViewModels call domain-facing repository contracts.
3. Repository implementations coordinate:
   - Local DB (Room)
   - Local auth/session persistence (DataStore)
   - Generation services and/or backend mobile API endpoints
4. Results propagate to UI as state updates.

## Commands for Architecture Validation

```bash
# Inspect dependency graph at compile level
./gradlew :app:dependencies

# Compile debug variant and verify cross-layer references are valid
./gradlew :app:compileDebugKotlin

# Run unit tests that enforce repository/use-case boundaries
./gradlew :app:testDebugUnitTest --tests '*RepositoryBoundaryUseCaseTest*'
```

## Backend Dependency and Integration Boundaries

- Retrofit mobile endpoints are defined in `data/api/ApiServices.kt` with `/api/v1/mobile/...` paths.
- Contract evolution must remain backward compatible for app versions already in production.
- Auth, library, create job, job polling, details, and download endpoints are critical path dependencies.

## Production Caveats

- Timeouts and retries: generation/status endpoints can be long-running; avoid aggressive client-side retry storms.
- Schema drift: backend key renames can break deserialization even if endpoint availability remains.
- Data persistence changes: Room uses destructive migration fallback currently; schema changes can wipe local cache if not migrated.
- Secret handling: OpenAI and Stripe values are sourced from `local.properties`/BuildConfig; never hardcode secrets in code.
