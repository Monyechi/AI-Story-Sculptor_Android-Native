# AI Story Sculptor (Android Native)

Native Android client for the existing Django backend, built with Kotlin + Jetpack Compose.

## Tech Stack

- Kotlin + Jetpack Compose (Material 3)
- MVVM + Repository pattern
- Coroutines + Flow
- Retrofit + OkHttp
- Kotlinx Serialization
- Room (offline cache)
- Hilt (dependency injection)
- DataStore (token persistence)
- Navigation Compose

## Architecture

Code is split into:

- `data/` → API, DB, DataStore, repository implementations
- `domain/` → models, repository interfaces, use cases
- `ui/` → screens, viewmodels, navigation, ui state

## Backend Base URL Configuration

Set your API base URL through Gradle properties.

1. Open (or create) `local.properties` at project root.
2. Add:

```properties
BASE_URL=https://your-backend-domain.com/
```

Notes:

- Keep trailing slash (the build script also normalizes it).
- This value is exposed as `BuildConfig.BASE_URL`.

## Implemented MVP Skeleton

- Auth flow: Login/Register screens + token persistence
- OkHttp `Authorization` interceptor + 401 refresh authenticator skeleton
- Library screen: Room cached list + pull-to-refresh network update
- Navigation: AuthFlow and MainFlow (Library/Create/Details)
- Create flow: 4-step wizard + submit + polling every 4s for generation status
- Details flow: metadata + chapters + download/share actions
- WorkManager download: background PDF fetch + local file share via FileProvider

## Django Route Mapping (Phase 3)

Mapped legacy backend routes (in `LegacyDjangoApi`):

- `login/`
- `register/`
- `bookshelf/`
- `create/`
- `book/{bookId}/details/`
- `download/pdf/{bookId}/`
- `download/docx/{bookId}/`

Important:

- Current Django app is mostly session-auth + HTML-rendered endpoints.
- For first-class native support, add JSON API endpoints (DRF recommended) for auth/library/create/details/status.

## TODO Markers You Should Replace

Search for `TODO:` in source files and replace:

- Exact endpoint paths
- Exact request/response JSON fields
- Create-book flow + polling
- Book details, download, and share integration

## Run

1. Open this folder in Android Studio.
2. Sync Gradle.
3. Configure `BASE_URL`.
4. Run app on emulator/device.

