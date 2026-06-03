# Atlan Performance — Mobile

Offline-first, bilingual (ES/EN) native iOS + Android coaching app for executive endurance athletes
whose lives disrupt their training. Atlan adapts the plan instead of punishing missed sessions,
explains training science only when asked, and works poolside without connectivity.

## Framework decision
**Kotlin Multiplatform shared core + native SwiftUI (iOS) + native Jetpack Compose (Android).**
The shared core holds the product logic that must never diverge across platforms; each UI is fully
native for first-class platform feel. Not a webview, PWA, React Native, Flutter, or shared-UI app.
See [`ARCHITECTURE.md`](ARCHITECTURE.md).

## Architecture summary
```text
SwiftUI iOS UI  ─┐
                 ├── Kotlin Multiplatform Shared Core
Compose Android ─┘

Shared Core:
- domain models
- use cases
- repository contracts
- fake local repositories
- seed data
- localization keys
- sync contracts

Platform UI:
- native navigation
- native screens
- native sheets
- platform haptics
- platform notifications
- platform accessibility
```

## Prerequisites
- **JDK 17** — the only requirement to build and test the shared core (see [Verify without a full IDE](#verify-without-a-full-ide)).
- Android Studio (Ladybug or newer) with Android SDK 35 — only to build/run the Android app.
- Xcode 15+ on macOS — only to build/run the iOS app.
- Kotlin 2.1.0 + Gradle 8.11.1 — resolved automatically via the committed Gradle wrapper.

## Verify without a full IDE
The shared KMP core compiles and runs on a desktop **JVM target**, so its logic and the three
`commonTest` suites can be verified from the command line with **only a JDK 17 installed** — no
Android Studio, no Xcode, no Android SDK:

```bash
./gradlew :shared:jvmTest          # compile shared core + run the 3 commonTest suites (JDK-only)
./gradlew :shared:compileKotlinJvm # compile-only check of the shared core (JDK-only)
```

The Gradle wrapper (`gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`) is committed, so
the first run downloads Gradle 8.11.1 itself — no separate Gradle install needed.

> The JVM target carries **no production UI**; it exists purely so this milestone is verifiable
> without a full IDE. The shipping platforms remain native Android (Compose) and iOS (SwiftUI).
> Test results land in `shared/build/reports/tests/jvmTest/index.html`.
>
> `:shared:allTests` and `:shared:testDebugUnitTest` additionally exercise the Android/iOS targets and
> therefore need the Android SDK / Xcode; use `:shared:jvmTest` for the IDE-free path.

## Run Android
1. Open the `atlan-performance-mobile/` folder in Android Studio.
2. Let Gradle sync (`:shared` + `:androidApp`).
3. Run the **androidApp** configuration on a device/emulator (minSdk 26).
4. The app launches to **Language Selection**.

CLI equivalent (needs the Android SDK):
```bash
./gradlew :androidApp:installDebug      # build + install
./gradlew :shared:jvmTest               # run shared KMP tests (JDK-only, no SDK needed)
```
> The Gradle wrapper is committed, so `./gradlew` works from a clean checkout with only a JDK.

## Run iOS
Requires **full Xcode** (not just the Command Line Tools) — it provides the iOS SDK + simulators.
The Xcode project is generated from [`iosApp/project.yml`](iosApp/project.yml) by
[XcodeGen](https://github.com/yonaskolb/XcodeGen), so it is reproducible and never hand-edited.

```bash
brew install xcodegen                       # one-time
cd iosApp && xcodegen generate              # writes AtlanPerformance.xcodeproj from project.yml
open AtlanPerformance.xcodeproj             # then pick a simulator and press Run
```

A pre-build script in the generated project runs `./gradlew :shared:embedAndSignAppleFrameworkForXcode`,
which builds the KMP `Shared` framework for the active configuration/SDK and links it — no manual
framework wiring. (The script sets `JAVA_HOME` to the Homebrew `openjdk@17` since Xcode's build
environment has a minimal PATH.) The app launches to **Language Selection**.

> Edit `project.yml` (never the `.xcodeproj`) and re-run `xcodegen generate` to change build settings.
> Bridge calls into the shared module are centralized in
> [`SharedContainer.swift`](iosApp/AtlanPerformance/SharedBridge/SharedContainer.swift); if any
> KMP→Swift generated signature needs a tweak after the first framework build, it lives there.

## Shared code structure
`shared/src/commonMain/.../shared/`: `design/`, `domain/{model,repository,usecase}/`, `data/{seed,fake,sync}/`,
`localization/`, `presentation/`. Tests in `commonTest/`. Platform `Platform` in `androidMain` / `iosMain` / `jvmMain` (the JVM actual backs IDE-free verification).

## Implemented in this first setup
- KMP shared module: domain models, 8 use cases, repository interfaces, fake in-memory repos, seed data,
  sync-queue types, bilingual copy, presentation state, design-token constants.
- Three shared tests (swap-without-shame, dashboard shape, localization parity).
- Android Compose app: theme, custom Atlan components, route navigation, all 10 screens.
- iOS SwiftUI app: design tokens, components, coordinator navigation, all 10 screens, KMP bridge.
- Six product/architecture docs.

## Intentionally NOT implemented yet (extension points + TODOs only)
backend · auth · subscription/billing · analytics SDK · HealthKit · Health Connect · push notifications ·
real sync · production database · calendar integration. See [`OFFLINE_FIRST.md`](OFFLINE_FIRST.md).

## Product guardrails
No streaks, leaderboards, peer ranking, red missed-session states, or "behind plan" language anywhere.
See [`PRODUCT_PRINCIPLES.md`](PRODUCT_PRINCIPLES.md).
