# Atlan Performance - Mobile Implementation Brief

Version: 2026-06-02  
Recommended framework: Kotlin Multiplatform shared core + SwiftUI native iOS UI + Jetpack Compose native Android UI  
Product status: Intended launched product  
Product type: Offline-first, bilingual ES/EN endurance coaching app

---

## 1. Product north star

Atlan Performance is an emotionally intelligent endurance-training coach for high-performing athletes whose lives disrupt their training plans.

The app must:

- Adapt the plan instead of punishing missed sessions.
- Explain training science only when asked.
- Work poolside without connectivity.
- Treat ES and EN as equal first-class product languages.
- Follow native iOS and Android patterns.
- Avoid consumer-fitness pressure mechanics such as streaks, leaderboards, shame states, red missed-session badges, and peer rankings.

Core principle:

> Life interruption is the default state. The product's job is to absorb disruption silently, not mark it red.

---

## 2. Navigation model

Use native stack navigation, not a web-style page model.

### 2.1 Root flows

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

### 2.2 Navigation principles

- Use a quiet header instead of a heavy tab bar on the Today Dashboard.
- Provide a gesture-revealed or sheet-based menu for Plan, History, Messages, Settings, and Profile.
- Use bottom sheets for contextual explanations and swap proposals.
- Use full-screen mode for active training and Wet Mode.
- Keep the primary action visible and singular on high-focus screens.

---

## 3. Design tokens

### 3.1 Color tokens

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

Usage rules:

- Coral is rare. Reserve it for productive action, completion, strategic emphasis, and onboarding warmth.
- Tide is used for science, calm state, progress, selected states, and Why affordances.
- Abyss and Foam define the core high-contrast identity.
- Wet Mode uses Abyss Deep background and Foam text.

### 3.2 Typography

Recommended product typography:

- Display: Fraunces or close serif/display substitute
- Body/UI: Manrope or native system fallback
- Native fallback on iOS: SF Pro plus New York where display tone is needed
- Native fallback on Android: Roboto plus a bundled display font if Fraunces is not used

Type rules:

- Use large calm display type for workout distance and key decisions.
- Use compact uppercase labels only for metadata and state.
- Do not shrink Spanish strings to fit; allow wrapping.

---

## 4. Screen specifications

## 4.1 Language Selection

### Goal

Let the user choose ES or EN before account creation.

### Layout

- Full-screen Foam background.
- Centered Atlan wordmark.
- Small Coral underline below wordmark.
- Prompt:
  - Choose your language
  - Elige tu idioma
- Equal buttons:
  - English
  - Espanol

### Components

- `BrandWordmark`
- `LanguageChoiceButton`
- `CenteredOnboardingScaffold`

### Interactions

- Tap a language to set `UserProfile.language` locally.
- Navigate to Welcome.
- Do not infer language from IP or device locale as a default selection.

### States

- Initial
- Language selected
- Local write failed

### iOS notes

- Respect preferred language only as ordering hint if product chooses to, not as preselection.
- Use VoiceOver labels: "Choose English" and "Choose Spanish".

### Android notes

- Support Android per-app language settings later, but do not replace in-product language onboarding.
- Use TalkBack labels with the visible language name.

---

## 4.2 Welcome

### Goal

Frame the app as adaptive and autonomy-supportive.

### English copy

- Eyebrow: Welcome
- Title: Built for athletes with lives.
- Body: Atlan adapts the plan around your week - not the other way around. Before we start, a few short questions to tune the app to how you actually train.
- CTA: Begin

### Spanish copy

- Eyebrow: Bienvenida
- Title: Hecho para quienes entrenan entre todo lo demas.
- Body: Atlan adapta el plan a tu semana - no al reves. Antes de empezar, unas preguntas cortas para afinarla a tu manera de entrenar.
- CTA: Comenzar

### Components

- `BackControl`
- `OnboardingEyebrow`
- `DisplayTitle`
- `BodyText`
- `PrimaryPillButton`

### Interactions

- Tap CTA to start calibration.
- Back returns to language selection.

### States

- Default
- Large text layout
- Spanish wrapping layout

---

## 4.3 Calibration

### Goal

Tune app behavior without scoring or judging the user.

### Layout

- Top row with back arrow and progress pill, e.g. "2 of 4".
- Intro label: A few short questions.
- Title: No right answers. Just calibration.
- One question per step.
- Three answer options.
- Fixed bottom CTA: Continue.

### Example questions

1. What pulls you to training most weeks?
   - The space it gives me from work
   - The pursuit of getting better
   - The structure for my week

2. When the plan meets life, what works?
   - Flex the plan around the constraint
   - Preserve the key session and move the easy work
   - Give me the shortest useful substitute

3. How much science do you want on the surface?
   - Minimal; show me only what to do
   - Moderate; show why when terms appear
   - High; give me mechanisms and sources

4. When should Atlan interrupt you?
   - Mornings only
   - Mornings and Sunday evening
   - Only when the week is at risk

### Components

- `ProgressPill`
- `CalibrationQuestionCard`
- `RadioOptionRow`
- `PrimaryPillButton`

### Interactions

- Selecting an option updates local onboarding state.
- Continue advances one question.
- Final continue generates Tuned Summary.

### States

- Option selected
- No option selected
- Validation prompt
- Large text
- Spanish wrapping

### Product rules

- No scoring language.
- No "good athlete" or "bad athlete" framing.
- Each answer must tune a real product setting.

---

## 4.4 Tuned Summary

### Goal

Show what the app inferred and allow adjustment.

### Layout

- Eyebrow: Tuned to your week
- Title: Here's how I'll work with you.
- Rows:
  1. Adaptive scheduling: On. I'll absorb disruption before it becomes a missed session.
  2. Depth on demand: On. The "why" lives one tap from the term, with sources.
  3. Notifications: Minimal. Mornings and Sunday evenings only.
  4. First session - tomorrow: Pool - Threshold - 1,000m
- CTA: See your first session

### Components

- `TunedSettingRow`
- `FirstSessionPreviewCard`
- `PrimaryPillButton`

### Interactions

- Tap any inferred setting to edit.
- CTA navigates to first session preview or Today Dashboard.

### States

- Settings generated
- Editing a setting
- No first session available

---

## 4.5 Today Dashboard

### Goal

Answer two questions quickly:

1. What is today?
2. Is the week still on track?

### Layout

Header:

- Left: atlan wordmark
- Right: date, e.g. Tuesday - May 26

Today card:

- Label: Today - Pool
- Main value: 1,000m
- Detail: Threshold - approx. 30 min
- Inline Why button next to Threshold
- CTA: Start session

Weekly arc card:

- Label: Weekly arc
- State: Load on track
- One calm line/area chart
- Coral marker for this week

Metric chips:

- Load: On track - Week 18 of 24
- Consistency: 72 days - Sustainable pattern
- Recovery: Good - Sleep 7h32 avg
- Each chip has an inline Why button

### Components

- `QuietHeader`
- `TodayHeroCard`
- `WhyButton`
- `WeeklyArcChart`
- `MetricChip`
- `GestureMenuHandle`

### Interactions

- Start session -> SessionStart.
- Tap Why -> WhyModal.
- Tap Weekly Arc -> WeeklyArcDetail.
- Swipe or tap header/menu affordance -> navigation sheet.

### States

- Normal
- Offline, cached
- Sync pending
- No session today
- Session swapped
- Session complete
- No plan loaded
- Data stale

### Explicit exclusions

Do not include:

- Streaks
- Leaderboards
- Peer ranking
- Red missed states
- Notification badges
- Multi-tab dashboard chrome
- Goal-completion pressure bars

---

## 4.6 Daily Session Detail

### Goal

Show the session structure with minimal cognitive load.

### Layout

- Date label: Today - Tuesday, May 26
- Title: Pool - Threshold
- Subtitle: 1,000m - approx. 30 minutes
- Structure card:
  - Warm-up: 400m easy - approx. 8 min
  - Main set: 4 x 100m at threshold - approx. 14 min - Why button
- CTA: Start session

### Components

- `SessionHeader`
- `SessionStructureCard`
- `SessionRow`
- `WhyButton`
- `PrimaryPillButton`

### Interactions

- Tap Why -> WhyModal for Threshold.
- Tap Start -> SessionStart.
- If offline, continue using cached session data.

### States

- Cached
- Offline
- Swap available
- Already completed
- Session unavailable

---

## 4.7 Why Modal

### Goal

Give science depth on demand.

### Layout

Native bottom sheet:

- Drag handle
- Eyebrow: Depth - Threshold
- Close button
- Title: Why threshold
- Body explanation
- Mechanism callout
- Citation block

### Components

- `BottomSheetScaffold`
- `WhyModalHeader`
- `MechanismCallout`
- `CitationBlock`

### Example content structure

- Concept definition
- Why it matters for this session
- Mechanism bullets
- Prescription details
- Reference citation

### Interactions

- Drag down or close to dismiss.
- Scroll if content exceeds sheet height.
- Tap citation if external source support is added later.

### States

- Cached content
- Offline cached content
- Missing content
- Unsupported language fallback
- Loading from local store

### Product rules

- No hype language.
- No generic "studies show" copy.
- Every mechanism needs a source.
- Tide, not Coral, marks Why affordances.

---

## 4.8 Session Swapper

### Goal

Absorb disruption without shame.

### Trigger examples

- User misses the pool window.
- Calendar event runs late.
- User opens the app at night and the original plan no longer fits.
- User taps "I cannot do this today."

### Layout

Bottom sheet or full-screen modal depending on urgency.

Content:

- Tag: Session adjusted
- Empathy line: Life happens.
- Original: 90-min pool - threshold
- Replacement: 45-min Vasa erg - threshold equivalent
- Load statement: Weekly load still on track.
- Primary action: Accept swap
- Secondary action: Skip today
- Tertiary: Adjust manually

### Components

- `SwapProposalSheet`
- `OriginalSessionRow`
- `ReplacementSessionRow`
- `LoadImpactBadge`
- `PrimaryPillButton`
- `SecondaryTextButton`

### Interactions

- Accept swap updates plan locally and queues sync.
- Skip today updates plan locally without red failure state.
- Adjust manually opens a constrained editor.
- Why on weekly load opens WhyModal.

### States

- Swap proposed
- Swap accepted
- Swap rejected
- Manual adjustment
- No equivalent session available
- Offline swap generated from cached plan
- Sync pending

### Product rules

Avoid:

- Missed
- Behind plan
- Streak broken
- Red failure badges
- Shame recovery CTAs

Use:

- Life happens.
- Want to swap?
- The week still works.
- Smart call - protect the week.

---

## 4.9 Session Start

### Goal

Transition from planning to workout execution.

### Layout

- Session title
- Offline readiness status
- Key set summary
- Toggle or CTA for Wet Mode
- CTA: Start workout

### Components

- `OfflineReadyPill`
- `SessionSummaryCard`
- `WetModePrompt`
- `PrimaryPillButton`

### Interactions

- Start workout -> StandardWorkoutMode or WetMode based on discipline/user preference.
- Tap Wet Mode -> WetMode.

### States

- Fully cached
- Partially cached
- Offline but usable
- Not cached; requires connection before start

---

## 4.10 Wet Mode

### Goal

Allow poolside workout control with wet hands, glare, fatigue, and no connectivity.

### Layout

Dark full-screen mode:

- Status bar area
- Offline pill: Offline - Cached
- Set label: Set 2 of 4 - Threshold
- Main display: 100m
- Intensity: Threshold
- Timer: 1:32
- Target pace: 1:35
- Bottom two-zone action grid:
  - Left: Pause - Swipe left
  - Right: Complete - Swipe right
- Small Wet Mode label at bottom

### Components

- `WetModeScreen`
- `OfflineCachedPill`
- `SetDisplayBlock`
- `LargeMetricDisplay`
- `WetActionZone`
- `SwipeHintTrack`

### Interactions

- Swipe right: complete set.
- Swipe left: pause.
- Long swipe down: exit Wet Mode.
- Haptic confirmation on successful complete.
- Do not require precision taps.
- Add accessible actions for screen readers.

### Specs

- Minimum action zone: 320 x 360 px when physically possible.
- Swipe threshold: approx. 120 px before commit.
- Contrast target: AAA-level for primary text.
- Completion write: local first, no network dependency.

### States

- Active set
- Set completed
- Paused
- Exit confirmation
- Offline cached
- Sync pending
- Accidental swipe below threshold
- Last set complete

### iOS notes

- Use custom SwiftUI gestures; fall back to UIKit gesture recognizers if directional arbitration is unreliable.
- Use UIImpactFeedbackGenerator or Core Haptics.
- Respect Reduce Motion.
- Add VoiceOver custom actions: Complete set, Pause set, Exit Wet Mode.

### Android notes

- Use Compose pointer input or draggable gestures.
- Use LocalHapticFeedback.
- Support predictive back with confirmation in active workout.
- Add TalkBack custom actions.

---

## 4.11 Post-Session Detail

### Goal

Move deeper metrics out of the dashboard and into the right context.

### Layout

- Completion summary
- Distance
- Duration
- Sets completed
- RPE input
- Notes
- Optional charts:
  - HR zone distribution
  - Pace consistency
  - Recovery signal
- CTA: Save
- Secondary: Adjust next session

### Components

- `CompletionSummaryCard`
- `RpeSelector`
- `NotesField`
- `OptionalMetricPanel`
- `SaveButton`

### States

- Saved locally
- Sync pending
- Sync failed but safe locally
- User skipped metrics

---

## 4.12 Settings

### Goal

Give the user control without making setup feel heavy.

### Sections

- Language
- Notification cadence
- Explanation density
- Offline cache
- Connected services
- Accessibility preferences
- Privacy and data export

### Components

- `SettingsList`
- `SettingsRow`
- `LanguagePickerSheet`
- `NotificationCadenceSheet`
- `OfflineCacheStatusCard`

### States

- Language changed
- Cache healthy
- Cache stale
- Sync pending
- Connected integration disabled

---

## 5. Component inventory

### Core components

```text
QuietHeader
BrandWordmark
PrimaryPillButton
SecondaryTextButton
WhyButton
OfflineCachedPill
ProgressPill
BottomSheetScaffold
MetricChip
WeeklyArcChart
SessionStructureCard
SessionRow
SwapProposalSheet
WetActionZone
LargeMetricDisplay
CitationBlock
MechanismCallout
SettingsRow
```

### Component rules

- Buttons use native press states and haptics where appropriate.
- WhyButton is a small Tide-outlined circle with an italic i.
- Primary CTAs are dark pills except Wet Mode Complete, which uses Coral.
- No component should introduce streak, rank, flame, trophy, or badge pressure language.

---

## 6. Global app states

### Network states

- Online
- Offline usable
- Offline partial cache
- Offline not ready
- Sync pending
- Sync failed but saved locally

### Session states

- Planned
- Started
- Paused
- Completed
- Skipped without failure
- Swapped
- Sync pending

### Plan states

- On track
- Needs adaptation
- Swap proposed
- No plan
- Plan stale

### Language states

- EN active
- ES active
- Unsupported copy key fallback
- Pending language change

### Permission states

- Notifications allowed
- Notifications denied
- Health integration not connected
- Calendar access not connected

---

## 7. Accessibility requirements

- Support Dynamic Type and Android font scaling.
- Support VoiceOver and TalkBack for every interactive element.
- Wet Mode must provide accessible actions for complete, pause, and exit.
- Do not rely on color alone.
- Keep minimum tap targets at or above platform guidelines; Wet Mode must exceed normal minimums significantly.
- Provide reduced-motion alternatives for animated charts and sheet transitions.
- Spanish strings wrap instead of shrinking.

---

## 8. Telemetry events

Track events without creating pressure mechanics.

```text
language_selected
onboarding_started
calibration_answered
onboarding_completed
today_dashboard_viewed
why_opened
why_dismissed
session_started
wet_mode_entered
wet_mode_set_completed
wet_mode_paused
wet_mode_exited
swap_proposed
swap_accepted
session_skipped_without_failure
post_session_saved
sync_queued
sync_completed
sync_failed
```

Do not track or display streaks, leaderboard rank, or guilt-oriented engagement metrics.

---

## 9. iOS implementation notes

- Use SwiftUI for screen composition.
- Use NavigationStack for primary flows.
- Use native `.sheet` or custom bottom sheet behavior for Why and Session Swapper.
- Use local persistence through SQLite, Core Data, SwiftData, or a KMP-compatible adapter.
- Use BackgroundTasks for scheduled sync where permitted.
- Use UserNotifications for Session Swapper reminders.
- Use HealthKit only after MVP flows are stable.
- Use VoiceOver custom actions for Wet Mode.
- Use native Dynamic Type and test extra-large sizes.
- Respect safe areas, haptics settings, Reduce Motion, and Focus modes.

---

## 10. Android implementation notes

- Use Jetpack Compose for screen composition.
- Use Compose Navigation or Navigation 3-compatible routing.
- Use Room for persistence if platform-specific storage is selected.
- Use WorkManager for sync queue draining.
- Use DataStore for simple preferences.
- Use notification channels for Session Swapper.
- Use Health Connect only after MVP flows are stable.
- Use LocalHapticFeedback for Wet Mode.
- Use TalkBack custom actions.
- Support predictive back and proper state restoration.
- Test font scale, dark mode, and offline behavior on real devices.

---

## 11. Build order

1. Shared KMP domain model and use cases.
2. Native design tokens on iOS and Android.
3. Onboarding flow.
4. Local database and mock plan seed.
5. Today Dashboard.
6. Daily Session Detail.
7. Why Modal.
8. Session Start and Wet Mode.
9. Session Swapper.
10. Sync queue.
11. Accessibility pass.
12. Localization QA.
13. Production analytics and crash reporting.

---

## 12. Done definition

The mobile implementation is acceptable when:

- The app can launch offline and show today's cached session.
- The user can complete a Wet Mode set without network.
- Session completion writes locally first.
- The dashboard contains no streak, leaderboard, red missed state, or peer-rank pressure mechanics.
- ES and EN are selected before account creation and render at equal visual priority.
- Why content is available one tap below workout terms and metric claims.
- Session Swapper adapts disruption without shame language.
- iOS and Android each feel native to their own platform.
