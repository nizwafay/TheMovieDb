# 0001 - Application Logging

## Status

Accepted

## Context

The app needs lightweight diagnostics for development and debugging, especially around network failures. At the same time, release builds should avoid noisy logs and must not expose secrets such as TMDB tokens, authorization headers, request URLs with query data, or response bodies.

Crash reporting is useful for a production app, but adding Firebase Crashlytics requires a Firebase project, `google-services.json`, release mapping upload, and privacy/consent considerations. The project should remain runnable without external private configuration.

## Decision

Introduce a dedicated `core:logging` module with an `AppLogger` abstraction.

Debug builds use `AndroidAppLogger`, which writes safe diagnostic messages to Logcat. Release builds use `NoOpAppLogger` by default.

The logger is injected through Koin, so a future production implementation can send non-sensitive errors to Crashlytics or another crash-reporting backend without changing feature or repository code.

Current usage is intentionally narrow:

- `core:network` logs safe summaries of mapped remote failures.
- Logs do not include tokens, headers, full URLs, query data, response bodies, or user-sensitive data.
- Mapped HTTP exceptions keep their original cause internally for debugging.

## Alternatives Considered

- Direct `Log.d`, `Log.w`, and `Log.e` calls: simple, but harder to control, test, and replace.
- Firebase Crashlytics immediately: valuable for production, but adds external setup and private files that do not fit the current project constraints.
- No logging: safest for release silence, but weaker for development diagnostics.

## Consequences

The project has a small, replaceable logging surface. Debugging improves without coupling app code to a specific crash-reporting vendor. Release builds stay quiet by default, and production crash reporting can be added later behind the same abstraction.
