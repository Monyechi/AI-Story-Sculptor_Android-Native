# Security

## Security Boundaries

- Client-side Android app is a semi-trusted environment; never rely on it for final authorization decisions.
- Backend API is the trust boundary for authZ/authN, rate limiting, and sensitive data processing.

## Secret and Credential Handling

Do not commit secrets. Use `local.properties` or CI secret injection.

Example local setup:

```properties
OPENAI_API_KEY=sk-...
STRIPE_PUBLISHABLE_KEY=pk_...
BASE_URL=https://your-backend-domain.com/
```

Quick secret scanning before commit:

```bash
git diff --cached
rg -n "(sk-[A-Za-z0-9]|pk_live_|BEGIN PRIVATE KEY|password\s*=)" .
```

## Authentication and Session Notes

- Mobile contract expects bearer token auth.
- Tokens/session metadata persisted locally must be treated as sensitive.
- Force logout on unrecoverable token-refresh failures.

Backend validation commands:

```bash
BASE_URL="https://your-backend-domain.com"
TOKEN="replace_me"

curl -i "$BASE_URL/api/v1/mobile/books/" -H "Authorization: Bearer $TOKEN"
curl -i "$BASE_URL/api/v1/mobile/auth/refresh/" \
  -H 'Content-Type: application/json' \
  -d '{"refresh_token":"replace_me"}'
```

## Transport and API Hardening Requirements

- Enforce HTTPS for all production traffic.
- Apply backend-side rate limits on auth, create-book, and job polling endpoints.
- Validate and sanitize all fields server-side (title, summary, character metadata, etc.).
- Return stable, non-sensitive error messages and avoid leaking stack traces.

## Module-Level Security Responsibilities

- `data/api`: network request shaping, auth headers, timeout behavior.
- `data/datastore` + `data/db`: local persistence minimization and secure handling.
- `domain`: enforce business invariants before expensive/generative operations.
- `ui`: avoid showing sensitive backend diagnostics in production UI.

## Production Caveats

- Current repository includes local password hashing patterns (SHA-256 comments) suitable only for local/offline prototypes; production auth should be backend-managed with modern password hashing (argon2/bcrypt) and hardened token lifecycle.
- Debug logging can expose request/response payloads; ensure release builds keep HTTP logs disabled.
- Client compromise is always possible; backend must independently validate ownership and permissions for every `bookId` and download request.
