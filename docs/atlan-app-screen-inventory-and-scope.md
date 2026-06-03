# Atlan App Screen Inventory and Product Scope

**Document type:** Mobile app screen inventory and implementation scope  
**Product:** Atlan mobile app  
**Platforms:** iOS and Android  
**Architecture context:** Kotlin Multiplatform shared core with native Swift and Android UI layers  
**Design context:** Calm bilingual training experience using Foam, Abyss, and rare Coral accent  
**Current validated baseline:** Language Selection renders correctly on iOS simulator. Shared KMP copy reaches the native iOS UI. The 4-set Threshold timer screen pattern is defined as the primary active-session interaction model.

---

## 1. Purpose

This document lists the screens, flows, and supporting product surfaces that should be included in the Atlan app based on the current case-study direction.

It is intended to help design, product, and engineering teams understand:

- which screens are required for the MVP
- which screens are required for a polished case-study prototype
- which screens are needed later for a production app
- how the active 4-set timer flow connects to onboarding, workout setup, session completion, offline behavior, and sync
- what iOS and Android considerations must be designed before implementation

---

## 2. Product Assumptions

The current app direction implies a mobile training product with the following characteristics:

- bilingual English/Spanish experience
- native iOS and Android presentation layers
- Kotlin Multiplatform shared business logic and copy
- offline/cached workout capability
- structured workout sessions
- set-based threshold training
- large, thumb-friendly active-session controls
- calm minimal UI with high contrast during activity
- session progress, recovery, and completion states

The screen list below assumes the app is focused on a guided workout experience rather than a broad social fitness product.

---

## 3. Screen Priority Levels

| Priority | Meaning |
|---|---|
| P0 | Required for the core case-study flow and MVP demo. |
| P1 | Required for a complete production-quality MVP. |
| P2 | Useful for a more mature app but not required for first demo. |
| P3 | Future expansion or advanced product capability. |

---

## 4. Core User Journey

The recommended primary case-study journey is:

```text
Launch
→ Language Selection
→ Welcome / Onboarding
→ Permissions
→ Home
→ Workout Plan / Session Detail
→ Workout Prep
→ Active 4-Set Timer
→ Rest / Transition Between Sets
→ Session Complete
→ Sync Status
→ Session Summary
```

This journey demonstrates the strongest product narrative:

1. bilingual accessibility
2. clean onboarding
3. structured training
4. resilient offline session behavior
5. native mobile interaction quality
6. shared KMP logic powering real UI

---

## 5. Required Screen Inventory

### 5.1 Launch and Entry

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| Splash / Launch Screen | P0 | Provides native startup surface before app state resolves. | Must use platform-native launch handling. Keep minimal. |
| App Loading / Bootstrap Screen | P1 | Handles shared-core initialization, cached session lookup, auth state, and language preference. | Needed if startup takes longer than native splash. |
| Language Selection | P0 | Lets the user choose English or Spanish as co-equal languages. | Already validated in iOS baseline. Must persist selection. |
| Welcome Screen | P0 | Introduces the product promise and moves user into setup. | Should avoid heavy marketing copy. |
| Returning User Resume Screen | P1 | If an active cached session exists, allows user to resume or discard. | Critical for offline/session resilience. |

### 5.2 Onboarding and Setup

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| Account Sign In | P1 | Allows returning users to access plans and history. | Can be deferred in prototype if case study is local-first. |
| Account Create | P1 | Creates user profile. | Keep form minimal. |
| Guest Mode Explanation | P1 | Explains what works without an account. | Useful if offline-first MVP is desired. |
| Profile Setup | P1 | Captures name, training level, units, and goal. | Avoid excessive onboarding friction. |
| Unit Preference | P1 | Selects metric or imperial display. | Should affect workout distances and pace formatting. |
| Notification Permission Rationale | P1 | Explains timer/rest reminders before platform permission prompt. | Do not trigger system permission cold. |
| Health/Fitness Permission Rationale | P2 | Explains optional Apple Health / Health Connect integration. | Not required for first timer MVP. |
| Location Permission Rationale | P3 | Explains location use if outdoor activity tracking is added. | Avoid if location is not required. |

### 5.3 Home and Workout Discovery

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| Home Dashboard | P0 | Primary hub showing today’s recommended session and status. | Should include resume active session if available. |
| Today’s Workout Card | P0 | Summarizes the next workout. | Can live on Home rather than separate screen. |
| Workout Plan List | P1 | Shows available training plans or scheduled workouts. | Needed once more than one workout exists. |
| Workout Detail | P0 | Shows session structure before starting. | Must show 4 sets, distance, target pace, expected duration, and offline availability. |
| Workout History | P1 | Shows completed sessions. | Supports trust and progress. |
| Progress Overview | P2 | Summarizes training trends. | Useful after multiple workouts are supported. |

### 5.4 Workout Preparation

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| Workout Prep | P0 | Final confirmation before starting the 4-set session. | Shows sets, pace target, estimated time, and Start. |
| Offline Availability Check | P0 | Confirms workout data is cached and ready. | Can be an inline state on Workout Prep. |
| Warmup Prompt | P1 | Optional pre-session step. | Can be skipped for MVP if not part of case study. |
| Safety / Readiness Check | P1 | Confirms user is ready before active timer. | Short and non-intrusive. |

### 5.5 Active Session and Set Flow

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| Active Set Timer | P0 | Main workout screen for a set. | The reference screen shows Set 2 of 4, Threshold, 100m, countdown, Pause, Complete. |
| Ready State | P0 | Pre-start state for Set 1 or resumed set. | Shows Start instead of Pause/Complete. |
| Paused State | P0 | Stops timer and exposes Resume, Complete, End Session. | Must persist pause state locally. |
| Rest Between Sets | P0 | Optional transition between Set 1→2, 2→3, 3→4. | Shows rest countdown, next set preview, Skip Rest. |
| Set Complete Transition | P0 | Confirms set completion and prepares next set. | May be instant or a short interstitial. |
| Overtime State | P1 | Handles timer reaching 0 without manual completion. | Recommended to avoid accidental auto-complete. |
| Back / Exit Confirmation | P0 | Prevents accidental workout loss. | Triggered by top-left back button or OS back gesture. |
| End Session Confirmation | P0 | Confirms ending before all sets are complete. | Must explain saved progress behavior. |
| Session Recovery Screen | P1 | Resumes after app kill/background interruption. | Critical for robust mobile behavior. |

### 5.6 Completion and Results

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| Session Complete | P0 | Confirms all 4 sets are finished. | Should feel calm and rewarding. Coral can be used sparingly. |
| Session Summary | P0 | Shows set results, elapsed time, target pace, completed sets, sync status. | Required to close the timer journey. |
| Set-by-Set Breakdown | P1 | Shows details for each set. | Useful for coaching and review. |
| Save Notes / Reflection | P1 | Lets user add perceived effort or notes. | Keep optional. |
| Share Result | P2 | Allows sharing summary externally. | Not required for MVP. |

### 5.7 Offline, Sync, and Error Handling

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| Offline Cached Indicator | P0 | Communicates that session is available offline. | Can be a status row, not a full screen. |
| Sync Pending State | P0 | Shows locally saved session awaiting upload. | Required if actions occur offline. |
| Sync Success State | P1 | Confirms local results uploaded. | Can be inline on summary. |
| Sync Failed State | P0 | Allows retry and explains data is still safe locally. | Must be explicit and reassuring. |
| No Connection Empty State | P1 | Shown when content cannot load and is not cached. | Include retry. |
| Local Data Conflict Resolution | P2 | Handles server/local mismatch. | Needed for multi-device accounts. |
| Generic Error Screen | P0 | Handles unrecoverable states. | Must offer retry or safe exit. |

### 5.8 Settings and Preferences

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| Settings Home | P1 | Entry point for preferences. | Required for language and units after onboarding. |
| Language Settings | P1 | Allows switching English/Spanish. | Must update KMP shared copy and native UI. |
| Units Settings | P1 | Metric/imperial preference. | Must update distance/pace formatting. |
| Timer Settings | P1 | Haptics, sound, auto-lock behavior, countdown cues. | Important for active training UX. |
| Notification Settings | P2 | Controls workout reminders and rest alerts. | Requires permission handling. |
| Accessibility Settings | P2 | Motion, contrast, text-size notes, haptics. | Native system settings should be respected first. |
| Account Settings | P2 | Email, sign out, delete account. | Needed if authentication exists. |
| Privacy / Data Settings | P2 | Data export, consent, integrations. | Required for production compliance. |

### 5.9 Help, Education, and Support

| Screen | Priority | Purpose | Notes |
|---|---:|---|---|
| How It Works | P1 | Explains set-based threshold workout structure. | Helpful for first-time users. |
| Timer Gesture Tutorial | P0 | Explains swipe left Pause and swipe right Complete. | Can be shown as one-time coach mark. |
| Pace Explanation | P1 | Explains target pace and threshold. | Reduces confusion. |
| FAQ / Help | P2 | Common questions. | Not required for prototype. |
| Contact Support | P2 | Support entry point. | Production need. |

---

## 6. MVP Screen Set

The smallest complete MVP should include these screens:

1. Splash / Launch Screen
2. Language Selection
3. Welcome Screen
4. Home Dashboard
5. Workout Detail
6. Workout Prep
7. Active Set Timer
8. Paused State
9. Rest Between Sets
10. Set Complete Transition
11. Exit Confirmation
12. Session Complete
13. Session Summary
14. Sync Pending / Sync Failed State
15. Settings Home
16. Language Settings
17. Units Settings
18. Timer Settings
19. Generic Error Screen

This set is sufficient to demonstrate a coherent product rather than only a single isolated timer screen.

---

## 7. Case-Study Prototype Screen Set

For portfolio or stakeholder case-study purposes, prioritize the most visually and narratively important screens:

1. Language Selection
2. Home Dashboard
3. Workout Detail
4. Workout Prep
5. Active Set Timer — Set 1 of 4
6. Active Set Timer — Set 2 of 4, Offline · Cached
7. Paused State
8. Rest Between Sets
9. Active Set Timer — Final Set
10. Session Complete
11. Session Summary
12. Sync Pending / Offline Saved
13. Settings — Language and Units

This prototype set tells the full story:

```text
bilingual setup → workout selection → active training → offline resilience → completion → review
```

---

## 8. Detailed Screen Requirements

### 8.1 Language Selection

Must include:

- Atlan wordmark
- English and Spanish as equal visual choices
- Foam background
- Abyss primary controls
- rare Coral accent only where useful
- persisted language selection
- KMP-provided copy
- support for iOS Dynamic Type and Android font scaling

States:

- default
- selected language
- loading after selection
- error saving preference

### 8.2 Home Dashboard

Must include:

- greeting or neutral header
- today’s workout summary
- resume active session if one exists
- offline/sync indicator if relevant
- access to history and settings

States:

- first-time user
- workout available
- no workout scheduled
- active session in progress
- offline cached
- sync pending
- loading
- error

### 8.3 Workout Detail

Must include:

- workout name
- type: Threshold
- total sets: 4
- distance per set: 100m or configured value
- target pace: 1:35 or configured value
- estimated duration
- rest information if applicable
- cached/offline availability
- Start Workout action

States:

- ready
- not cached
- caching
- cached
- unavailable offline
- loading
- error

### 8.4 Workout Prep

Must include:

- current workout summary
- readiness message
- Start action
- optional warmup reminder
- offline cached confirmation
- ability to go back safely

States:

- ready
- checking cache
- missing data
- starting
- error

### 8.5 Active Set Timer

Must include:

- connection/cache status
- set index: `Set N of 4`
- workout type: `Threshold`
- interval label: `100m`
- large countdown timer
- target pace
- Pause action
- Complete action
- back button
- gesture hints

Required behavior:

- timer must count from monotonic time
- timer must survive app backgrounding
- set progress must persist locally
- pause must stop elapsed active time
- complete must advance to next state
- offline actions must queue for sync

States:

- ready
- active
- paused
- overtime
- completing
- sync pending
- sync failed
- error

### 8.6 Paused State

Must include:

- clear Paused label
- frozen timer
- Resume action
- Complete Set action
- End Session action
- optional elapsed time

States:

- paused
- resuming
- ending
- save failed

### 8.7 Rest Between Sets

Must include:

- completed set confirmation
- next set preview
- rest countdown
- Skip Rest action
- End Session option

States:

- rest active
- rest paused if supported
- skip confirmation if needed
- next set loading
- error

### 8.8 Exit Confirmation

Must include:

- clear consequence copy
- options:
  - Keep Going
  - Save and Exit
  - End Session
- offline data safety note if applicable

States:

- default
- saving
- save failed
- discarded/end confirmed

### 8.9 Session Complete

Must include:

- completion message
- all 4 sets completed
- total elapsed time
- primary action to view summary
- sync state if offline

States:

- complete online
- complete offline saved
- syncing
- sync failed

### 8.10 Session Summary

Must include:

- workout title
- date/time
- total sets completed
- set-by-set breakdown
- target pace
- actual elapsed time if available
- notes/reflection if supported
- sync status
- done action

States:

- synced
- pending sync
- sync failed with retry
- partial session
- loading
- error

### 8.11 Settings

Must include:

- language preference
- units preference
- timer preferences
- haptic/sound preferences
- offline storage status if applicable
- app version/build

States:

- default
- saving preference
- save success
- save failed

---

## 9. Navigation Architecture

### 9.1 Recommended Navigation Model

Use a stack-based navigation model with tab-level expansion only after the app grows.

MVP recommendation:

```text
Root
├── Language Selection
├── Onboarding
└── Main Stack
    ├── Home
    ├── Workout Detail
    ├── Workout Prep
    ├── Active Session Flow
    ├── Session Summary
    └── Settings
```

Future production recommendation:

```text
Tabs
├── Today
├── History
├── Progress
└── Settings
```

### 9.2 Active Session Navigation Rules

The active timer should behave like a protected flow.

Rules:

- Native back gestures should not immediately discard the session.
- Back button opens Exit Confirmation.
- App backgrounding should preserve timer state.
- Relaunch should offer Resume Session if incomplete.
- Deep links should not interrupt an active session without confirmation.
- During active countdown, avoid modals unless they are critical.

---

## 10. Layout and Component Needs

### 10.1 Core Layout Components

The app needs these reusable layout components:

- App Shell
- Safe Area Container
- Screen Header
- Large Action Footer
- Split Action Zone
- Status Row
- Progress Header
- Section Container
- Card Surface
- Empty State Layout
- Error State Layout
- Modal Sheet Layout
- Bottom Sheet Layout

### 10.2 Core UI Components

Required reusable components:

- Button
- Icon Button
- Text Button
- Split Action Button
- Language Option Button
- Workout Card
- Status Badge
- Sync Badge
- Timer Display
- Pace Display
- Set Progress Label
- Progress Dots or Step Indicator
- Confirmation Dialog
- Bottom Sheet
- Toast / Inline Banner
- Loading Spinner
- Skeleton Placeholder
- Form Field
- Segmented Control
- Settings Row
- Toggle / Switch

### 10.3 Timer-Specific Components

Required timer components:

- ActiveTimerView
- SetHeaderView
- OfflineCachedIndicator
- CountdownText
- TargetPaceText
- PausePanel
- CompletePanel
- RestCountdownView
- SetTransitionView
- SessionCompleteView
- SyncPendingView
- SyncFailedRetryView

---

## 11. Interaction Requirements

### 11.1 Gestures

The active timer reference uses directional gestures:

| Gesture | Action | Notes |
|---|---|---|
| Swipe left | Pause | Must not trigger accidentally from a small movement. |
| Swipe right | Complete | Should require enough distance or velocity to confirm intent. |
| Tap Pause panel | Pause | Required fallback for accessibility. |
| Tap Complete panel | Complete | Required fallback for accessibility. |
| Back tap | Exit confirmation | Never discard immediately. |
| OS back gesture | Exit confirmation | Android and iOS parity required. |

### 11.2 Haptics

Recommended haptic moments:

- Start set
- Pause
- Resume
- Complete set
- Final session complete
- Sync failure
- Timer reaches zero

Platform notes:

- iOS: use `UIImpactFeedbackGenerator`, `UINotificationFeedbackGenerator`, or SwiftUI sensory feedback where appropriate.
- Android: use `Vibrator`, `VibrationEffect`, or Compose haptic feedback APIs depending on implementation layer.
- Always respect system haptic accessibility settings where possible.

### 11.3 Audio

Optional audio cues:

- countdown near zero
- rest complete
- session complete

Rules:

- Provide a setting to disable sound.
- Do not rely on audio alone.
- Pair with visual and haptic cues.

---

## 12. State Model Needed in Shared Core

The app should define shared KMP models for screen state, not just localized text.

Recommended shared-state types:

```text
AppBootstrapState
LanguagePreferenceState
WorkoutListState
WorkoutDetailState
WorkoutPrepState
ActiveSessionState
TimerState
SetState
RestState
SessionCompletionState
SyncState
ErrorState
SettingsState
```

### 12.1 Active Session State

Recommended structure:

```text
ActiveSessionState
- sessionId
- workoutId
- workoutType
- totalSets
- currentSetIndex
- currentSetLabel
- targetPace
- timerState
- connectionState
- syncState
- isCached
- canPause
- canComplete
- canResume
- canEnd
- localizedCopy
```

### 12.2 Timer State

Recommended structure:

```text
TimerState
- status: ready | active | paused | overtime | completed
- durationMs
- remainingMs
- elapsedMs
- startedAtMonotonicMs
- pausedAccumulatedMs
- pauseStartedAtMonotonicMs
- displayText
```

### 12.3 Sync State

Recommended structure:

```text
SyncState
- online
- cached
- pendingUpload
- syncing
- synced
- failed
- lastSyncAttemptAt
- retryAvailable
```

---

## 13. Offline and Persistence Requirements

The app needs local persistence for:

- language preference
- units preference
- workout metadata
- cached workout plan
- active session ID
- current set index
- timer start time
- accumulated pause time
- completed sets
- manually completed timestamps
- sync queue
- failed sync attempts

Required offline behavior:

1. User can start cached workout offline.
2. User can complete sets offline.
3. User can pause/resume offline.
4. User can complete session offline.
5. App clearly shows that results are saved locally.
6. App syncs when connection returns.
7. Sync failure does not erase local data.

---

## 14. iOS Implementation Notes

### 14.1 Navigation

- Use `NavigationStack` for standard flows.
- Protect active session exits with confirmation dialogs or sheets.
- Use native interactive-dismiss prevention for modal active-session surfaces where needed.

### 14.2 Layout

- Respect safe areas, Dynamic Island, home indicator, and rounded device corners.
- Active session bottom actions should extend toward the bottom but avoid inaccessible tap zones.
- Use `safeAreaInset` or equivalent to handle bottom action zones.

### 14.3 Timer

- Derive timer from monotonic time, not UI decrement loops.
- Use app lifecycle notifications to recalculate remaining time after background/foreground transitions.
- Consider Live Activities later if workouts should appear on Lock Screen or Dynamic Island.

### 14.4 Accessibility

- Support Dynamic Type where possible.
- Timer text may need controlled scaling to avoid layout collapse.
- Provide VoiceOver labels for gesture actions.
- Do not require swipe-only actions.
- Minimum target size should be at least 44x44 pt.

### 14.5 Platform Features to Consider Later

- Apple Health integration
- Live Activities
- Lock Screen widgets
- Watch companion app
- Shortcuts/Siri intents

---

## 15. Android Implementation Notes

### 15.1 Navigation

- Use Jetpack Navigation or Compose Navigation.
- Android system back must route to Exit Confirmation during active sessions.
- Handle process death restoration explicitly.

### 15.2 Layout

- Support edge-to-edge layouts carefully.
- Account for gesture navigation insets.
- Use large touch targets; Material minimum target guidance is a useful baseline.
- Ensure split bottom action panels remain reachable on tall devices.

### 15.3 Timer

- Derive timer from monotonic time using elapsed realtime APIs.
- Recalculate after backgrounding or process restoration.
- Consider foreground service only if the timer must continue with durable notification behavior.

### 15.4 Accessibility

- Support font scaling.
- Provide TalkBack labels and actions for Pause and Complete.
- Do not rely on color alone for state.
- Minimum target size should be at least 48x48 dp.

### 15.5 Platform Features to Consider Later

- Health Connect integration
- Foreground workout notification
- Wear OS companion
- App widgets

---

## 16. Localization Requirements

The app must be designed as bilingual from the beginning.

Required localization coverage:

- Language Selection
- onboarding copy
- workout labels
- set labels
- timer states
- pause/complete actions
- error messages
- sync status messages
- settings
- accessibility labels
- permission rationale screens

Localization rules:

- Do not hardcode English in native UI.
- Copy should come from the KMP shared core where practical.
- Avoid layouts that break when Spanish strings are longer.
- Test all primary screens in English and Spanish.
- Avoid abbreviations that do not translate cleanly.

---

## 17. Accessibility Requirements

Required across all screens:

- large touch targets
- high color contrast
- visible focus states where applicable
- VoiceOver and TalkBack labels
- no swipe-only critical actions
- timer state announced accessibly
- errors described in text
- color not used as the only status indicator
- support for reduced motion
- support for text scaling
- clear offline/sync messages

Timer-specific accessibility:

- Pause and Complete must be accessible as explicit actions.
- Gesture hints must not be the only instruction.
- Countdown should not announce every second by default.
- Important transitions can be announced, for example “Set 2 complete” or “Rest started.”

---

## 18. Empty, Loading, Error, and Edge States

The app needs design coverage for these states:

### 18.1 Empty States

- no workouts available
- no history yet
- no cached workout offline
- no progress data

### 18.2 Loading States

- app bootstrap
- loading workout detail
- starting session
- syncing session
- loading history

### 18.3 Error States

- failed to load workout
- failed to save language preference
- failed to start session
- timer state recovery failed
- sync failed
- unknown error

### 18.4 Edge States

- app killed during active set
- device time changed
- offline from start
- offline during completion
- session partially completed
- user exits mid-set
- user completes all sets while offline
- user changes language mid-session
- user changes units mid-session
- timer reaches zero but set is not manually completed

---

## 19. Analytics and Event Tracking Needs

For a production-quality implementation, track these events:

- language_selected
- onboarding_completed
- workout_detail_viewed
- workout_started
- set_started
- set_paused
- set_resumed
- set_completed
- set_overtime_started
- rest_started
- rest_skipped
- session_completed
- session_exited
- session_saved_offline
- sync_started
- sync_completed
- sync_failed
- settings_changed

Do not track sensitive health details without explicit consent and a clear privacy policy.

---

## 20. What Else Needs to Be Included in the App

Beyond screens, the app needs these product and technical foundations:

### 20.1 Design System

- mobile token mapping
- screen templates
- component library
- state documentation
- gesture documentation
- accessibility documentation
- iOS/Android parity notes

### 20.2 Shared Core

- localized string provider
- workout models
- timer state machine
- session repository interface
- offline cache interface
- sync queue interface
- preference storage interface
- error models

### 20.3 Native Platform Layers

- SwiftUI views for iOS
- Compose or native Android views
- platform-specific haptics
- lifecycle handling
- accessibility semantics
- safe-area/inset handling
- navigation integration

### 20.4 QA Coverage

- unit tests for timer math
- unit tests for set progression
- unit tests for localization keys
- offline sync tests
- app relaunch recovery tests
- iOS UI tests
- Android UI tests
- accessibility audits
- contrast validation
- snapshot tests in English and Spanish

### 20.5 Content Design

- English copy
- Spanish copy
- permission rationale copy
- error copy
- sync/offline copy
- empty state copy
- accessibility labels

### 20.6 Privacy and Compliance

- privacy policy
- data storage explanation
- health data consent if health integrations are added
- account deletion if accounts exist
- data export if production compliance requires it

---

## 21. Recommended Build Order

### Phase 1 — Current Baseline Stabilization

1. Commit known-good Language Selection implementation.
2. Add persistent language preference.
3. Add startup routing based on language selection.
4. Add basic Home screen.

### Phase 2 — Core Workout Flow

1. Workout Detail
2. Workout Prep
3. Active Set Timer
4. Pause/Resume
5. Complete Set
6. Rest Between Sets
7. Session Complete
8. Session Summary

### Phase 3 — Offline Resilience

1. Local session persistence
2. Resume active session
3. Offline cached status
4. Sync pending status
5. Sync failure and retry

### Phase 4 — Settings and Preferences

1. Settings Home
2. Language Settings
3. Units Settings
4. Timer Settings
5. Accessibility-related preferences where needed

### Phase 5 — Production Readiness

1. Authentication if required
2. History
3. Progress overview
4. Notifications
5. Health integrations
6. Analytics
7. Privacy and support

---

## 22. Final Recommended Screen Checklist

Use this as the master checklist.

### P0 Screens

- [ ] Splash / Launch Screen
- [ ] Language Selection
- [ ] Welcome Screen
- [ ] Home Dashboard
- [ ] Workout Detail
- [ ] Workout Prep
- [ ] Active Set Timer
- [ ] Ready State
- [ ] Paused State
- [ ] Rest Between Sets
- [ ] Set Complete Transition
- [ ] Exit Confirmation
- [ ] End Session Confirmation
- [ ] Session Complete
- [ ] Session Summary
- [ ] Sync Pending State
- [ ] Sync Failed State
- [ ] Generic Error Screen
- [ ] Timer Gesture Tutorial

### P1 Screens

- [ ] App Loading / Bootstrap Screen
- [ ] Returning User Resume Screen
- [ ] Account Sign In
- [ ] Account Create
- [ ] Guest Mode Explanation
- [ ] Profile Setup
- [ ] Unit Preference
- [ ] Notification Permission Rationale
- [ ] Workout Plan List
- [ ] Workout History
- [ ] Warmup Prompt
- [ ] Safety / Readiness Check
- [ ] Overtime State
- [ ] Session Recovery Screen
- [ ] Set-by-Set Breakdown
- [ ] Save Notes / Reflection
- [ ] No Connection Empty State
- [ ] Settings Home
- [ ] Language Settings
- [ ] Units Settings
- [ ] Timer Settings
- [ ] How It Works
- [ ] Pace Explanation

### P2 Screens

- [ ] Health/Fitness Permission Rationale
- [ ] Progress Overview
- [ ] Share Result
- [ ] Sync Success State
- [ ] Local Data Conflict Resolution
- [ ] Notification Settings
- [ ] Accessibility Settings
- [ ] Account Settings
- [ ] Privacy / Data Settings
- [ ] FAQ / Help
- [ ] Contact Support

### P3 Screens

- [ ] Location Permission Rationale
- [ ] Advanced analytics
- [ ] Social/share hub
- [ ] Coach feedback
- [ ] Wearable companion surfaces
- [ ] Widgets / Live Activities / Foreground notification extensions

---

## 23. Summary

The app needs more than the active timer screen to feel complete. The core product should include a full journey from language selection through workout discovery, preparation, active 4-set execution, offline-safe completion, session summary, and settings.

The strongest MVP should focus on:

1. bilingual language foundation
2. home and workout detail
3. 4-set active timer
4. pause, complete, rest, and exit states
5. offline cached and sync-pending behavior
6. session completion and summary
7. settings for language, units, and timer behavior
8. iOS and Android accessibility parity

This scope is sufficient to turn the current validated technical baseline into a coherent, production-oriented mobile app experience.
