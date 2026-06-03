# Atlan Performance - Strong Build Prompt

Use this prompt with a product design agent, coding agent, or mobile engineering team to generate the implementation plan and first build of the mobile Atlan Performance product.

---

## Prompt

You are a senior mobile architect, senior iOS engineer, senior Android engineer, and senior UI/UX designer.

Design and scaffold **Atlan Performance**, a launched native iOS/Android mobile product for executive endurance athletes. Atlan is an emotionally intelligent endurance-training coach for high-performing athletes whose lives disrupt their training plans. It does not punish missed sessions. It adapts the plan, explains the science only when asked, and works poolside without connectivity.

## Framework decision

Recommend and use this architecture unless you identify a critical blocker:

- Kotlin Multiplatform for shared business logic, domain models, repositories, sync rules, i18n keys, telemetry definitions, and training-plan use cases.
- SwiftUI for the native iOS UI, with UIKit only where lower-level control is needed.
- Jetpack Compose for the native Android UI.
- Local-first persistence and sync queue on both platforms.
- Platform-specific adapters for haptics, notifications, background work, HealthKit, Health Connect, and accessibility actions.

Do not implement the primary product as a web app, PWA, React Native app, Flutter app, or fully shared UI unless explicitly asked. The product must follow native iOS and Android patterns.

## Product principles

1. Life interruption is the default state. The product absorbs disruption silently.
2. No shame states. Do not use streaks, leaderboards, red missed-session states, peer rankings, or "behind plan" pressure mechanics.
3. Depth on demand, not depth on display. Science lives behind contextual Why buttons.
4. Offline-first. Today's session, Wet Mode, and relevant Why content must work without network.
5. Bilingual parity. ES and EN are first-class product languages selected before account creation.
6. Native tactile quality matters. Wet Mode must feel reliable with wet hands, glare, and fatigue.

## Required deliverables

Produce the following:

1. Framework recommendation and reasoning.
2. Mobile app information architecture.
3. Native navigation model for iOS and Android.
4. Screen-by-screen implementation brief.
5. Design token system.
6. Component inventory.
7. Interaction specifications.
8. Offline-first data architecture.
9. Shared KMP module structure.
10. iOS-specific notes.
11. Android-specific notes.
12. App states and error states.
13. Accessibility requirements.
14. Build sequence.
15. Acceptance criteria.

## Required screens

Implement or specify these screens:

1. Language Selection
2. Welcome
3. Calibration
4. Tuned Summary
5. Today Dashboard
6. Daily Session Detail
7. Why Modal
8. Session Swapper
9. Session Start
10. Wet Mode
11. Post-Session Detail
12. Settings

## Required navigation

Use stack-first native navigation:

```text
AppRoot
  OnboardingStack
    LanguageSelection
    Welcome
    Calibration
    TunedSummary
    FirstSessionPreview

  MainStack
    TodayDashboard
    DailySessionDetail
    WeeklyArcDetail
    History
    Settings

  WorkoutStack
    SessionStart
    StandardWorkoutMode
    WetMode
    PauseState
    CompleteSession
    PostSessionDetail

  ModalLayer
    WhyModal
    SessionSwapper
    OfflineStatus
    NotificationPermission
    LanguageChange
```

## Required visual system

Use these color tokens:

```text
Abyss:        #0B2A3C
Abyss Deep:   #061A26
Tide:         #0E8A9A
Tide Deep:    #0A6F7D
Tide Soft:    #BFE0E5
Tide Pale:    #DDEEF1
Coral:        #FF6A3D
Coral Bright: #FF7E50
Foam:         #ECF7F8
Foam Warm:    #F4FAFB
Paper:        #FBFCFC
```

Rules:

- Coral is rare and reserved for productive action, completion, strategic emphasis, and onboarding warmth.
- Tide is used for Why affordances, calm state, selected state, and science.
- Wet Mode uses Abyss Deep background, Foam text, and Coral only for the Complete zone.

Typography:

- Display: Fraunces or native display fallback.
- Body/UI: Manrope or native system fallback.
- Spanish strings wrap; do not shrink them to fit.

## Required screen details

### Language Selection

- Full-screen centered onboarding layout.
- Atlan wordmark.
- Prompt: "Choose your language" and "Elige tu idioma".
- Equal buttons: English and Espanol.
- No preselected language based on device locale or IP.

### Welcome

English:

- Eyebrow: Welcome
- Title: Built for athletes with lives.
- Body: Atlan adapts the plan around your week - not the other way around. Before we start, a few short questions to tune the app to how you actually train.
- CTA: Begin

Spanish:

- Eyebrow: Bienvenida
- Title: Hecho para quienes entrenan entre todo lo demas.
- Body: Atlan adapta el plan a tu semana - no al reves. Antes de empezar, unas preguntas cortas para afinarla a tu manera de entrenar.
- CTA: Comenzar

### Calibration

- One question per screen.
- Progress pill, e.g. "2 of 4".
- Title: No right answers. Just calibration.
- Three option rows per question.
- Answers tune real settings: autonomy, explanation density, notification cadence, schedule flexibility.

### Tuned Summary

Show inferred settings:

- Adaptive scheduling: On. I'll absorb disruption before it becomes a missed session.
- Depth on demand: On. The "why" lives one tap from the term, with sources.
- Notifications: Minimal. Mornings and Sunday evenings only.
- First session - tomorrow: Pool - Threshold - 1,000m.
- CTA: See your first session.

### Today Dashboard

Header:

- atlan wordmark
- date

Today card:

- Today - Pool
- 1,000m
- Threshold - approx. 30 min
- Why button beside Threshold
- CTA: Start session

Weekly arc:

- Weekly arc
- Load on track
- One calm chart
- Coral marker for this week

Metric chips:

- Load: On track - Week 18 of 24
- Consistency: 72 days - Sustainable pattern
- Recovery: Good - Sleep 7h32 avg
- Why button on each chip

Do not include streaks, leaderboards, red missed states, peer ranking, notification badges, or heavy tab navigation.

### Daily Session Detail

- Date label
- Title: Pool - Threshold
- Subtitle: 1,000m - approx. 30 minutes
- Structure card with warm-up and main set
- Why button beside threshold
- CTA: Start session

### Why Modal

Native bottom sheet:

- Handle
- Eyebrow: Depth - Threshold
- Close button
- Title: Why threshold
- Mechanism callout
- Citation block
- Cached offline content if available

No motivational hype. Science must be precise and sourced.

### Session Swapper

Triggered when life disrupts the plan.

Content:

- Tag: Session adjusted
- Empathy: Life happens.
- Original: 90-min pool - threshold
- Replacement: 45-min Vasa erg - threshold equivalent
- Load statement: Weekly load still on track.
- Primary: Accept swap
- Secondary: Skip today
- Tertiary: Adjust manually

Never use "Missed", "Streak broken", "behind plan", or red failure badges.

### Wet Mode

Full-screen dark active workout mode:

- Offline pill: Offline - Cached
- Set label: Set 2 of 4 - Threshold
- Main metric: 100m
- Intensity: Threshold
- Timer: 1:32
- Target pace: 1:35
- Bottom left zone: Pause - Swipe left
- Bottom right zone: Complete - Swipe right
- Long swipe down exits Wet Mode

Specifications:

- Approx. 120 px swipe threshold before commit.
- Very large touch zones.
- Completion writes locally first.
- Haptic feedback on completion.
- Accessible actions for Complete, Pause, and Exit.

## Shared KMP module requirements

Create or specify modules:

```text
shared-core/domain
shared-core/usecases
shared-core/data/repositories
shared-core/data/local
shared-core/data/remote
shared-core/data/sync
shared-core/i18n
shared-core/telemetry
shared-core/testing
```

Core use cases:

```text
CompleteOnboarding
GetTodayDashboard
GetDailySessionDetail
StartWorkout
EnterWetMode
CompleteSet
PauseSet
ExitWetMode
GenerateSwapProposal
AcceptSwapProposal
SkipSessionWithoutFailureState
ResolveWhyContent
QueueOfflineWrite
DrainSyncQueue
ResolveSyncConflict
```

## Offline-first requirements

- Local source of truth for today session and workout actions.
- Network must not block Wet Mode.
- Writes should persist locally first, then sync later.
- Use a sync queue.
- Show Sync pending without alarming the user.
- Protect workout data from loss.

Suggested data entities:

```text
UserProfile
TrainingPlan
TrainingWeek
Session
SessionSet
SessionCompletion
SwapProposal
WhyConcept
Citation
NotificationCadence
OfflineCachePolicy
SyncQueueItem
```

## iOS notes

- Use SwiftUI NavigationStack.
- Use native sheets for Why Modal and Session Swapper.
- Use custom gestures for Wet Mode; use UIKit if SwiftUI gestures are insufficient.
- Use UIImpactFeedbackGenerator or Core Haptics.
- Use UserNotifications.
- Use BackgroundTasks for sync where allowed.
- Use HealthKit only after MVP.
- Support VoiceOver, Dynamic Type, Reduce Motion, and safe areas.

## Android notes

- Use Jetpack Compose.
- Use Compose Navigation or Navigation 3-compatible architecture.
- Use Room where platform-specific persistence is needed.
- Use WorkManager for sync queue draining.
- Use DataStore for simple preferences.
- Use Notification Channels.
- Use Health Connect only after MVP.
- Support TalkBack, font scaling, predictive back, and state restoration.

## Required app states

Include states for:

- Online
- Offline usable
- Offline partial cache
- Sync pending
- Sync failed but saved locally
- No plan
- Plan stale
- Session planned
- Session started
- Session paused
- Session completed
- Session swapped
- Session skipped without failure
- Why content missing
- Unsupported language fallback
- Notification permission denied

## Accessibility acceptance

- Every action has VoiceOver and TalkBack labels.
- Wet Mode has custom accessible actions.
- Do not use color alone.
- Support dynamic text size and Android font scaling.
- Spanish copy wraps instead of shrinking.
- Reduce Motion is respected.

## Output format

Return:

1. A concise framework recommendation.
2. A system architecture diagram in text.
3. A file/module tree.
4. Screen-by-screen UI brief.
5. Component inventory.
6. Interaction/state table.
7. iOS implementation notes.
8. Android implementation notes.
9. MVP build order.
10. Acceptance criteria.

## Acceptance criteria

The proposed implementation is correct only if:

- iOS feels native to iOS.
- Android feels native to Android.
- Core behavior is shared and tested through KMP.
- Wet Mode works offline.
- Today's session loads from local cache.
- Completing a set writes locally first.
- Session Swapper adapts disruption without shame.
- Why content is one tap below scientific terms.
- ES/EN language parity is preserved.
- The dashboard avoids streak, leaderboard, and red missed-session patterns.
