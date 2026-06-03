# Atlan Performance - Framework Recommendation

Version: 2026-06-02  
Prepared for: Mobile Atlan Performance product  
Scope: Native iOS and Android mobile application, launched product, offline-first, bilingual ES/EN, poolside-ready endurance coaching.

---

## 1. Executive recommendation

Use **Kotlin Multiplatform (KMP) for the shared product core**, with **fully native UI on each platform**:

- **Shared core:** Kotlin Multiplatform
- **iOS UI:** SwiftUI, with UIKit where lower-level control is needed
- **Android UI:** Jetpack Compose
- **Shared data/sync/domain logic:** KMP modules
- **Platform storage:** SQLite-backed local persistence on both platforms, exposed through shared repositories where practical
- **Background sync:** native platform workers on each OS
- **Health integrations:** platform-specific adapters, optional in MVP

This is the strongest framework choice because Atlan is not just a content app. It has high-stakes native interaction needs: offline-first session execution, wet-hand poolside gestures, haptics, accessibility, bilingual copy parity, native notifications, local-first sync, and possible HealthKit / Health Connect integrations. KMP lets the team share the product logic that must remain consistent across platforms while keeping the interaction layer native.

The recommendation is **not** full shared UI with Flutter, React Native, or Compose Multiplatform for the entire app. Those can work for many apps, but Atlan's product value depends on platform fidelity and tactile native behavior. The UI should feel like a first-class iOS app on iPhone and a first-class Android app on Android.

---

## 2. Why KMP plus native UI is the best fit

### 2.1 It preserves native UX fidelity

Atlan has native mobile moments where tiny differences matter:

- Wet Mode directional gestures with very large hit zones
- Haptic confirmation for set completion
- Offline cached workout display
- Bottom sheets that respect each OS convention
- Native notification handling for Session Swapper
- VoiceOver and TalkBack semantics
- Dynamic Type and Android font scaling
- Platform-specific back behavior
- HealthKit and Health Connect adapters

SwiftUI and Jetpack Compose are the right UI tools for those surfaces. They allow each platform to follow its own conventions instead of forcing a cross-platform abstraction into two OS ecosystems.

### 2.2 It avoids duplicating the product brain

The following logic should not diverge between iOS and Android:

- Training-plan data model
- Session Swapper rules
- Offline cache policy
- Sync conflict rules
- Why-content lookup
- Bilingual copy keys and fallback behavior
- Measurement and telemetry event definitions
- Training-load summaries
- Plan recalculation primitives
- Permission and onboarding state machines

KMP is well suited for this shared layer because it is designed to reuse code across platforms while keeping the benefits of native programming.

### 2.3 It supports a launched product roadmap

Atlan is intended as a real product, not a prototype. A launched app needs:

- Reliable local persistence
- Testable business logic
- Clear sync behavior
- Platform-specific UX quality
- Upgradeable architecture
- Strong accessibility
- Direct access to OS APIs

KMP gives the team a shared core without locking the UI into a cross-platform renderer.

---

## 3. Recommended architecture

```text
atlan-mobile/
  shared-core/                       # Kotlin Multiplatform
    domain/
      TrainingPlan
      Session
      SessionSet
      SwapProposal
      WhyConcept
      UserPreferences
    usecases/
      GetTodaySession
      StartSession
      CompleteSet
      PauseSession
      ProposeSessionSwap
      AcceptSessionSwap
      ResolveWhyConcept
      UpdateLanguage
      SyncPendingWrites
    data/
      repositories/
      local/
      remote/
      sync/
      mappers/
    i18n/
      CopyKeys
      LocaleResolver
      CopyParityRules
    telemetry/
      EventNames
      EventPayloads
    tests/

  ios-app/                           # SwiftUI native shell
    Screens/
    Components/
    PlatformAdapters/
      LocalDatabaseAdapter
      NotificationAdapter
      HapticsAdapter
      HealthKitAdapter
      BackgroundSyncAdapter
    Resources/

  android-app/                       # Jetpack Compose native shell
    screens/
    components/
    platformadapters/
      RoomDatabaseAdapter
      NotificationAdapter
      HapticsAdapter
      HealthConnectAdapter
      WorkManagerSyncAdapter
    resources/
```

---

## 4. Shared KMP core responsibilities

The KMP shared layer should own behavior that must be identical on iOS and Android.

### 4.1 Domain models

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
LanguagePreference
NotificationCadence
OfflineCachePolicy
SyncQueueItem
```

### 4.2 Use cases

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

### 4.3 Rules that must be shared

- Do not mark disruption as failure.
- Do not create streak, leaderboard, rank, or red missed-session states.
- If the planned session no longer fits, propose a swap or a calm skip state.
- Cache today's session, next-session data, and relevant Why content locally.
- Any workout completion event writes locally first.
- Language choice happens before account creation.
- Spanish and English copy must use equal hierarchy; Spanish must wrap, not shrink.

---

## 5. Platform-specific UI responsibilities

### 5.1 iOS app

Use SwiftUI as the default UI framework.

Native iOS responsibilities:

- NavigationStack-based flow architecture
- Native sheet presentation for Why Modal and Session Swapper
- SwiftUI gestures for Wet Mode, with UIKit fallback if gesture arbitration needs lower-level control
- UIImpactFeedbackGenerator or Core Haptics for completion feedback
- UserNotifications for Session Swapper reminders
- BackgroundTasks for sync triggers
- HealthKit adapter if health data integration enters scope
- Dynamic Type, VoiceOver labels, Reduce Motion support
- App Intents or widgets later, only after core flows stabilize

### 5.2 Android app

Use Jetpack Compose as the default UI framework.

Native Android responsibilities:

- Compose Navigation or Navigation 3-compatible architecture
- ModalBottomSheet for Why Modal and Session Swapper
- Pointer input / draggable gestures for Wet Mode
- HapticFeedback for completion feedback
- Room for local persistence where platform-specific storage is preferred
- WorkManager for sync queue draining
- DataStore for user preferences where appropriate
- Notification channels for Session Swapper
- Health Connect adapter if health data integration enters scope
- TalkBack semantics, font scaling, predictive back behavior

---

## 6. Framework comparison

| Option | Fit | Strengths | Risks for Atlan | Verdict |
|---|---:|---|---|---|
| KMP shared core + SwiftUI + Jetpack Compose | Excellent | Native UI fidelity, shared business logic, direct platform APIs, strong offline architecture | Requires iOS and Android UI skill sets | Recommended |
| Fully separate native apps | Strong | Maximum platform control, least abstraction | Duplicate logic, higher long-term maintenance cost, logic drift risk | Viable but less efficient |
| Flutter | Good for many apps | Fast cross-platform UI, strong tooling, single codebase | UI is rendered through Flutter; native pattern fidelity and Health/API edge cases may require more bridge work | Not first choice |
| React Native | Good for JS-heavy teams | Large ecosystem, native component model, faster staffing in some markets | More moving parts around native modules, offline sync, gestures, haptics, and long-term platform drift | Not first choice |
| Compose Multiplatform shared UI | Promising | Shared UI in Kotlin, good for consistent product surfaces | iOS-native fidelity and platform convention nuance are weaker than SwiftUI for a premium iOS product | Consider only for internal tools or low-risk screens |

---

## 7. Why not Flutter as the primary recommendation

Flutter is a strong cross-platform framework, especially for highly custom visual apps and teams that want one UI codebase. It could implement Atlan. The issue is product fit: Atlan is positioned as a native iOS/Android coaching product, not a visually identical app shell on both platforms.

Atlan needs system-level excellence in:

- Native gestures
- Haptics
- Accessibility
- Notifications
- Background work
- App lifecycle behavior
- OS-specific health integrations
- Sheet/navigation/back behavior

Flutter can bridge those areas, but the more Atlan leans into native surface quality, the less valuable a shared UI layer becomes.

---

## 8. Why not React Native as the primary recommendation

React Native is productive for teams with deep React/TypeScript skill and can ship high-quality mobile apps. It is not the best default here because Atlan's highest-value interactions are not standard feed, form, commerce, or dashboard interactions.

The product's competitive edge depends on:

- Low-latency poolside workout interactions
- Very reliable offline writes
- Gesture confidence and haptic confirmation
- Platform-native accessibility
- Background sync correctness
- Direct platform APIs

React Native can support those through native modules, but KMP plus native UI keeps those areas closer to the OS while still sharing the product brain.

---

## 9. Storage and sync recommendation

Use an offline-first architecture with local storage as the immediate source of truth.

Minimum behavior:

- Read today's session from local storage.
- Write workout actions locally first.
- Queue sync operations while offline.
- Drain queue when connectivity returns.
- Keep a local event log for set completions, pauses, swaps, and skips.
- Use deterministic conflict resolution.
- Never block Wet Mode on network availability.

Recommended sync model:

```text
UI action
  -> platform UI event
  -> shared KMP use case
  -> local write
  -> UI updates immediately from local state
  -> sync queue item created
  -> platform background worker drains queue later
  -> remote success updates local sync status
```

---

## 10. MVP implementation phases

### Phase 1 - Product skeleton

- KMP shared module setup
- iOS SwiftUI app shell
- Android Compose app shell
- Language selection
- Onboarding flow
- Today dashboard with local mock data
- Shared design tokens

### Phase 2 - Offline session core

- Local database
- Today session repository
- Daily session detail
- Start session
- Wet Mode
- Local set-completion writes
- Offline status states

### Phase 3 - Why and Session Swapper

- Why content model and modal
- Citation rendering
- Session Swapper proposal logic
- Accept / skip flows
- Plan recalculation placeholder
- Notification copy and interaction

### Phase 4 - Production hardening

- Sync queue
- Conflict resolution
- Accessibility audit
- Localization QA
- Error states
- Analytics schema
- Crash reporting
- Performance profiling

### Phase 5 - Integrations

- HealthKit adapter
- Health Connect adapter
- Calendar-aware interruption detection
- Wearable companion exploration

---

## 11. Implementation acceptance criteria

The framework decision is successful if:

- iOS screens feel native to iOS, not ported from Android.
- Android screens feel native to Android, not ported from iOS.
- Domain behavior is tested once in the shared KMP layer.
- The same session-swap logic produces the same outcome on both platforms.
- Wet Mode works without network on both platforms.
- Workout completion writes locally within one interaction frame.
- Bilingual copy parity is enforced by tests or review checks.
- No team has to rewrite plan logic twice.

---

## 12. Source notes

Sources reviewed on 2026-06-02:

- Kotlin Multiplatform: https://kotlinlang.org/multiplatform/
- Compose Multiplatform and Jetpack Compose relationship: https://kotlinlang.org/docs/multiplatform/compose-multiplatform-and-jetpack-compose.html
- Jetpack Compose: https://developer.android.com/compose
- Android offline-first architecture: https://developer.android.com/topic/architecture/data-layer/offline-first
- SwiftUI: https://developer.apple.com/documentation/swiftui/
- Apple Human Interface Guidelines: https://developer.apple.com/design/human-interface-guidelines/
- React Native docs: https://reactnative.dev/docs/getting-started
- Flutter docs: https://docs.flutter.dev/learn/pathway
