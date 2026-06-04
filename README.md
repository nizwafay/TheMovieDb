# TheMovieDb

An Android movie discovery app built with Jetpack Compose and The Movie Database (TMDB) API. The app starts from a genre-first experience, lets users browse movies by genre, opens movie details, plays the first YouTube trailer, and shows user reviews with pagination.

## Highlights

- Jetpack Compose UI with Material 3
- Modular architecture with clean architecture boundaries
- MVVM for feature presentation logic
- Koin dependency injection
- Retrofit, OkHttp, and Moshi for networking
- Room for local cache
- Coroutines for async work
- Pull to refresh and endless scrolling
- Offline/cache-aware UI states
- Movie detail with trailer playback, metadata, chips, rating, and reviews
- YouTube-style fullscreen player behavior
- Pure unit tests with MockK
- JaCoCo coverage report and verification
- Detekt static analysis
- GitHub Actions CI quality gates
- Debug Logcat logger with release no-op logging
- Release hardening with R8, resource shrinking, backup restrictions, and cleartext traffic disabled

## Modules

```text
app
core:data
core:database
core:domain
core:logging
core:model
core:network
core:testing
core:ui
feature:genres
feature:movies
```

The feature modules own UI and ViewModels. Domain owns use cases and repository contracts. Data owns repository implementations, data sources, mappers, and cache behavior. Network and database are kept as reusable infrastructure modules.

Architecture tradeoffs are documented in [Architecture Decision Records](docs/adr/README.md).

## Current Features

- Browse movie genres from TMDB with Room-backed cache fallback.
- Browse movies by genre with endless scrolling.
- Open movie details using only the movie id from navigation.
- Show trailer, title, overview, rating, release date, genres, and spoken languages.
- Show user reviews with a YouTube-inspired preview and expandable review panel.
- Keep movie detail locked to portrait for reading.
- Rotate automatically to landscape only when the YouTube player enters fullscreen.
- Close fullscreen with either the YouTube control or the hardware back button.

Reusable UI utilities live in `core:ui`, including loading/empty/error state pieces, pull-to-refresh support, pagination helpers, date formatting, message mapping, movie UI formatting, and the YouTube player wrapper.

## Setup

Create or update `local.properties` with a TMDB API Read Access Token:

```properties
tmdb.accessToken=YOUR_TMDB_READ_ACCESS_TOKEN
```

You can also provide it through an environment variable:

```bash
export TMDB_ACCESS_TOKEN=YOUR_TMDB_READ_ACCESS_TOKEN
```

Use the TMDB API Read Access Token for Bearer auth, not the shorter legacy API key. Do not commit API tokens.

## Run

Build the debug APK:

```bash
./gradlew :app:assembleDebug
```

Run all pure debug unit tests:

```bash
./gradlew testDebugUnitTest
```

Build the release APK:

```bash
./gradlew :app:assembleRelease
```

Release builds require a valid TMDB read access token. The build fails early if the token is missing or looks like the legacy API key.

## JaCoCo

Generate the aggregate JaCoCo report for pure debug unit tests:

```bash
./gradlew jacocoDebugUnitTestReport
```

Open the HTML report:

```text
build/reports/jacoco/jacocoDebugUnitTestReport/html/index.html
```

Run the coverage rules:

```bash
./gradlew jacocoDebugUnitTestCoverageVerification
```

Or run both:

```bash
./gradlew jacocoDebugUnitTestReport jacocoDebugUnitTestCoverageVerification
```

Current rules:

- Line coverage minimum: `70%`
- Branch coverage minimum: `50%`

The report is intentionally scoped to pure unit tests only. It excludes Android framework wiring and UI surfaces such as DI modules, Activity/Application, Compose screens/routes, generated classes, `core:database`, and `core:testing`.

## Static Analysis

Run Detekt:

```bash
./gradlew detekt
```

Detekt uses the shared configuration in `config/detekt/detekt.yml`. The rule set keeps production code strict while allowing a few intentional Android, Compose, and test-helper patterns.

## Debugging And Stability

The app uses a small `core:logging` abstraction instead of direct Logcat calls across the codebase.

- Debug builds use Logcat for safe diagnostic messages.
- Release builds use no-op logging by default.
- Network failures are logged as safe summaries only.
- Tokens, headers, full URLs, query data, response bodies, and user-sensitive data must not be logged.
- Crashlytics can be added later behind the same `AppLogger` interface.

The logging decision is documented in [ADR 0001 - Application Logging](docs/adr/0001-application-logging.md).

## CI

GitHub Actions runs the main quality gates on pull requests and pushes to `main` or `master`:

```bash
./gradlew testDebugUnitTest jacocoDebugUnitTestReport jacocoDebugUnitTestCoverageVerification
./gradlew detekt
./gradlew lintDebug
./gradlew :app:assembleRelease
```

The workflow uses a CI-only placeholder TMDB token for release compilation. Real app builds should still provide a real TMDB API Read Access Token through `local.properties` or `TMDB_ACCESS_TOKEN`.

## Testing Approach

The test suite focuses on pure unit tests:

- ViewModel state transitions
- Use case behavior
- Repository and cache behavior
- Mapper conversions
- Data source delegation and mapping
- Network error mapping
- UI formatter/message helpers
- Navigation route construction

Instrumentation and integration-style tests are intentionally not part of the current test strategy.

## Security Notes

The app uses several baseline protections:

- Release minification with R8
- Release resource shrinking
- Android cleartext traffic disabled
- Backup and device-transfer data excluded
- Release token validation
- Basic ProGuard rules for Retrofit and Moshi reflection
- Original launcher icon assets, with no third-party or copyrighted branding

Important: shipping `BuildConfig.TMDB_ACCESS_TOKEN` inside the APK does not truly protect the token. R8 makes extraction harder, not impossible. For a production app, use a backend proxy so the TMDB token never ships to users.

## Architecture Snapshot

```text
Compose Screen
    -> ViewModel
        -> UseCase
            -> Repository contract
                -> Repository implementation
                    -> Remote data source / Local data source
                        -> Retrofit / Room
```

The UI observes immutable state from the ViewModel. The domain layer stays independent of Android framework details. Data modules coordinate remote-first loading, cache fallback, and error propagation.
