# Deployment

## Scope

This document covers deployment for:
1. Android client build/release pipeline.
2. Coordination requirements with the backend mobile API dependency.

## Android Release Build Commands

```bash
# Clean + compile + lint + unit test
./gradlew --no-daemon clean lintDebug testDebugUnitTest

# Build release artifact (unsigned/signed depending on local setup)
./gradlew --no-daemon assembleRelease

# Optional bundle for Play upload workflows
./gradlew --no-daemon bundleRelease
```

## CI Pipeline

GitHub Actions workflow (`.github/workflows/android-ci.yml`) currently runs:

```bash
./gradlew --no-daemon build
./gradlew --no-daemon test
./gradlew --no-daemon lint
```

Treat CI green status as minimum quality gate, not full production sign-off.

## Backend Readiness Checklist (Blocking for Production)

Before shipping a client release, verify backend mobile contract readiness:

```bash
BASE_URL="https://your-backend-domain.com"

curl -f "$BASE_URL/api/v1/mobile/auth/login/" -H 'Content-Type: application/json' -d '{"email":"user@example.com","password":"pass"}'
curl -f "$BASE_URL/api/v1/mobile/books/" -H 'Authorization: Bearer <token>'
curl -f "$BASE_URL/api/v1/mobile/books/<bookId>/" -H 'Authorization: Bearer <token>'
```

Also confirm job orchestration and downloadable artifact endpoints in a staging load test.

## Environment Configuration

- `local.properties` supplies local secrets and keys.
- `BuildConfig` is generated at build time from Gradle fields.
- Keep production base URL and credentials in secure CI/CD secret stores, not repository files.

## Release Coordination with Backend

- Version backend APIs with backward compatibility for older app builds.
- Roll out backend changes before (or in lockstep with) client changes requiring new fields.
- Maintain consistent error schemas to prevent client deserialization/regression issues.

## Production Caveats

- The app uses Room destructive migration fallback; local cache loss can happen after schema changes.
- Long-running generation jobs may exceed default proxy/gateway timeouts unless backend queue + polling is tuned.
- Release signing, Play Console tracks, and staged rollout policy are not codified in this repo; ensure your org-level process covers these steps.
- Monitor 401/refresh loops and polling load after deployment to avoid cascading backend pressure.
