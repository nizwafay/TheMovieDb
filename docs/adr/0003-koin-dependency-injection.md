# 0003 - Koin Dependency Injection

## Status

Accepted

## Context

The app needs dependency injection for repositories, use cases, data sources, Retrofit, Room, logging, and ViewModels. The graph should remain readable and modular.

## Decision

Use Koin for dependency injection.

Koin modules are split by responsibility:

- app-level modules for ViewModels, use cases, and app configuration.
- data modules for repositories, local data sources, remote data sources, and coroutines.
- network module for Retrofit, OkHttp, Moshi, API services, and auth.
- database module for Room and DAOs.
- logging module for debug and release logger implementations.

## Alternatives Considered

- Hilt: strong compile-time integration and common in Android production apps, but requires more generated wiring and annotations.
- Manual dependency injection: simple for small apps, but noisy as the graph grows.

## Consequences

Koin keeps module setup concise and easy to read. It trades compile-time DI validation for lower ceremony and flexible runtime wiring.
