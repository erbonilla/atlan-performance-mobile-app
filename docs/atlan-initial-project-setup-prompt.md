# Atlan Performance — Initial Mobile Project Setup Prompt

Use this prompt as the starting instruction for Cursor, Codex, Claude Code, or a senior mobile engineering team.

---

## Prompt

You are acting as a senior mobile architect, senior iOS engineer, senior Android engineer, and senior UI/UX systems designer.

Set up the initial mobile codebase for **Atlan Performance**, a launched native iOS/Android product.

Atlan Performance is an offline-first, bilingual ES/EN coaching product for executive endurance athletes. It is an emotionally intelligent endurance-training coach for high-performing users whose lives frequently disrupt their training plans. The product does not punish missed sessions. It adapts the plan, explains training science only when asked, and works poolside without connectivity.

The app must feel native on iOS and Android. Do not build this as a webview, Ionic app, Expo app, or generic cross-platform UI shell.

---

# 1. Framework decision

Use this architecture:

- Shared core: **Kotlin Multiplatform**
- iOS app: **SwiftUI**
- Android app: **Kotlin + Jetpack Compose**

Shared responsibilities:

- domain models
- training-plan logic
- session-swap logic
- offline-first repository interfaces
- seed data
- localization keys
- sync contracts
- validation rules
- design token definitions as shared constants where useful

Platform-specific responsibilities:

- native navigation
- native UI rendering
- haptics
- notifications
- background work
- accessibility semantics
- platform-specific storage adapters where needed

Do not use Compose Multiplatform for the iOS UI in this initial setup. The requirement is native SwiftUI on iOS and native Compose on Android.

---

# 2. Repository goal

Create a clean initial repository that can compile and run a basic Atlan mobile shell on both platforms.

The first setup should not attempt to build the full product. It should establish:

- architecture
- navigation
- design system
- shared domain layer
- mock offline data
- starter screens
- starter tests
- product documentation

The codebase should be production-oriented, not a throwaway prototype.

---

# 3. Project name and package names

Use:

```text
Product name: Atlan Performance
Repository folder: atlan-performance-mobile
Shared module package: com.atlan.performance.shared
Android package: com.atlan.performance.android
iOS app name: AtlanPerformance
iOS bundle identifier placeholder: com.atlan.performance.ios
```

---

# 4. Expected top-level structure

Create this structure:

```text
atlan-performance-mobile/
  README.md
  ARCHITECTURE.md
  PRODUCT_PRINCIPLES.md
  DESIGN_TOKENS.md
  LOCALIZATION.md
  OFFLINE_FIRST.md

  settings.gradle.kts
  build.gradle.kts
  gradle/
    libs.versions.toml
    wrapper/

  shared/
    build.gradle.kts
    src/
      commonMain/
        kotlin/
          com/atlan/performance/shared/
            AtlanShared.kt

            design/
              AtlanColors.kt
              AtlanTypography.kt
              AtlanSpacing.kt
              AtlanRadii.kt
              AtlanMotion.kt

            domain/
              model/
                Language.kt
                UserProfile.kt
                AthletePersona.kt
                TrainingPlan.kt
                TrainingWeek.kt
                TrainingSession.kt
                SessionSet.kt
                SessionStatus.kt
                WorkoutDiscipline.kt
                WorkoutIntensity.kt
                WhyConcept.kt
                SwapProposal.kt
                OfflineStatus.kt
                NotificationCadence.kt
                ExplanationDensity.kt

              repository/
                UserProfileRepository.kt
                TrainingPlanRepository.kt
                SessionRepository.kt
                WhyConceptRepository.kt
                SwapProposalRepository.kt
                SyncQueueRepository.kt

              usecase/
                CompleteOnboardingUseCase.kt
                GetTodayDashboardUseCase.kt
                GetTodaySessionUseCase.kt
                GetWhyConceptUseCase.kt
                ProposeSessionSwapUseCase.kt
                AcceptSessionSwapUseCase.kt
                CompleteWorkoutSetUseCase.kt
                ToggleWetModeUseCase.kt

            data/
              seed/
                SeedUser.kt
                SeedTrainingPlan.kt
                SeedWhyConcepts.kt
                SeedLocalization.kt

              fake/
                FakeUserProfileRepository.kt
                FakeTrainingPlanRepository.kt
                FakeSessionRepository.kt
                FakeWhyConceptRepository.kt
                FakeSwapProposalRepository.kt
                FakeSyncQueueRepository.kt

              sync/
                SyncQueueItem.kt
                SyncOperation.kt
                SyncState.kt

            localization/
              LocalizedStringKey.kt
              AtlanCopy.kt
              EnglishCopy.kt
              SpanishCopy.kt

            presentation/
              DashboardState.kt
              OnboardingState.kt
              SessionDetailState.kt
              WetModeState.kt
              WhyModalState.kt
              SessionSwapperState.kt

      commonTest/
        kotlin/
          com/atlan/performance/shared/
            ProposeSessionSwapUseCaseTest.kt
            GetTodayDashboardUseCaseTest.kt
            LocalizationParityTest.kt

      androidMain/
        kotlin/
          com/atlan/performance/shared/platform/
            AndroidPlatform.kt

      iosMain/
        kotlin/
          com/atlan/performance/shared/platform/
            IOSPlatform.kt

  androidApp/
    build.gradle.kts
    src/
      main/
        AndroidManifest.xml
        java/com/atlan/performance/android/
          MainActivity.kt
          AtlanAndroidApp.kt

          navigation/
            AtlanNavGraph.kt
            AtlanRoute.kt

          design/
            AtlanTheme.kt
            AtlanColorScheme.kt
            AtlanType.kt
            AtlanComponents.kt

          screen/
            onboarding/
              LanguageSelectionScreen.kt
              WelcomeScreen.kt
              CalibrationScreen.kt
              TunedSummaryScreen.kt

            dashboard/
              TodayDashboardScreen.kt

            session/
              SessionDetailScreen.kt
              WetModeScreen.kt
              WhyModalSheet.kt
              SessionSwapperSheet.kt

            settings/
              SettingsScreen.kt

  iosApp/
    AtlanPerformance/
      AtlanPerformanceApp.swift

      Navigation/
        AppRoute.swift
        AppCoordinator.swift

      Design/
        AtlanColors.swift
        AtlanTypography.swift
        AtlanSpacing.swift
        AtlanComponents.swift

      Screens/
        Onboarding/
          LanguageSelectionView.swift
          WelcomeView.swift
          CalibrationView.swift
          TunedSummaryView.swift

        Dashboard/
          TodayDashboardView.swift

        Session/
          SessionDetailView.swift
          WetModeView.swift
          WhyModalView.swift
          SessionSwapperView.swift

        Settings/
          SettingsView.swift

      SharedBridge/
        SharedContainer.swift
        SharedMappers.swift
```

Adjust exact generated Xcode project structure if the Kotlin Multiplatform template requires a slightly different layout, but preserve the conceptual separation.

---

# 5. Product principles to encode in docs and code comments

Create `PRODUCT_PRINCIPLES.md` with these rules:

1. Life interruption is the default state, not the exception.
2. The app absorbs disruption silently instead of marking it red.
3. Scientific depth lives one tap below the surface.
4. The dashboard answers only:
   - What should I do today?
   - Is the week still on track?
5. No streak pressure.
6. No leaderboard pressure.
7. No red missed-session shame states.
8. No “you are behind” language.
9. Spanish is not a translation layer; it is a first-class product language.
10. Wet Mode must work offline and be operable with wet hands.

---

# 6. Design tokens

Add these design tokens in shared documentation and platform implementations.

## Colors

```text
Abyss:        #0B2A3C
AbyssDeep:    #061A26
Tide:         #0E8A9A
TideDeep:     #0A6F7D
TideSoft:     #BFE0E5
TidePale:     #DDEEF1
Coral:        #FF6A3D
CoralBright:  #FF7E50
CoralDeep:    #E55428
Foam:         #ECF7F8
FoamWarm:     #F4FAFB
Paper:        #FBFCFC
```

## Color usage rules

- Abyss and AbyssDeep are the primary dark brand surfaces.
- Foam and Paper are the primary light surfaces.
- Tide is used for science, calm progress, and “Why” affordances.
- Coral is rare. Use Coral only for productive action, completion, and high-signal accents.
- Do not use Coral for errors, guilt, or missed-session states.
- Do not create red failure states for training disruption.

## Typography

Use platform-native implementation, but preserve this hierarchy:

- Display: calm editorial display type.
- Body: high-legibility sans.
- Numeric: tabular numeric style where available.
- Wet Mode: extra-large, high-contrast numbers.

iOS:

- Use SwiftUI `Font` wrappers.
- Stub custom font names for later asset installation.
- Use Dynamic Type-friendly text styles.

Android:

- Use Compose typography tokens.
- Stub custom font resources for later asset installation.
- Use scalable text sizes.

## Spacing

Create tokenized spacing:

```text
xs: 4
sm: 8
md: 12
lg: 16
xl: 24
2xl: 32
3xl: 48
4xl: 64
```

## Radii

```text
sm: 4
md: 8
lg: 16
sheet: 24
phone: 32
pill: 999
```

---

# 7. Initial screens to scaffold

Create native screens on both platforms for:

1. Language Selection
2. Welcome
3. Calibration
4. Tuned Summary
5. Today Dashboard
6. Session Detail
7. Why Modal
8. Session Swapper
9. Wet Mode
10. Settings

Use real Atlan starter copy from the product concept.

---

# 8. Onboarding flow

## 8.1 Language Selection

Purpose:

Let users choose English or Spanish before account creation.

Layout:

- Light background: FoamWarm
- Centered wordmark: `atlan`
- Coral underline
- Prompt:
  - English: `Choose your language`
  - Spanish: `Elige tu idioma`
- Two equal buttons:
  - `English`
  - `Español`

Rules:

- No default selected.
- No flag icons.
- No IP geolocation language choice.
- Persist language selection locally.
- After selection, navigate to Welcome.

## 8.2 Welcome

English:

```text
Eyebrow: Welcome
Title: Built for athletes with lives.
Body: Atlan adapts the plan around your week — not the other way around. Before we start, a few short questions to tune the app to how you actually train.
CTA: Begin
```

Spanish:

```text
Eyebrow: Bienvenida
Title: Hecho para quienes entrenan entre todo lo demás.
Body: Atlan adapta el plan a tu semana — no al revés. Antes de empezar, unas preguntas cortas para afinarla a tu manera de entrenar.
CTA: Comenzar
```

## 8.3 Calibration

Purpose:

Tune coaching posture without scoring or judging the user.

Layout:

- Back arrow
- Progress pill: `2 of 4`
- Label: `A few short questions`
- Title: `No right answers. Just calibration.`
- Question card
- Three options
- CTA: `Continue`

Example question:

```text
What pulls you to training most weeks?

- The space it gives me from work
- The pursuit of getting better
- The structure for my week
```

Rules:

- Selected option uses TidePale and Tide.
- No “good athlete / bad athlete” language.
- Store answers as settings, not scores.

## 8.4 Tuned Summary

English:

```text
Eyebrow: Tuned to your week
Title: Here's how I'll work with you.

Adaptive scheduling
On. I'll absorb disruption before it becomes a missed session.

Depth on demand
On. The "why" lives one tap from the term, with sources.

Notifications
Minimal. Mornings and Sunday evenings only.

First session · tomorrow
Pool · Threshold · 1,000m

CTA: See your first session
```

Rules:

- Each inferred setting should be modeled so it can become editable later.
- Navigate to Today Dashboard after CTA.

---

# 9. Today Dashboard

Create a calm native dashboard.

Layout:

Header:

- Brand: `atlan`
- Date: `Tuesday · May 26`

Today hero card:

- Label: `Today · Pool`
- Main value: `1,000m`
- Detail: `Threshold · ~30 min`
- Small Tide `i` button beside `Threshold`
- CTA: `Start session →`

Weekly arc card:

- Label: `Weekly arc`
- Status: `Load on track`
- One simple calm line chart placeholder
- Coral marker for current week

Metric chips:

1. Load — `On track` — `Week 18 of 24`
2. Consistency — `72 days` — `Sustainable pattern`
3. Recovery — `Good` — `Sleep 7h32 avg`

Each metric chip includes a Tide `i` affordance.

Do not add:

- streaks
- peer ranking
- leaderboards
- notification badges
- red missed-session rows
- “behind plan” messaging
- multi-tab dashboard navigation

---

# 10. Session Detail

Layout:

```text
Today · Tuesday, May 26

Pool · Threshold
1,000m · ~30 minutes

Warm-up
400m easy
~8 min

Main set
4 × 100m at threshold [i]
~14 min

CTA: Start session
```

Interaction:

- Tapping the `i` beside threshold opens the Why Modal.
- Tapping Start Session opens Wet Mode for this initial build.

---

# 11. Why Modal

Create a native bottom sheet on Android and sheet-style presentation on iOS.

Content:

```text
Eyebrow: Depth · Threshold
Title: Why threshold

Threshold intervals develop lactate clearance — the rate at which your body processes accumulating lactate during sustained efforts. Repeating this stimulus at the boundary between sustainable and unsustainable pace raises the work rate you can hold for an extended duration.

Mechanism
- Increased mitochondrial density in slow-twitch fibers
- Elevated capillarization around working muscle
- Improved enzymatic activity for aerobic energy production

These adaptations distinguish trained endurance athletes from untrained populations. The standard prescription is 4–6 repetitions of 5–15 minutes at threshold pace, with short recoveries — the partial recovery is the point.

Reference
Maglischo, E. W. (2003). Swimming Fastest (2nd ed.). Champaign, IL: Human Kinetics, pp. 348–356.
```

Rules:

- Use Tide for the concept label and mechanism block.
- Do not use motivational copy inside the science explanation.
- Include a close affordance.
- Ensure the sheet is scrollable.

---

# 12. Session Swapper

Create a sheet/modal that can be opened from the dashboard for now.

Purpose:

Show how Atlan adapts when a session no longer fits the user’s day.

Content:

```text
Tag: Session Adjusted

Original:
90-min pool · threshold

Replacement:
45-min Vasa erg · threshold equivalent

Weekly load:
Still on track.

Affirmation:
Smart call — protect the week.

Primary CTA:
Accept swap

Secondary CTA:
Skip today
```

Rules:

- Neither action is failure.
- Accepting the swap updates the dashboard state locally.
- Skipping today updates the plan locally without shame copy.
- Do not show red warning states.

---

# 13. Wet Mode

Create a full-screen native workout mode.

Visual rules:

- Background: AbyssDeep
- Text: Foam
- Productive action: CoralBright
- Very large type
- No small controls
- No precision tap dependency

Content:

```text
Offline · Cached

Set 2 of 4 · Threshold

100m

Threshold

1:32
Target pace 1:35
```

Bottom action zones:

- Left half:
  - Arrow left
  - `Pause`
  - `Swipe Left`
- Right half:
  - Arrow right
  - `Complete`
  - `Swipe Right`

Gesture behavior:

- Swipe right completes the current set.
- Swipe left pauses the session.
- Long swipe down exits Wet Mode.
- Use a swipe threshold large enough to prevent accidental activation.
- Add haptic feedback on successful complete/pause where available.
- Write completion locally first.

Initial setup can implement gesture detection simply, but leave clear TODOs for production-grade gesture thresholds and waterproof-pouch testing.

Accessibility:

- Provide accessible actions:
  - `Complete set`
  - `Pause set`
  - `Exit Wet Mode`
- Do not make swipe the only accessible action.

---

# 14. Settings

Create a simple Settings screen with:

- Language: English / Español
- Notification cadence: Minimal
- Explanation density: Standard
- Offline cache: Today + 7 days
- About Atlan

No account system is needed for the first setup.

---

# 15. Navigation

Implement native navigation separately per platform.

Android:

- Use Navigation Compose or a simple Compose navigation layer.
- Routes:
  - language
  - welcome
  - calibration
  - tunedSummary
  - dashboard
  - sessionDetail
  - wetMode
  - settings

iOS:

- Use SwiftUI NavigationStack.
- Create an `AppCoordinator` or equivalent simple route state.
- Routes:
  - language
  - welcome
  - calibration
  - tunedSummary
  - dashboard
  - sessionDetail
  - wetMode
  - settings

Modal routes:

- Why Modal
- Session Swapper

---

# 16. Shared domain models

Create shared Kotlin domain models.

Minimum models:

```kotlin
enum class Language {
    EN,
    ES
}

enum class WorkoutDiscipline {
    SWIM,
    BIKE,
    RUN,
    STRENGTH,
    VASA
}

enum class WorkoutIntensity {
    EASY,
    AEROBIC,
    THRESHOLD,
    VO2,
    RECOVERY
}

enum class SessionStatus {
    PLANNED,
    SWAPPED,
    COMPLETED,
    SKIPPED,
    PAUSED
}

data class UserProfile(
    val id: String,
    val language: Language,
    val onboardingCompleted: Boolean,
    val notificationCadence: NotificationCadence,
    val explanationDensity: ExplanationDensity
)

data class TrainingSession(
    val id: String,
    val scheduledAtIso: String,
    val discipline: WorkoutDiscipline,
    val intensity: WorkoutIntensity,
    val title: String,
    val distanceLabel: String,
    val durationEstimateLabel: String,
    val status: SessionStatus,
    val offlineAvailable: Boolean,
    val sets: List<SessionSet>
)

data class SessionSet(
    val id: String,
    val order: Int,
    val label: String,
    val distanceLabel: String,
    val targetPaceLabel: String?,
    val completed: Boolean
)

data class WhyConcept(
    val id: String,
    val conceptKey: String,
    val language: Language,
    val eyebrow: String,
    val title: String,
    val body: String,
    val mechanisms: List<String>,
    val reference: String
)

data class SwapProposal(
    val id: String,
    val originalSessionId: String,
    val replacementTitle: String,
    val replacementDetail: String,
    val weeklyLoadStatus: String,
    val status: SwapProposalStatus
)
```

Also create:

```kotlin
enum class NotificationCadence {
    MINIMAL,
    STANDARD,
    DETAILED
}

enum class ExplanationDensity {
    LIGHT,
    STANDARD,
    DEEP
}

enum class SwapProposalStatus {
    PROPOSED,
    ACCEPTED,
    REJECTED,
    EXPIRED
}
```

---

# 17. Shared use cases

Implement simple versions of:

```text
CompleteOnboardingUseCase
GetTodayDashboardUseCase
GetTodaySessionUseCase
GetWhyConceptUseCase
ProposeSessionSwapUseCase
AcceptSessionSwapUseCase
CompleteWorkoutSetUseCase
ToggleWetModeUseCase
```

For this first setup, repositories can be fake in-memory repositories. However, design them so they can later be replaced by real local database repositories.

---

# 18. Offline-first setup

Create `OFFLINE_FIRST.md` and document this rule:

> Local data is the app’s source of truth. Network sync is secondary. The app must be able to read today’s session, start Wet Mode, complete sets, and view cached Why content without connectivity.

Initial implementation:

- Use fake local repositories seeded on app launch.
- Mark today’s session as `offlineAvailable = true`.
- Simulate sync queue types but do not connect a backend yet.
- Add TODOs for:
  - SQLDelight or platform-specific database adapters
  - background sync
  - conflict resolution
  - remote API integration
  - auth
  - encrypted local storage for sensitive user settings

---

# 19. Localization

Create `LOCALIZATION.md`.

Rules:

- English and Spanish are first-class.
- No flag icons.
- Do not auto-select language by locale.
- Long Spanish strings wrap; they do not shrink.
- Product voice must preserve emotional parity, not literal translation.

Initial copy keys:

```text
language.choose.english
language.choose.spanish
onboarding.welcome.eyebrow
onboarding.welcome.title
onboarding.welcome.body
onboarding.welcome.cta
calibration.title
calibration.subtitle
tuned.title
tuned.cta
dashboard.today.label
dashboard.startSession
session.whyThreshold.title
wetMode.offlineCached
wetMode.pause
wetMode.complete
swapper.accept
swapper.skipToday
```

Implement simple shared copy access in Kotlin, then map to native strings later.

---

# 20. Android implementation notes

Use:

- Kotlin
- Jetpack Compose
- Material 3 only as a low-level component base
- Atlan custom components instead of default Material visual language
- Compose Navigation or a small route-state implementation

Create:

- `AtlanTheme`
- `AtlanButton`
- `AtlanPill`
- `AtlanInfoButton`
- `AtlanMetricChip`
- `AtlanSessionCard`
- `AtlanBottomSheet`
- `WetModeActionZone`

Android-specific TODOs:

- WorkManager for sync
- Room or SQLDelight integration
- Notification channels
- Health Connect integration later, not now
- Haptic feedback for Wet Mode
- TalkBack semantics

---

# 21. iOS implementation notes

Use:

- Swift
- SwiftUI
- NavigationStack
- Native sheets
- Native haptics later
- KMP shared framework bridge

Create:

- `AtlanColors`
- `AtlanTypography`
- `AtlanButton`
- `AtlanPill`
- `AtlanInfoButton`
- `AtlanMetricChip`
- `AtlanSessionCard`
- `WhyModalView`
- `WetModeActionZone`

iOS-specific TODOs:

- BackgroundTasks for sync
- UserNotifications for Session Swapper
- SwiftData, Core Data, SQLite, or SQLDelight adapter later
- HealthKit integration later, not now
- VoiceOver actions for Wet Mode
- Haptics for complete/pause

---

# 22. Testing

Add initial shared unit tests.

## Test 1: `ProposeSessionSwapUseCaseTest`

Given:

- A disrupted pool threshold session

When:

- A swap is proposed

Then:

- Replacement is not framed as failure
- Weekly load is still on track
- No shame copy appears in the proposal

## Test 2: `GetTodayDashboardUseCaseTest`

Assert:

- Dashboard returns today’s session
- Dashboard contains no streak field
- Dashboard contains no leaderboard field
- Dashboard contains Why affordance data
- Dashboard contains weekly arc state

## Test 3: `LocalizationParityTest`

Assert:

- English and Spanish onboarding keys both exist
- Spanish welcome title is not empty
- Language options have equal priority
- Spanish strings are not treated as optional fallbacks

---

# 23. README requirements

Create a `README.md` with:

- Product description
- Framework decision
- Architecture diagram in text form
- Setup prerequisites
- How to run Android
- How to run iOS
- How shared code is structured
- What is implemented in the first setup
- What is intentionally not implemented yet

Include this architecture summary:

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

---

# 24. First milestone acceptance criteria

The first commit is acceptable when:

- The repo opens in Android Studio.
- The shared KMP module compiles.
- Android app launches to Language Selection.
- iOS app launches to Language Selection.
- User can navigate:
  - Language Selection
  - Welcome
  - Calibration
  - Tuned Summary
  - Dashboard
- Dashboard shows:
  - Today card
  - Weekly arc placeholder
  - Metric chips
- Session Detail opens from Dashboard.
- Why Modal opens from Session Detail.
- Wet Mode opens from Session Detail.
- Session Swapper can be opened from Dashboard.
- Shared fake repositories provide seed data.
- No shame copy appears anywhere.
- No streaks, leaderboards, peer ranking, or red missed-session UI exists.
- README and architecture docs exist.

---

# 25. Implementation discipline

Do not overbuild.

Do not add:

- backend
- auth
- subscription billing
- analytics SDK
- HealthKit
- Health Connect
- push notifications
- real sync
- production database
- complex calendar integrations

Only scaffold extension points and TODOs for these.

Prioritize:

- clean architecture
- native UI shell
- domain correctness
- offline-first mental model
- design tokens
- bilingual structure
- Wet Mode interaction foundation
- no-guilt product language

---

# 26. Final output expected from the implementation agent

After setting up the project, provide:

1. A summary of files created.
2. Any commands needed to run Android.
3. Any commands or Xcode steps needed to run iOS.
4. Known limitations.
5. Next recommended implementation milestone.

Proceed with the initial project setup.
