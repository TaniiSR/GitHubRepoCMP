# GitHubRepoCMP

Trending GitHub repositories on **Android and iOS** from one Kotlin codebase. It is a clone of
[GitHubRepo](https://github.com/TaniiSR/GitHubRepo) (same UX/UI, same GitHub search API) rebuilt with
Kotlin Multiplatform, Compose Multiplatform and MVI.

## Features
- Cache-first list of top-starred repositories, pull to refresh, shimmer loading skeleton
- Tap a row to expand description, language and star count
- Typed error handling with the original error copy, Lottie error view and Retry

## Tech stack
Kotlin 2.4 - Compose Multiplatform - MVI - Ktor (OkHttp / Darwin) - kotlinx.serialization -
Koin - SQLDelight (offline cache) - Coil 3 - Compottie (Lottie) - AndroidX Lifecycle ViewModel

## Architecture
```
shared/      domain (pure Kotlin) -> data (Ktor, SQLDelight) -> presentation (MVI) -> ui (Compose)
androidApp/  Android entry point
```
Unidirectional flow: `UI -> Intent -> ViewModel -> State -> UI`, with one-shot `Effect`s for messages.

## Tests (TDD)
Written test-first: DTO parsing, Ktor `MockEngine` status table, SQLDelight cache, repository cache
policy, ViewModel state sequences (Turbine), DI wiring and Compose UI tests.
```bash
./gradlew :shared:allTests          # JVM + iOS simulator
./gradlew :androidApp:assembleDebug
```
