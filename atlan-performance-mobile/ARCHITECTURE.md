# Architecture — Atlan Performance

## Stack
- **Shared core:** Kotlin Multiplatform (KMP) — pure Kotlin, no UI.
- **iOS UI:** native SwiftUI (UIKit only for low-level gesture/haptic control).
- **Android UI:** native Kotlin + Jetpack Compose (Material 3 as a low-level base only).
- **Not** webview / PWA / Ionic / Expo / React Native / Flutter / Compose Multiplatform UI.

```text
SwiftUI iOS UI  ─┐
                 ├── Kotlin Multiplatform Shared Core
Compose Android ─┘

Shared Core (shared/):
- domain models
- use cases
- repository contracts
- fake local repositories
- seed data
- localization keys
- sync contracts (types only)
- design-token constants

Platform UI (androidApp/, iosApp/):
- native navigation
- native screens
- native sheets
- platform haptics      (TODO)
- platform notifications (TODO)
- platform accessibility
```

## Layering
```
presentation (state holders, shared)  ->  consumed by native UI
        |
   use cases (shared)
        |
 repository interfaces (shared)
        |
 fake in-memory repos (shared)  ->  later: real local-DB repos
        |
   domain models + seed (shared)
```
Native UI never talks to repositories directly — it goes through shared use cases and reads shared
presentation state. This keeps the "product brain" identical across iOS and Android.

## Module map
- `shared/src/commonMain` — all shared logic (design, domain/model, domain/repository, domain/usecase,
  data/seed, data/fake, data/sync, localization, presentation).
- `shared/src/commonTest` — the three starter tests.
- `shared/src/androidMain` / `iosMain` — `Platform` expect/actual.
- `androidApp/` — Compose app: navigation, design, screens.
- `iosApp/` — SwiftUI app: Navigation, Design, Screens, SharedBridge (KMP framework bridge).

## Offline-first
Local storage is the source of truth; network never blocks Wet Mode. See `OFFLINE_FIRST.md`.

## What is shared vs. native
| Shared (KMP) | Native (per platform) |
|---|---|
| domain models, training-plan & swap logic | navigation, UI rendering |
| offline-first repository interfaces | haptics, notifications, background work |
| seed data, localization keys, sync contracts | accessibility semantics |
| validation rules, design-token constants | platform storage adapters |
