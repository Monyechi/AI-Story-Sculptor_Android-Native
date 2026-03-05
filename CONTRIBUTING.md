# Contributing

Thanks for contributing to AI Story Sculptor Android Native.

## Project Boundaries

This repository is the Android client and test harness for a backend API contract.

- Android app module: `app/`
- CI workflow: `.github/workflows/android-ci.yml`
- Mobile backend contract reference: `docs/MOBILE_API_CONTRACT.md`
- Build configuration: `build.gradle.kts`, `app/build.gradle.kts`, `gradle.properties`

Please keep changes scoped:

1. UI and navigation changes belong in `app/src/main/java/.../ui`.
2. Business interfaces and use cases belong in `app/src/main/java/.../domain`.
3. Persistence, API adapters, and generation integrations belong in `app/src/main/java/.../data`.
4. Wiring and dependency graph changes belong in `app/src/main/java/.../di`.

## Local Setup

1. Install JDK 17.
2. Ensure Android SDK 35 and required build tools are available.
3. Create `local.properties` with environment-specific values:

```properties
BASE_URL=https://your-backend-domain.com/
OPENAI_API_KEY=sk-...
STRIPE_PUBLISHABLE_KEY=pk_...
```

4. Validate Gradle and compile:

```bash
./gradlew --version
./gradlew clean assembleDebug
```

## Development Commands

Use these commands before opening a PR:

```bash
./gradlew --no-daemon testDebugUnitTest
./gradlew --no-daemon lintDebug
./gradlew --no-daemon assembleDebug
```

If you can run an emulator/device locally, also execute:

```bash
./gradlew --no-daemon connectedDebugAndroidTest
```

## Backend Dependency Expectations

This Android client depends on a backend implementing `docs/MOBILE_API_CONTRACT.md`.

Before submitting backend-dependent features, verify:

```bash
# Replace BASE_URL to point to your deployed/staging backend
BASE_URL="https://your-backend-domain.com"

curl -i "$BASE_URL/api/v1/mobile/auth/login/" \
  -H 'Content-Type: application/json' \
  -d '{"email":"test@example.com","password":"password"}'
```

Also validate that long-running job routes (`/jobs/{jobId}/`) and download link routes are live and return JSON with stable keys.

## Production Caveats for Contributors

- Do not commit secrets (OpenAI keys, Stripe keys, backend tokens) into source or Gradle files.
- Keep debug logging out of release behavior; networking logs should remain non-verbose in production.
- Avoid breaking DTO compatibility: additive backend changes are preferred to renaming/removing fields.
- The app currently includes local-first auth/book generation internals and a mobile API contract path; backend migration must preserve user data and UX expectations.

## Pull Request Checklist

- [ ] Scope is clear and isolated to the intended layer(s).
- [ ] Unit tests updated or added for changed business behavior.
- [ ] Lint passes for changed modules.
- [ ] Backend contract changes documented in `docs/MOBILE_API_CONTRACT.md` when applicable.
- [ ] Production caveats (timeouts, error handling, retries, token usage) reviewed.
