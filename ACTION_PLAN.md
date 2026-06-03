# Atlan Performance — Initial Setup Action Plan

Derived from `atlan-initial-project-setup-prompt.md` (primary) and `docs/` (depth). Scope = the first buildable milestone only: architecture, navigation, design system, shared domain, mock offline data, 10 starter screens, starter tests, docs. **Not** the full product.

## Guiding constraints (carry through every phase)
- KMP shared core + native SwiftUI (iOS) + native Compose (Android). No cross-platform UI.
- Local data is source of truth; network never blocks Wet Mode. Repos are fake/in-memory but shaped for later real-DB swap.
- No streaks/leaderboards/peer rank/red missed-session/"behind plan" anywhere (UI, copy, telemetry, model fields). ES+EN first-class.
- Use exact names/packages (CLAUDE.md → Naming). Use verbatim starter copy (EN+ES) from the prompt.

## Phase 0 — Repo skeleton & docs
1. Create `atlan-performance-mobile/` with root Gradle files (`settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml`, wrapper) and the three-module layout (`shared/`, `androidApp/`, `iosApp/`).
2. Write the six root docs from the prompt: `README.md` (incl. text architecture diagram, run steps, what is/ isn't implemented), `ARCHITECTURE.md`, `PRODUCT_PRINCIPLES.md` (the 10 rules verbatim), `DESIGN_TOKENS.md`, `LOCALIZATION.md`, `OFFLINE_FIRST.md` (with the source-of-truth rule + TODO list).
- **Done when:** tree matches setup-prompt §4; docs cover §5, §6, §18, §19, §23.

## Phase 1 — Shared KMP core
3. Domain models + enums (§16): Language, WorkoutDiscipline, WorkoutIntensity, SessionStatus, UserProfile, TrainingSession, SessionSet, WhyConcept, SwapProposal, NotificationCadence, ExplanationDensity, SwapProposalStatus, plus the remaining model files in the tree (AthletePersona, TrainingPlan/Week, WhyConcept, OfflineStatus).
4. Design-token constants (`design/AtlanColors/Typography/Spacing/Radii/Motion.kt`).
5. Repository interfaces (`domain/repository/*`) + fake in-memory implementations (`data/fake/*`).
6. Seed data (`data/seed/*`): seed user, training plan with today's Pool/Threshold/1,000m session marked `offlineAvailable = true`, Why concepts (Threshold w/ Maglischo citation), localization seed.
7. Sync contracts only (`data/sync/SyncQueueItem/SyncOperation/SyncState.kt`) — types, no backend.
8. Localization (`localization/*`): `LocalizedStringKey`, `AtlanCopy`, `EnglishCopy`, `SpanishCopy` keyed per §19.
9. Use cases (§17): CompleteOnboarding, GetTodayDashboard, GetTodaySession, GetWhyConcept, ProposeSessionSwap, AcceptSessionSwap, CompleteWorkoutSet. (The spec's `ToggleWetMode` was superseded by the richer `StartWorkoutTimerUseCase` + `WorkoutTimerState` machine — Wet Mode is now reached by navigation, not a boolean toggle — so the shipped set is still 8 use cases but with that substitution.)
10. Presentation state holders (`presentation/*`): Dashboard/Onboarding/SessionDetail/WetMode/WhyModal/SessionSwapper state.
11. `androidMain`/`iosMain` platform expect/actual (`AndroidPlatform.kt`, `IOSPlatform.kt`).
- **Done when:** shared module compiles standalone.

## Phase 2 — Shared tests (`commonTest`)
12. `ProposeSessionSwapUseCaseTest`, `GetTodayDashboardUseCaseTest`, `LocalizationParityTest` per §22 assertions.
- **Done when:** all three pass.

## Phase 3 — Android app (Compose)
13. App scaffold: `MainActivity`, `AtlanAndroidApp`, KMP wiring to fakes/seed on launch.
14. Theme/design: `AtlanTheme`, `AtlanColorScheme`, `AtlanType` (Material 3 only as low-level base; Atlan visual language on top).
15. Custom components: `AtlanButton`, `AtlanPill`, `AtlanInfoButton`, `AtlanMetricChip`, `AtlanSessionCard`, `AtlanBottomSheet`, `WetModeActionZone`.
16. Navigation: `AtlanNavGraph` + `AtlanRoute` (language, welcome, calibration, tunedSummary, dashboard, sessionDetail, wetMode, settings; Why + Swapper as modals).
17. Screens (verbatim copy): onboarding (Language/Welcome/Calibration/TunedSummary), Dashboard, SessionDetail, WetMode, WhyModalSheet, SessionSwapperSheet, Settings.
- **Done when:** launches to Language Selection; full nav path works; TODOs for WorkManager/Room/notifications/Health Connect/haptics/TalkBack.

## Phase 4 — iOS app (SwiftUI)
18. App entry `AtlanPerformanceApp.swift` + `SharedBridge/` (SharedContainer, SharedMappers) linking KMP framework.
19. Design: `AtlanColors/Typography/Spacing/Components.swift` (Dynamic Type-friendly, stubbed fonts).
20. Navigation: `AppRoute` + `AppCoordinator` over `NavigationStack`; native `.sheet` for Why + Swapper.
21. Screens mirroring Android set under `Screens/Onboarding|Dashboard|Session|Settings`.
- **Done when:** launches to Language Selection; full nav path works; TODOs for BackgroundTasks/UserNotifications/SwiftData-or-SQLDelight/HealthKit/VoiceOver/haptics.

## Phase 5 — Verify against acceptance gate
22. Walk the §24 checklist on both platforms. Confirm Wet Mode writes completion locally first and exposes accessible Complete/Pause/Exit actions. Grep the codebase for forbidden patterns (streak, leaderboard, missed, behind, flame, trophy, badge, red-failure) — must be absent.
23. Produce the §26 final output: files-created summary, Android run commands, iOS Xcode steps, known limitations, next milestone (per framework-doc Phase 2: local DB + offline session core).

## Risks / watch-items
- KMP→Xcode framework integration may force minor `iosApp` layout differences — preserve conceptual separation (allowed by §4).
- Don't let the broader docs (Session Start, Post-Session, History, WeeklyArcDetail, telemetry SDK) pull initial scope wider — they are future phases.
- Keep Coral usage rare; verify no red/disruption coloring slips in.

## Deliverables status
- [x] `CLAUDE.md` — created.
- [x] `ACTION_PLAN.md` — this file.
- [x] Phase 0 — repo skeleton + 6 root docs (`atlan-performance-mobile/`).
- [x] Phase 1 — shared KMP core (59 commonMain files: models, repos, fakes, seed, sync, i18n, 8 use cases, presentation, design tokens, platform expect/actual).
- [x] Phase 2 — 3 shared `commonTest` tests (swap-without-shame, dashboard shape, localization parity).
- [x] Phase 3 — Android Compose app (18 files: theme, components, route nav, all 10 screens).
- [x] Phase 4 — iOS SwiftUI app (19 files: tokens, components, coordinator nav, all 10 screens, KMP bridge).
- [x] Phase 5 (partial) — static forbidden-term scan passed (no shame/pressure copy in UI strings).
- [x] Phase 5 — **shared core compiles and all 3 `commonTest` suites pass via CLI** (`./gradlew :shared:jvmTest`, JDK-17-only — no IDE). Added a desktop `jvm()` target + committed Gradle wrapper to make this verifiable without Android Studio/Xcode.
- [x] Phase 5 — **Android `:androidApp:compileDebugKotlin` compiles** (SDK present; `local.properties` → `sdk.dir`).
- [x] Phase 5 — **iOS app builds, launches, and the full onboarding→dashboard→modal flow runs on the iPhone 17 simulator** (Xcode 26.5). Verified screens: Language Selection, Welcome, Calibration (selectable rows), Tuned Summary, Today Dashboard, Settings, Why Modal (science + empty state), Session Swapper.
- [x] Phase 5 — **Design-patterns refinement pass** (`docs/atlan-mobile-design-patterns.md`): added pressed/disabled/loading button states, 44pt/48dp touch targets, color-not-alone selection (checkmark + selected trait), `AtlanBackButton` (Android), co-equal bilingual headings, and a11y labels — applied to both platforms.
- [x] **Bug fix (found during the run):** Why Modal + Session Swapper crashed on open — `@EnvironmentObject AppCoordinator/SharedContainer` was not inherited across the SwiftUI `.sheet` boundary. Fixed by re-injecting both environment objects on each sheet's content in `AtlanPerformanceApp.swift`. Both modals now open correctly.
- [x] **No-streak fix:** Dashboard "Consistency" chip changed from a breakable "72 days" day-count to a qualitative "Steady · Sustainable pattern" (a consecutive-day counter conflicts with principle #1 + the no-streak rule). In `GetTodayDashboardUseCase`.
- [x] **4-Set Threshold Timer (`atlan-4-set-timer-mobile-interactions.md`):** replaced the static Wet Mode stepper with a pure, monotonic, drift-free timer state machine (`WorkoutTimerState` + `StartWorkoutTimerUseCase`, 6 new `commonTest` cases). Both Wet Mode screens rebuilt with a live M:SS countdown, pause/resume accounting, overtime (no auto-complete), early-complete confirmation (>10s), auto-advance, and a session-complete summary. Verified Set 1→4→summary on iPhone 17 sim + Pixel 7 emulator. Persistence/real-sync left as TODO anchors (out of initial scope). Fixed a latent iOS tap/drag conflict (`.gesture` → `.simultaneousGesture`).
- [x] **Active-session safety + flow (`atlan-app-screen-inventory-and-scope.md` P0 gaps):** extended the timer machine with a **REST phase** (rest countdown + next-set preview + Skip Rest, auto-starts at zero), **Exit/End-session confirmation** (back/long-swipe/End never discards silently → "End this session?" → partial summary), and a **richer Session Summary** (set-by-set ✓/○ breakdown + total elapsed). 4 more `commonTest` cases (rest entry/skip, rest auto-start, end-early partial count, total elapsed). Verified on both platforms: live timer → complete → Rest → End → partial summary. Backend-dependent inventory items (auth, real sync, persistence-backed resume, history, health, analytics) remain deferred per CLAUDE.md.
- [x] **Prep + onboarding-polish batch (inventory P0, no backend):** added a **Workout Prep** screen (readiness summary — type/sets/pace/est. time + offline-ready + warm-up reminder → Begin) between Session Detail and Wet Mode (new `AppRoute.workoutPrep` / `AtlanRoute.WORKOUT_PREP`; iOS file registered in the `.xcodeproj`); a one-time **Gesture Tutorial** coach mark on first Wet Mode entry (in-memory seen-flag; TODO persist); and a reusable **Generic Error Screen** (`AtlanErrorView` / `AtlanErrorScreen` — calm, no-red, Retry + safe exit) wired as the Session Detail load-failure fallback. Verified Prep + tutorial show/dismiss on iPhone 17 sim + Pixel 7 emulator. Error path is wired but unreachable with fake repos.
- [x] **Functional Settings (inventory P1, no backend):** Settings is now interactive — a **live Language switch** (English | Español segmented control that re-renders the whole app immediately; verified propagating to the Dashboard's "Ajustar hoy"), a **Haptics** toggle (gates all Wet Mode haptics), and a **Keep-screen-awake** toggle (iOS `isIdleTimerDisabled` / Android `View.keepScreenOn` during Wet Mode). Preferences held in `AppCoordinator` (iOS) / nav state (Android); TODO persist. Notification cadence / explanation density / offline cache / version remain display rows. Verified the live EN↔ES switch on both platforms.
- [x] **Sync-status surfacing (inventory P0, UI stub):** the session summary now surfaces the offline-resilience states using `OfflineStatus` — **"Pending sync"** ("Saved offline. We'll sync when you're back online.") with a **Retry sync** affordance that runs a brief "Syncing…" then lands on **"Saved locally"** ("Sync failed — your results are safe. Retry when you're back online."). Calm, never red; data always safe. Verified the Pending → Syncing → Saved-locally flow on both platforms. TODO: real sync-queue drain (no backend in this milestone).
- [x] **Preference persistence (platform storage adapter — in CLAUDE.md's platform layer, not a DB):** non-sensitive preferences (language, haptics, keep-awake, tutorial-seen) now survive relaunch — iOS via `UserDefaults` (`AppCoordinator` didSet/init), Android via `SharedPreferences` (`AtlanNavGraph`). Verified end-to-end on both: write → durable across restart → read (e.g. the gesture tutorial shows once per install, not per launch). **Found & fixed an Android issue:** `SharedPreferences.apply()` writes weren't flushing observably on the emulator → switched to `commit()` (synchronous; appropriate for these tiny, infrequent writes). TODO: migrate to a shared `PreferencesRepository` when the real storage layer lands; encrypted storage remains a separate TODO for sensitive data.
- [x] **Timer preference (rest between sets) — functional, no model change:** added a live **"Rest between sets"** control (30s | 45s | 60s segment) to Settings on both platforms. It feeds `StartWorkoutTimerUseCase`'s new `restDurationMs` parameter (no model surgery — `WorkoutTimerState.restDurationMs` already existed), so the next Wet Mode session's REST countdown uses the chosen value. Persisted alongside the other prefs (`UserDefaults` / `SharedPreferences`). Verified `:shared:jvmTest` + `:androidApp:compileDebugKotlin` green. (Per-set *work* duration is still a 105s default — that one genuinely needs a `SessionSet.durationMs` field, left as a TODO.)
- [x] **Branded launch / splash (native, both platforms):** a brief Foam-surface splash with the `atlan` wordmark + Coral rule that fades in then auto-advances (~0.8s) to Language Selection. Android: `AtlanSplash` composable gated in `AtlanAndroidApp` (Activity `windowBackground` already FoamWarm, so no white/black cold-start flash). iOS: `AtlanSplashView` overlay in the root `ZStack` of `AtlanPerformanceApp.swift` (defined in-file → no `.xcodeproj` entry needed), dismissed via a `.task` timer. No artificial input block; language-agnostic (no copy). Android verified via compile; **iOS pending sim verification**. TODO(optional): Android 12 `SplashScreen` API; set the iOS generated launch screen's background to Foam.
- [x] **Localization gap closed (follow-up task):** the Wet Mode timer surface (coach mark, active/rest timer, summary, two-zone actions + a11y labels, confirmation dialogs, sync-status block) is now fully bilingual on both platforms via the shared `AtlanCopy` / `LocalizedStringKey` layer — Android `WetModeScreen` now takes a `language` parameter. (The numeric iOS VoiceOver "N minutes M seconds remaining" label remains English — number-word grammar is a separate low-priority TODO.)
- [ ] **Units preference — deferred pending a product decision.** All distances/paces are pre-formatted strings (`"100m"`, `"1:35"`) with no numeric fields, and pool lengths (25m vs 25yd) don't convert. A genuine Units control needs numeric model fields + a formatter + per-unit seed data (data work), so a Metric/Imperial toggle was **not** shipped as a dead control. Awaiting a decision on scope (see notes).
- [x] **No-backend P1 screens batch (both platforms, all verified green):**
  - **Workout Plan List** — new screen showing the current week's sessions (`GetTrainingPlanUseCase` → `getCurrentWeek()`; seed expanded with a completed Recovery + an upcoming Endurance session). Completed/upcoming are calm equal states; only today's session is actionable (→ Session Detail). Includes a **calm empty state** (covers the inventory's "no content" / no-connection-empty item by construction). Reached via a "View this week's plan" affordance on the dashboard. Routes: `AtlanRoute.WORKOUT_PLAN` / `AppRoute.workoutPlan`.
  - **How It Works** — calm primer on set-based threshold training; the inventory's separate **Pace Explanation** is folded in as its own section (so two checklist items are covered). Reached from a new tappable Settings row. Routes: `HOW_IT_WORKS` / `.howItWorks`.
  - **Save Notes / Reflection** — added optional **perceived-effort** chips (Easy/Moderate/Hard · Fácil/Moderado/Duro) to the Session Summary. Tappable (no keyboard — appropriate for a post-swim/wet context), color-not-alone (✓ + Tide fill), local-only (TODO persist with results).
  - **Consciously skipped (redundant / out of no-backend scope), documented not built:** App Loading/Bootstrap (the branded Splash already covers the cold-start gap); standalone Safety/Readiness Check (Workout Prep already carries the readiness message + warm-up reminder); Profile Setup (overlaps Calibration, adds onboarding friction the principles warn against, and its Units field is deferred); a standalone No-Connection screen (folded into the Plan List empty state + the existing Generic Error surface).
  - **Verified:** `:shared:jvmTest` (4 suites) + `:androidApp:compileDebugKotlin` green; **iOS `xcodebuild` BUILD SUCCEEDED** for the simulator after `xcodegen generate` picked up the two new Swift files (directory-globbed sources — no `.pbxproj` surgery).

## Verified builds (current environment)
- **Shared KMP core:** `./gradlew :shared:jvmTest` — GetTodayDashboard, ProposeSessionSwap, LocalizationParity (7 tests, 0 failures).
- **Android:** `./gradlew :androidApp:assembleDebug` builds; installed + ran the full flow on a Pixel 7 emulator (SDK at `~/Library/Android/sdk`, `local.properties` → `sdk.dir`). Verified: Language Selection, Welcome, Calibration (selectable rows + selected state), Tuned Summary, Dashboard, Why bottom sheet (science content), Session Swapper bottom sheet, Settings.
- **iOS:** builds + runs on the iPhone 17 simulator (Xcode 26.5); the `.xcodeproj` exists and links the `Shared` framework via the run-script phase. Same full flow exercised by hand, incl. Why Modal + Session Swapper.

## Known limitations (this environment)
- Some KMP→Swift generated signatures (async suspend wrappers, enum case names) are centralized in `SharedContainer.swift` if they need adjustment after a framework rebuild.
- Most repositories are still fake in-memory (profile/plan/session/why/swap); the **sync queue is now real SQLDelight** (see below). No remote sync or notifications yet (by design).

## Offline persistence core (Phase 2 — started)
- [x] **SQLDelight foundation, verified on all three platforms.** Wired the `app.cash.sqldelight` plugin + drivers into the version catalog and shared `build.gradle.kts`; declared it at the root with `apply false` to stop its transitive Kotlin Gradle plugin from **downgrading** KGP (that was the one real snag — it broke the `compilerOptions { jvmTarget }` DSL until pinned). Schema in `SyncQueue.sq` → generated `AtlanDatabase`. Cross-platform `expect/actual DatabaseDriverFactory` (Android `AndroidSqliteDriver(context)`, iOS `NativeSqliteDriver`, JVM `JdbcSqliteDriver(IN_MEMORY)`); iOS links system `libsqlite3` (`-lsqlite3` in `project.yml` — the static framework leaves `sqlite3_*` undefined otherwise).
- [x] **Sync queue migrated to durable storage** (`SqlDelightSyncQueueRepository` replaces `FakeSyncQueueRepository` in `AtlanShared`; `AtlanShared(databaseDriverFactory)` now takes a driver, provided by each platform). `markSynced` is the drain primitive. Payload `Map<String,String>` encoded with RS/US separators (dependency-free).
- [x] **Verified:** `:shared:jvmTest` (6 tests — 4 original + new `SyncQueuePersistenceTest` proving an item is readable from a *second* repo on the same DB + drops out of `pending()` once synced); `:androidApp:compileDebugKotlin`; **iOS `xcodebuild` BUILD SUCCEEDED** (sim) with the native SQLite driver linked.

## Next recommended milestone
Continue Phase 2: migrate the remaining repositories (profile/plan/session) to SQLDelight following the sync-queue pattern; **persist active-session/timer state** to unblock Returning-User Resume + Session Recovery; add the sync-queue **drain workers** (WorkManager / BackgroundTasks) + a remote API; offload DB calls to a background dispatcher (`kotlinx-coroutines-core`).
