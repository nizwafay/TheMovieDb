# 0004 - Remote First Cache Strategy

## Status

Accepted

## Context

The app consumes TMDB data and should remain useful when the network is slow, unavailable, rate-limited, or temporarily failing. Genre and movie list data are good candidates for local caching.

## Decision

Use a remote-first cache strategy:

1. Try to load fresh data from the remote data source.
2. Save successful responses to Room.
3. Read cached data after the remote attempt.
4. Return cached data when available, while still exposing refresh errors through UI messages.
5. Throw the mapped error when no cached data exists.

The shared `RemoteFirstCache` helper keeps this flow reusable across repositories.

## Alternatives Considered

- Network-only: simpler, but poor offline behavior.
- Database-only with background refresh: stable UI, but more infrastructure than the current app needs.

## Consequences

The UI can show saved data and still tell the user when refresh fails. Repository logic is slightly more involved, but the behavior is closer to a resilient app experience.
