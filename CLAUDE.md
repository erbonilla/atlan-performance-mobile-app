# CLAUDE.md — Atlan Performance (Mobile)

Guidance for Claude Code working in this repository. Read this before generating or editing code.

## Source-of-truth hierarchy
When instructions conflict, defer in this order:
1. `atlan-initial-project-setup-prompt.md` — **primary**. Defines the authoritative *initial setup* scope, exact file tree, package names, and acceptance criteria. This is the narrow, buildable first milestone.
2. `docs/atlan-mobile-implementation-brief.md` — detailed screen/component/state specs. Use for depth on screens already in scope.
3. `docs/atlan-build-prompt.md` — broader product build prompt (12 screens incl. Session Start, Post-Session, History, WeeklyArcDetail). Use for direction beyond the first milestone, not for expanding initial scope.
4. `docs/atlan-framework-recommendation.md` — framework reasoning and phased roadmap.

Scope rule: the initial build implements the **10 screens in the setup prompt only**. Treat extra screens/use-cases in the docs as *future* work — scaffold extension points, do not build them now.

## What Atlan is
Offline-first, bilingual ES/EN native iOS+Android coaching app for executive endurance athletes whose lives disrupt training. It adapts the plan instead of punishing missed sessions, explains training science only on demand, and works poolside without connectivity. Launched-product quality, not a throwaway prototype.

## Architecture (non-negotiable)
- **Shared core:** Kotlin Multiplatform (KMP).
- **iOS UI:** native SwiftUI (UIKit only for lower-level gesture/haptic control).
- **Android UI:** native Kotlin + Jetpack Compose.
- **Do NOT** build as webview, PWA, Ionic, Expo, React Native, Flutter, or Compose Multiplatform UI. iOS must feel native to iOS; Android must feel native to Android.

Shared (KMP) owns logic that must not diverge: domain models, training-plan & session-swap logic, offline-first repository interfaces, seed data, localization keys, sync contracts, validation rules, design-token constants.

Platform code owns: navigation, UI rendering, haptics, notifications, background work, accessibility semantics, storage adapters.

Data flow: UI action → platform UI event → shared use case → local write → UI updates from local state → sync-queue item created → platform worker drains later. **Local storage is the source of truth; network never blocks Wet Mode.**

## Naming (use exactly)
```
Product name:        Atlan Performance
Repository folder:   atlan-performance-mobile
Shared package:      com.atlan.performance.shared
Android package:     com.atlan.performance.android
iOS app name:        AtlanPerformance
iOS bundle id:       com.atlan.performance.ios
```
Top-level module layout: `shared/`, `androidApp/`, `iosApp/`, plus root docs (`README.md`, `ARCHITECTURE.md`, `PRODUCT_PRINCIPLES.md`, `DESIGN_TOKENS.md`, `LOCALIZATION.md`, `OFFLINE_FIRST.md`) and Gradle config. Full tree: §4 of the setup prompt. Preserve the conceptual separation even if the KMP template requires minor layout tweaks.

## Product principles — enforce in code, copy, and tests
1. Life interruption is the default state, not the exception.
2. Absorb disruption silently; never mark it red.
3. Scientific depth lives one tap below the surface (Why affordances).
4. The dashboard answers only: *What should I do today?* and *Is the week still on track?*
5–10. **No** streak pressure, **no** leaderboards, **no** peer ranking, **no** red missed-session shame states, **no** "you are behind" language. Spanish is a first-class product language, not a translation layer. Wet Mode must work offline and be operable with wet hands.

These are hard constraints. Never introduce streaks, flames, trophies, badges, leaderboards, rank, "missed", "streak broken", "behind plan", or red failure states anywhere — UI, copy, telemetry, or model fields.

## Design tokens
**Colors:** Abyss `#0B2A3C`, AbyssDeep `#061A26`, Tide `#0E8A9A`, TideDeep `#0A6F7D`, TideSoft `#BFE0E5`, TidePale `#DDEEF1`, Coral `#FF6A3D`, CoralBright `#FF7E50`, CoralDeep `#E55428`, Foam `#ECF7F8`, FoamWarm `#F4FAFB`, Paper `#FBFCFC`.
- Abyss/AbyssDeep = primary dark surfaces; Foam/Paper = primary light surfaces.
- Tide = science, calm progress, selected state, Why affordances.
- Coral is **rare** — only productive action, completion, high-signal accents. Never Coral for errors/guilt/missed sessions. No red disruption states.

**Spacing:** xs 4, sm 8, md 12, lg 16, xl 24, 2xl 32, 3xl 48, 4xl 64.
**Radii:** sm 4, md 8, lg 16, sheet 24, phone 32, pill 999.
**Type:** platform-native impl. Hierarchy: Display (calm editorial), Body (high-legibility sans), Numeric (tabular), Wet Mode (extra-large high-contrast). Stub custom font names/resources for later. Dynamic Type (iOS) / scalable sizes (Android). Doc references Fraunces (display) + Manrope (body); native fallbacks acceptable.

## Initial screens (10) and key behaviors
Language Selection → Welcome → Calibration → Tuned Summary → Today Dashboard; Session Detail (from Dashboard); Why Modal (from Session Detail `i`); Wet Mode (from Session Detail Start); Session Swapper (openable from Dashboard); Settings. Routes per platform: `language, welcome, calibration, tunedSummary, dashboard, sessionDetail, wetMode, settings`; modal routes: Why Modal, Session Swapper.
- Use the real Atlan starter copy verbatim from the setup prompt (§8–§14), in both EN and ES.
- Wet Mode: AbyssDeep bg, Foam text, CoralBright for productive action; very large type; swipe-right completes, swipe-left pauses, long-swipe-down exits; large swipe threshold; haptics where available; write completion locally first. Provide accessible actions (Complete/Pause/Exit) — swipe must not be the only path. Leave TODOs for production gesture thresholds and waterproof-pouch testing.
- Why Modal: Tide concept label + mechanism block; precise sourced science, no motivational copy; scrollable; close affordance.
- Session Swapper: neither action is failure; accept/skip update local state; no red warnings.

## Domain & use cases (shared)
Models and enums per setup prompt §16 (`Language`, `WorkoutDiscipline`, `WorkoutIntensity`, `SessionStatus`, `UserProfile`, `TrainingSession`, `SessionSet`, `WhyConcept`, `SwapProposal`, `NotificationCadence`, `ExplanationDensity`, `SwapProposalStatus`, etc.). Use cases (§17): CompleteOnboarding, GetTodayDashboard, GetTodaySession, GetWhyConcept, ProposeSessionSwap, AcceptSessionSwap, CompleteWorkoutSet, ToggleWetMode. Back them with **fake in-memory repositories** designed for later swap to real local DB repos.

## Localization
EN and ES first-class. No flag icons. Never auto-select language by locale/IP. Long Spanish strings wrap, never shrink. Preserve emotional parity, not literal translation. Implement shared Kotlin copy access keyed by the keys in setup prompt §19; map to native strings later.

## Testing (initial, shared `commonTest`)
- `ProposeSessionSwapUseCaseTest` — swap is not framed as failure, weekly load still on track, no shame copy.
- `GetTodayDashboardUseCaseTest` — returns today's session, has Why-affordance data + weekly-arc state, **no streak/leaderboard fields**.
- `LocalizationParityTest` — EN+ES onboarding keys both exist, ES welcome title non-empty, equal-priority options, ES not a fallback.

## Scope discipline — do NOT build now
No backend, auth, subscription/billing, analytics SDK, HealthKit, Health Connect, push notifications, real sync, production database, or calendar integration. Only scaffold extension points + TODOs. Prioritize: clean architecture, native UI shell, domain correctness, offline-first mental model, design tokens, bilingual structure, Wet Mode foundation, no-guilt language.

TODO anchors to leave: SQLDelight/platform DB adapters, background sync (WorkManager / BackgroundTasks), conflict resolution, remote API, auth, encrypted local storage, notification channels, Health Connect/HealthKit, TalkBack/VoiceOver semantics, Wet Mode haptics.

## Build / run (KMP)
- Android: open repo in Android Studio; run the `androidApp` configuration. Acceptance: launches to Language Selection.
- iOS: open `iosApp` in Xcode (KMP shared framework linked via the generated integration); run the `AtlanPerformance` scheme. Acceptance: launches to Language Selection.
- Shared KMP module must compile independently; run `commonTest` for the three tests above.
(Replace with exact Gradle tasks / Xcode steps once the project is scaffolded.)

## First-milestone acceptance (gate)
Repo opens in Android Studio; shared KMP compiles; both apps launch to Language Selection; user can traverse Language → Welcome → Calibration → Tuned Summary → Dashboard; Dashboard shows Today card + weekly-arc placeholder + metric chips; Session Detail opens from Dashboard; Why Modal and Wet Mode open from Session Detail; Session Swapper opens from Dashboard; fake repos provide seed data; **no shame copy, streaks, leaderboards, peer ranking, or red missed-session UI anywhere**; README + architecture docs exist.
