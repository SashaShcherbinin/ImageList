# ImageList

Android app built on a clean **MVI (Model–View–Intent)** architecture with a multi-module Gradle project structure.

## Module Structure

```
ImageList/
├── app/                    # Application module — features, DI wiring, entry point
├── core/                   # Pure Kotlin — domain utilities, exceptions, extensions
├── base/
│   ├── compose/            # MVI engine, theme, shared Compose components, navigation
│   ├── network/            # Ktor HttpClient configuration (Koin module)
│   ├── storage/            # In-memory + optional persistent cache (LocalStorage)
│   └── logger/             # AppLog interface + Koin module
└── buildSrc/               # Convention Gradle plugins for library modules
```

### `:app`

The application module. Contains all feature packages and is the only module that depends on everything else.

**Feature package structure:**

```
feature/<name>/
├── data/           # DTOs, mappers, API service, repository implementation
├── di/             # Koin module for the feature
├── domain/         # Entities, repository interface, use cases, navigation routes
└── presentation/   # State / Intent / Effect / Event, ViewModel, Composable screen
```

### `:core`

Pure Kotlin module with no business logic and no Android dependencies. Extends Kotlin's standard library with reusable utilities shared across all modules — `Result` helpers, `Flow` operators, Koin DSL shorthands, domain exceptions, and shared startup contracts.

### `:base`

A set of modules that wrap third-party frameworks — UI (Compose), networking (Ktor), caching/storage, and logging. Each module hides framework-specific setup behind a simple interface so features stay decoupled from implementation details.

---

## MVI Architecture

The MVI implementation lives in `:base:compose`. Every screen follows the same contract.

### Core interfaces

```kotlin
interface Intent           // User actions / UI events
interface Effect           // Internal side-effects produced by the Processor
interface Event            // One-time navigation / UI events delivered to the screen
interface State            // Immutable UI state snapshot
```

### Components

| Component | Responsibility |
|---|---|
| **Processor** | Receives `Intent + State` → returns `Flow<Effect>` (business logic, use case calls) |
| **Reducer** | Receives `Effect + State` → returns new `State?` (`null` = no state change) |
| **Publisher** | Receives `Effect` → returns `Event?` (converts effects to one-time events) |
| **Repeater** _(optional)_ | Receives `Effect + State` → returns `Intent?` (triggers a follow-up intent automatically) |

### `MviViewModel`

```
UI sends Intent
       │
       ▼
  Processor.process(intent, state) → Flow<Effect>
       │
  for each Effect:
       ├─ Reducer.reduce(effect, state) → updates StateFlow
       ├─ Publisher.publish(effect)    → emits one-time Event via SharedFlow
       └─ Repeater.repeat(effect, state) → re-calls process() with a new Intent
```

- `state` is exposed as `StateFlow` — the screen collects it to render UI.
- `event` is exposed as `SharedFlow` — the screen collects it for navigation or toasts.
- Each `Intent` class cancels any in-flight job for the same intent type before starting a new one.
- Events are wrapped in `Consumable<T>` to prevent re-delivery after configuration changes.

---

## Dependency Injection (Koin)

Modules are registered in `MainApplication`:

```kotlin
startKoin {
    modules(
        loggerModule(),
        networkModule(),
        featureSplashModule(),
        featurePhotosModule(),
    )
}
```

Each feature owns its own Koin module that wires:

- `viewModelOf(::FeatureViewModel)`
- `factoryOf(::Processor)`, `factoryOf(::Reducer)`, `factoryOf(::Publisher)`
- `factoryCastOf(::UseCaseImpl, UseCase::class)` — binds implementation to interface
- `singleCalsOf(::RepositoryImpl, Repository::class)` — singleton bound to interface
