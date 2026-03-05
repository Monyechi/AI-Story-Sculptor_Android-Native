# Known Limitations

## Android Client Limitations

1. **Single-module codebase**: all functionality lives in `app`, which increases coupling and build times as features grow.
2. **Room migration policy**: database config currently uses destructive migration fallback, risking local data/cache loss on schema updates.
3. **Long-running operations UX**: chapter generation/rendering can be slow and depends on remote/provider latency.
4. **Instrumentation test coverage**: only smoke-level scenarios are covered; broader end-to-end UI automation is limited.

## Backend Dependency Limitations

1. **Strict contract dependency**: client behavior assumes `/api/v1/mobile/...` endpoint shapes from `docs/MOBILE_API_CONTRACT.md`.
2. **Polling sensitivity**: create-job polling UX depends on backend job state cadence and timeout behavior.
3. **Error-shape fragility**: non-uniform backend error payloads can degrade user-facing error handling quality.
4. **Artifact delivery assumptions**: download URLs and expiration semantics are backend-defined; inconsistent behavior impacts share/download reliability.

## Concrete Diagnostics Commands

```bash
# Verify local compile baseline
./gradlew --no-daemon assembleDebug

# Verify deterministic unit tests
./gradlew --no-daemon testDebugUnitTest

# Spot-check backend availability for required routes
BASE_URL="https://your-backend-domain.com"
TOKEN="replace_me"

curl -i "$BASE_URL/api/v1/mobile/books/" -H "Authorization: Bearer $TOKEN"
curl -i "$BASE_URL/api/v1/mobile/books/<bookId>/download/?format=pdf" -H "Authorization: Bearer $TOKEN"
```

## Production Caveats

- If backend introduces breaking response changes, already-released app versions may fail without hotfix capability.
- If backend queues degrade, client polling can amplify load; coordinate retry intervals and caching.
- Release safety depends on external systems (Play rollout strategy, backend observability, incident response) not encoded in this repository.
- OpenAI/provider-side latency/cost controls are external and can impact generation throughput or user-perceived reliability.
