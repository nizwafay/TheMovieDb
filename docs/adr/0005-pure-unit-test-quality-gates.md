# 0005 - Pure Unit Test Quality Gates

## Status

Accepted

## Context

The project needs reliable automated checks that run quickly in local development and CI. The current priority is business logic, state management, mapping, repository behavior, and error handling.

## Decision

Use pure JVM unit tests as the main test gate.

The test suite covers:

- ViewModel state transitions.
- Use case behavior.
- Repository and cache behavior.
- Mapper conversions.
- Local and remote data-source delegation.
- Network error mapping.
- UI formatter and message helpers.
- Navigation route construction.

JaCoCo verifies aggregate unit-test coverage. Detekt and Android lint run as additional quality gates.

## Alternatives Considered

- Instrumented Room and Compose UI tests: valuable, but slower and more expensive to maintain.
- MockWebServer API contract tests: useful for integration confidence, but outside the current pure unit test boundary.

## Consequences

The test suite is fast and stable. It does not fully replace UI, database, or API integration tests, but it gives strong coverage over the app's decision-making logic.
