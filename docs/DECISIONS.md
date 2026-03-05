# Architecture Decision Records

Short records of the key design choices made in this project and why.

---

## ADR-001: MVVM + Repository Pattern

**Status:** Accepted

**Context:**
The app has multiple screens that share common data (auth state, book list, creation status).
We needed an architecture that survives configuration changes, is testable without a running device,
and cleanly separates UI logic from data concerns.

**Decision:**
Adopted MVVM (Model-View-ViewModel) with a clean-architecture Repository layer.
ViewModels hold observable `StateFlow` state; screens are stateless Compose functions that react
to them. All data access goes through domain-layer repository interfaces, never directly from
ViewModels or screens.

**Consequences:**
- ViewModels are plain JVM classes — fully testable with `kotlinx-coroutines-test` and fake repositories.
- UI layer has zero knowledge of Room, Retrofit, or DataStore; swapping implementations requires no screen changes.
- Hilt injects concrete implementations at runtime and fakes at test time.

**Alternatives considered:**
- MVC / naked Activity logic — rejected; not survivable across config changes and not unit-testable.
- MVP — rejected; requires a View interface for every screen, high boilerplate with Compose.

---

## ADR-002: Room for Offline-First Book Cache

**Status:** Accepted

**Context:**
Book generation is asynchronous and takes seconds to minutes. The app must display a responsive
library immediately on launch and survive short network outages without showing an empty screen.

**Decision:**
Room is used as the single source of truth for the local book list, chapters, and characters.
On app launch the UI observes Room `Flow`s immediately; network refreshes write back into Room,
which propagates automatically to all observers. There is no separate "cache invalidation" step.

**Consequences:**
- Library screen is always fast, even offline.
- Pull-to-refresh triggers a network fetch; UI updates arrive via the same Room flow, not a
  separate callback chain.
- Schema migrations are handled by Room and tracked in `data/db/`.

**Alternatives considered:**
- Network-only (no local cache) — rejected; creates empty/loading states on every cold launch.
- DataStore — rejected; not designed for structured relational data like books + chapters + characters.
- SQLDelight — considered; rejected in favour of Room's tighter Kotlin/Android integration and
  first-party Compose `Flow` support.

---

## ADR-003: WorkManager for PDF Background Download

**Status:** Accepted

**Context:**
PDF downloads can be large (several MB) and must survive the user leaving the app mid-download,
the system killing the process, or the screen rotating. A raw coroutine tied to a ViewModel
would be cancelled in any of these scenarios.

**Decision:**
All file downloads are enqueued via WorkManager (`DownloadWorker`). The worker handles HTTP
streaming, writes to the app's private files directory, and shares the result via FileProvider.
Unique work ensures duplicates are not enqueued if the user taps download twice.

**Consequences:**
- Downloads survive process death and are retried on failure (exponential back-off).
- The system schedules the work according to battery/network constraints if needed.
- UI can observe work progress via `WorkInfo` LiveData/Flow without coupling to the worker lifecycle.

**Alternatives considered:**
- Coroutine in ViewModel — rejected; cancelled on process death or navigation away.
- Foreground Service — rejected; requires a persistent notification and is overkill for a
  file download that typically completes in seconds.

---

## ADR-004: Backend Proxy for AI Provider Credentials

**Status:** Accepted

**Context:**
Early prototypes embedded the OpenAI API key in `local.properties` and exposed it via
`BuildConfig`. Any user with a decompiler could extract the key from the APK, leading to
unbounded API costs and potential abuse.

**Decision:**
AI provider credentials (OpenAI API key) live exclusively in the backend server environment.
The Android client calls backend proxy endpoints (`/api/v1/mobile/ai/...`) authenticated with
its own short-lived JWT. The backend validates the token, applies per-user quota checks, and
forwards the key to OpenAI — the key never leaves the server.

**Consequences:**
- Zero AI secrets in the APK.
- Per-user rate limiting and token deduction are enforced server-side, preventing API abuse.
- Backend can rotate the AI provider key at any time without a client release.

**Alternatives considered:**
- BuildConfig key — rejected; trivially extractable from release APK.
- Android Keystore — rejected; Keystore protects keys *generated on device*, not secrets
  provisioned from a third-party service. It cannot safely store an OpenAI API key.
- Certificate pinning only — rejected; pins prevent MITM but do not hide the key from the
  device owner.
