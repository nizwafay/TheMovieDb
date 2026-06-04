# 0006 - TMDB Token Handling

## Status

Accepted for the current app; not recommended as the final production security model.

## Context

TMDB requires a bearer token. The app needs a simple local and CI setup while avoiding accidental token commits.

## Decision

Read the TMDB token from `local.properties` or `TMDB_ACCESS_TOKEN`, then expose it through `BuildConfig.TMDB_ACCESS_TOKEN`.

Release builds validate that the token is present and does not look like the legacy API key format. `local.properties` is ignored by Git.

## Alternatives Considered

- Hardcode the token: rejected because it risks accidental exposure and is poor practice.
- Use a backend proxy: best production option, but outside the current app scope.
- Fetch token remotely at runtime: still requires protecting the remote source and does not solve the core trust problem alone.

## Consequences

This approach keeps setup simple. It does not truly secure the TMDB token because APK contents can be inspected. A production app should call a backend proxy so the token never ships to users.
