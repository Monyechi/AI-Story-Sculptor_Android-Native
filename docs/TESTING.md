# Testing Guide

## Test Types in This Repo

### Unit Tests (`app/src/test/...`)

Focus:
- ViewModel behavior
- Domain/use-case boundaries
- Repository contract usage with fakes

Run:

```bash
./gradlew --no-daemon testDebugUnitTest
```

### Instrumentation Tests (`app/src/androidTest/...`)

Focus:
- Smoke flow for auth + book creation
- Device/emulator runtime integration

Run:

```bash
./gradlew --no-daemon connectedDebugAndroidTest
```

## Recommended Verification Pipeline

Use this command sequence before merge:

```bash
./gradlew --no-daemon clean
./gradlew --no-daemon lintDebug
./gradlew --no-daemon testDebugUnitTest
./gradlew --no-daemon assembleDebug
```

For CI parity checks:

```bash
./gradlew --no-daemon build
./gradlew --no-daemon test
./gradlew --no-daemon lint
```

## Backend-Dependent Smoke Checks

When a feature depends on backend routes, run endpoint checks against staging/prod-like environment:

```bash
BASE_URL="https://your-backend-domain.com"
ACCESS_TOKEN="replace_me"

curl -i "$BASE_URL/api/v1/mobile/books/" \
  -H "Authorization: Bearer $ACCESS_TOKEN"

curl -i "$BASE_URL/api/v1/mobile/jobs/job_abc123/" \
  -H "Authorization: Bearer $ACCESS_TOKEN"
```

## Module Boundary Coverage Expectations

- `domain` changes must include/adjust deterministic unit tests.
- `ui/viewmodel` changes should include behavior tests using `MainDispatcherRule` and fake repos.
- `data/repository` behavior changes should include tests that validate mapping/error paths across boundary surfaces.

## Production Caveats for Testing

- Emulator-based tests can be flaky in headless CI; keep smoke tests minimal and deterministic.
- Network-dependent instrumentation should avoid live third-party calls (OpenAI, external APIs) in CI.
- Keep backend contract checks in staging; avoid production-destructive test fixtures.
- Validate release logging behavior separately so sensitive payloads are not logged in production builds.
