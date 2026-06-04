# 0002 - Modular Clean Architecture

## Status

Accepted

## Context

The app has multiple feature areas and shared infrastructure. The codebase needs boundaries that make ownership, dependencies, and test targets clear as the app grows.

## Decision

Use modular clean architecture:

- `app` owns app startup, navigation graph, theme, and dependency wiring.
- `feature:*` modules own feature UI, routes, and ViewModels.
- `core:domain` owns use cases, repository contracts, results, and domain errors.
- `core:data` owns repository implementations, data-source contracts, mappers, and cache coordination.
- `core:network` owns Retrofit services, DTOs, interceptors, and network factories.
- `core:database` owns Room database, DAOs, and entities.
- `core:model` owns shared app models.
- `core:ui` owns reusable UI utilities and formatters.
- `core:logging` owns the application logging abstraction and implementations.
- `core:testing` owns shared test utilities.

Repository contracts live in `core:domain`; implementations live in `core:data`.

## Alternatives Considered

- Single app module: simple initially, but boundaries become unclear as features grow.
- Feature-only modularization: useful for UI ownership, but shared domain and data infrastructure can still become tangled.
- Repository contracts and implementations in the same module: easier wiring, but weaker dependency direction.

## Consequences

The project has more Gradle modules and wiring than a small sample app. In return, domain logic stays independent from Android framework details, shared infrastructure is reusable, and feature code depends on stable contracts.
