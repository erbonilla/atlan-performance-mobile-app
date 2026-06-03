# Atlan Mobile: 4-Set Threshold Timer Interaction Specification

**Scope:** Mobile implementation documentation for the interactive 4-set threshold timer flow shown in the reference screen.  
**Platforms:** iOS and Android.  
**Primary use case:** A user performs a structured workout interval, completing 4 sets with a live countdown timer, cached/offline support, and large swipe/tap actions for Pause and Complete.

---

## 1. Reference Screen Summary

The reference screen represents an active workout interval:

- User is offline, but the session is cached locally.
- Current workout position is **Set 2 of 4**.
- Activity type is **Threshold**.
- Distance or interval label is **100m**.
- Live countdown timer shows **1:32**.
- Target pace is **1:35**.
- Bottom action zone is split into two large panels:
  - **Pause** on the left, Abyss-toned panel.
  - **Complete** on the right, Coral panel.
- The interaction model supports directional gestures:
  - Swipe left to pause.
  - Swipe right to complete.
- Back navigation is available through a circular top-left icon.

This screen should feel intentionally minimal, high-contrast, and thumb-friendly during physical activity.

---

## 2. Core Interaction Model

The 4-set timer flow should operate as a stateful workout session, not as isolated screens.

### Main states

| State | Description | Primary UI |
|---|---|---|
| `ready` | Set is loaded but timer has not started. | Shows set details and Start action. |
| `active` | Timer is counting down. | Shows live countdown, Pause and Complete actions. |
| `paused` | Timer is stopped temporarily. | Shows Resume, Complete, and End options. |
| `completed_set` | Current set has been completed. | Shows transition to next set or session summary. |
| `rest` | Optional rest interval between sets. | Shows rest countdown and Skip Rest. |
| `completed_session` | All 4 sets are complete. | Shows completion summary. |
| `offline_cached` | Session is stored locally while offline. | Shows Offline · Cached indicator. |
| `sync_pending` | User action occurred offline and needs upload. | Shows Pending Sync indicator. |
| `sync_failed` | Local session could not sync. | Shows retry affordance. |
| `error` | Timer/session cannot proceed. | Shows recoverable error state. |

---

## 3. Four-Set Flow

The workout should progress through four sets using a deterministic state machine.

### Set sequence

1. Set 1 of 4 · Threshold
2. Set 2 of 4 · Threshold
3. Set 3 of 4 · Threshold
4. Set 4 of 4 · Threshold
5. Session complete summary

### Active set behavior

For each set:

1. Load the current set metadata from the shared KMP core.
2. Display the current set index as `Set N of 4`.
3. Display the interval label, for example `100m`.
4. Display the activity type, for example `Threshold`.
5. Start or resume the countdown timer.
6. Persist timer progress locally at regular intervals.
7. Allow Pause, Complete, and Back behavior.
8. When the timer reaches zero, transition automatically according to the workout rule:
   - auto-complete the set, or
   - show an overtime state if the athlete must manually complete.

Recommended default: when the countdown reaches zero, keep the screen active but change the timer to an overtime presentation until the user completes the set. This avoids accidental set completion during real-world movement.

---

## 4. Timer Behavior

### Timer source of truth

The timer should be derived from monotonic time, not from repeated decrementing UI ticks.

Use this model:

- Store `setStartedAtMonotonic`.
- Store `setDurationMs`.
- Store `accumulatedPausedMs`.
- Store `pauseStartedAtMonotonic` when paused.
- Calculate remaining time as:

```text
remainingMs = setDurationMs - (nowMonotonic - setStartedAtMonotonic - accumulatedPausedMs)
```

This prevents drift when the app is backgrounded, the UI thread is delayed, or the device is under load.

### Display format

Use:

```text
M:SS
```

Examples:

- `1:32`
- `0:08`
- `0:00`

For overtime, use either:

```text
+0:04
```

or keep `0:00` and show a small `Over target` status. The `+0:04` option is more precise.

### Tick frequency

- Update the visible timer every 250–1000 ms.
- Use 1000 ms for battery efficiency unless the design requires smoother countdown transitions.
- All business logic should remain based on monotonic elapsed time, not visual tick count.

### App backgrounding

When the app enters background:

1. Persist the active session state.
2. Persist the monotonic start references and wall-clock fallback timestamps.
3. Stop high-frequency UI updates.
4. On return, recalculate remaining time from elapsed time.
5. If the set expired while backgrounded, enter the expired/overtime state rather than silently completing unless product policy says otherwise.

---

## 5. Screen Anatomy

### Top area

Elements:

- Status bar.
- Circular back button.
- Session status text: `Offline · Cached`, `Online`, `Pending Sync`, or `Sync Failed`.
- Set metadata: `Set 2 of 4 · Threshold`.

Interaction:

- Back button should not immediately discard active progress.
- If the timer is active, tapping Back should open a confirmation sheet:
  - Continue workout
  - Pause and exit
  - End workout

### Main content area

Elements:

- Large interval label: `100m`.
- Activity type: `Threshold`.
- Live countdown: `1:32`.
- Target pace: `Target pace 1:35`.

Behavior:

- The large label remains stable during the set.
- Timer updates should not cause layout shift.
- Use tabular numerals for timer text if the font supports it.
- Timer should be readable at arm’s length.

### Bottom action area

Elements:

- Left action: Pause.
- Right action: Complete.

Behavior:

- Both zones should be large enough for imprecise taps during motion.
- Use fixed bottom positioning.
- Respect safe-area insets.
- Preserve the large rounded bottom corners from the device edge treatment.

---

## 6. Pause Interaction

### Entry methods

The user can pause by:

1. Tapping the Pause panel.
2. Swiping left if gesture support is enabled.
3. Using an accessibility action named `Pause set`.

### Pause transition

When paused:

- Stop elapsed active time accumulation.
- Preserve current remaining time.
- Change status text to `Paused`.
- Replace bottom actions with:
  - Resume
  - Complete

Recommended paused layout:

```text
Paused
1:32 remaining

[Resume] [Complete]
Secondary: End workout
```

### Resume behavior

On resume:

1. Add pause duration to `accumulatedPausedMs`.
2. Return to `active` state.
3. Continue countdown from the preserved remaining time.

### Pause analytics

Track:

- set number
- time remaining at pause
- pause duration
- whether user resumed, completed, or exited

---

## 7. Complete Interaction

### Entry methods

The user can complete by:

1. Tapping the Complete panel.
2. Swiping right if gesture support is enabled.
3. Using an accessibility action named `Complete set`.

### Completion confirmation

For a workout timer, accidental completion is plausible. Use one of these safeguards:

Preferred option:

- Require a committed swipe gesture for completion.
- Tapping Complete opens a lightweight confirmation only if the set has more than a configurable amount of time remaining.

Recommended threshold:

```text
If remaining time > 10 seconds, confirm early completion.
If remaining time <= 10 seconds, complete immediately.
```

Confirmation copy:

```text
Complete set early?
This will mark Set 2 as complete with 1:32 remaining.

[Keep going] [Complete set]
```

### After completion

If sets remain:

1. Persist set completion locally.
2. Mark sync state as `pending` if offline.
3. Advance to next set.
4. Show a short transition screen or rest state.

If Set 4 is completed:

1. Persist session completion locally.
2. Show session complete summary.
3. Queue sync if offline.

---

## 8. Gesture Design

### Swipe left to pause

- Direction: right-to-left.
- Minimum distance: 30–40% of screen width.
- Velocity threshold: allow a faster shorter swipe to commit.
- During drag, reveal the Pause state progressively.
- On release below threshold, snap back.
- On commit, trigger haptic feedback and enter paused state.

### Swipe right to complete

- Direction: left-to-right.
- Minimum distance: 30–40% of screen width.
- Velocity threshold: allow a faster shorter swipe to commit.
- During drag, reveal Coral completion affordance progressively.
- On release below threshold, snap back.
- On commit, trigger haptic feedback and complete the set.

### Conflict prevention

- Do not allow horizontal swipe gestures to conflict with system back gestures.
- Preserve iOS edge-swipe back behavior if the screen is inside a navigation stack.
- Prefer bottom-panel gesture capture rather than full-screen horizontal gesture capture.
- If full-screen gestures are used, exclude the left-edge system gesture zone.

---

## 9. Set Transition Pattern

After completing a set, show a brief transition. Keep it short and functional.

### Option A — Immediate next set

Use when workouts are continuous.

```text
Set 2 complete
Next: Set 3 of 4 · Threshold

[Start Set 3]
```

Auto-advance after 1–2 seconds if the product should minimize friction.

### Option B — Rest interval

Use when the workout includes rest between efforts.

```text
Set 2 complete
Rest
0:45
Next: 100m · Threshold

[Skip Rest]
```

When rest timer reaches zero, move to `ready` or `active` for the next set depending on product policy.

Recommended mobile default: show rest if prescribed by the workout. Otherwise use immediate next set.

---

## 10. Session Completion Screen

After Set 4 completes, show a summary screen.

Required content:

- `Workout complete`
- Total completed sets: `4 of 4`
- Total active time
- Average pace, if available
- Offline/sync status
- Primary action: `Done`
- Secondary action: `View details`, if available

Offline copy:

```text
Workout saved offline
Your results will sync when connection returns.
```

Sync success copy:

```text
Workout synced
```

Sync failure copy:

```text
Workout saved locally
Sync failed. Retry when you’re back online.
```

---

## 11. Offline and Cached Behavior

The screenshot shows `Offline · Cached`, so the timer flow must be offline-first.

### Requirements

- The user must be able to start, pause, resume, complete sets, and finish the session offline.
- All state transitions must be written to local storage immediately.
- Sync should be queued for later upload.
- UI should distinguish between:
  - cached workout data
  - locally saved progress
  - sync pending
  - sync failed

### Status labels

| Network/session condition | Label |
|---|---|
| Online and synced | `Online` |
| Offline with cached workout | `Offline · Cached` |
| Offline with unsynced progress | `Offline · Saved locally` |
| Online but upload pending | `Pending sync` |
| Sync failed | `Sync failed` |

### Sync policy

On reconnection:

1. Upload completed set events in chronological order.
2. Upload final session summary.
3. Resolve conflicts by event timestamp and local session ID.
4. Never discard local workout progress without explicit user consent.

---

## 12. Interaction States

### Active state

Visual behavior:

- Dark Abyss background.
- Large high-contrast interval label.
- Timer remains visually prominent.
- Bottom actions are fully visible.

Available actions:

- Pause
- Complete
- Back with confirmation

### Paused state

Visual behavior:

- Timer freezes.
- Status changes to `Paused`.
- Bottom actions change to Resume and Complete.
- Optional secondary destructive action: End workout.

Available actions:

- Resume
- Complete
- End workout
- Back

### Overtime state

Occurs when remaining time reaches zero and the set is not yet manually completed.

Visual behavior:

- Timer shows `+0:01`, `+0:02`, etc., or shows `0:00` with `Over target`.
- Complete action becomes more prominent.
- Pace comparison may show `Over target pace`.

Available actions:

- Complete
- Pause

### Early completion state

Occurs when user completes before timer reaches zero.

Visual behavior:

- Show confirmation if remaining time exceeds threshold.
- Then transition to next set or rest.

### Disabled state

Use disabled states only when an action truly cannot occur.

Examples:

- Complete disabled while session state is loading.
- Pause disabled during final sync submission.

Do not disable actions due to offline mode.

### Error state

Examples:

- Cached workout missing.
- Local save failed.
- Timer state corrupted.

Recovery actions:

- Retry loading
- Restore last saved state
- Exit workout

---

## 13. Component Requirements

### Timer display component

Inputs:

- `remainingMs`
- `elapsedMs`
- `durationMs`
- `state`
- `showOvertime`

Behavior:

- Formats time consistently.
- Uses tabular numerals.
- Does not own business timing logic.
- Re-renders from state provided by shared core/view model.

### Set header component

Inputs:

- `currentSetIndex`
- `totalSets`
- `activityType`
- `networkStatus`
- `cacheStatus`

Output example:

```text
Offline · Cached
Set 2 of 4 · Threshold
```

### Bottom action component

Inputs:

- `leftAction`
- `rightAction`
- `state`
- `gestureEnabled`

Required features:

- Large tap zones.
- Gesture affordance labels.
- Haptic feedback on commit.
- Accessibility labels and hints.
- Safe-area padding.

### Confirmation sheet component

Used for:

- Early completion.
- Back during active workout.
- End workout.

Requirements:

- Native-feeling modal bottom sheet on both platforms.
- Clear primary and secondary actions.
- Destructive actions styled distinctly.

---

## 14. Navigation Pattern

### Recommended route structure

```text
LanguageSelection
  → WorkoutOverview
  → ActiveWorkoutSession
      → SetActive
      → SetPaused
      → Rest
      → SessionComplete
```

`SetActive`, `SetPaused`, and `Rest` can be internal substates of `ActiveWorkoutSession` rather than separate navigation destinations.

### Back behavior

From active set:

- Tapping back opens confirmation.
- System back on Android opens the same confirmation.
- iOS interactive pop should either be disabled during active timing or intercepted with confirmation where technically feasible.

From paused set:

- Back may return to WorkoutOverview after saving paused state.

From completion summary:

- Back should not return to an active completed timer.
- Route should reset to WorkoutOverview or Home.

---

## 15. Layout Rules

### Safe areas

Respect:

- iOS Dynamic Island / notch.
- Android status bar cutouts.
- Bottom home indicator / navigation bar.

Bottom action panels should extend visually to the bottom edge but keep text and gestures above the safe-area inset.

### Responsive sizing

Use proportional vertical structure:

- Top metadata area: compact.
- Main timer area: dominant.
- Bottom actions: fixed height, approximately 25–30% of screen height.

For small screens:

- Reduce vertical spacing before reducing timer size.
- Keep action labels readable.
- Preserve minimum touch targets.

For large screens:

- Keep content centered.
- Avoid oversized bottom controls beyond ergonomic reach.

### Touch target minimums

- Minimum tappable area: 44 × 44 pt on iOS.
- Minimum tappable area: 48 × 48 dp on Android.
- Bottom actions should greatly exceed minimums.

---

## 16. Motion and Feedback

### Haptics

Use platform haptics sparingly:

| Action | Haptic |
|---|---|
| Pause committed | Light impact |
| Resume | Light impact |
| Complete set | Medium impact or success notification |
| Final session complete | Success notification |
| Invalid gesture snapback | No haptic or subtle warning |

### Animation

Recommended transitions:

- Timer text: no aggressive animation; avoid distraction.
- Bottom action drag: follows finger.
- Gesture commit: quick slide/settle animation.
- Set transition: 150–250 ms fade/slide.
- Rest-to-next-set: subtle transition.

Respect reduced-motion settings:

- Disable non-essential motion.
- Preserve state clarity through immediate changes.

---

## 17. Accessibility

### VoiceOver and TalkBack

Provide meaningful labels:

- Back button: `Back`
- Timer: `1 minute 32 seconds remaining`
- Set metadata: `Set 2 of 4, Threshold`
- Pause action: `Pause set. Swipe left or double tap to pause.`
- Complete action: `Complete set. Swipe right or double tap to complete.`

### Dynamic type / font scaling

- Support system font scaling where possible.
- Avoid clipping timer text.
- If large accessibility text is enabled, allow metadata to wrap before shrinking critical timer text.

### Color and contrast

- Do not rely on Coral alone to indicate completion.
- Include text labels and directional hints.
- Ensure all text on Abyss and Coral backgrounds meets WCAG AA contrast.

### Screen reader announcements

Announce important events:

- `Set 2 started`
- `30 seconds remaining`
- `10 seconds remaining`
- `Set paused`
- `Set 2 complete`
- `Workout complete`

Avoid announcing every timer tick.

---

## 18. iOS Implementation Notes

### Architecture

Recommended stack:

- SwiftUI for screen composition.
- KMP shared core for workout/session state.
- Observable view model or equivalent adapter for SwiftUI binding.
- Monotonic timing via platform clock abstractions exposed by shared core where possible.

### Lifecycle

Handle:

- `scenePhase == .background`
- `scenePhase == .inactive`
- `scenePhase == .active`

On background:

- Persist session state.
- Stop UI timer subscription.

On foreground:

- Rehydrate state.
- Recalculate remaining time.
- Resume UI updates if active.

### Gestures

Use SwiftUI drag gesture or UIKit-backed gesture only on the bottom action zone.

Consider:

- preserving system back gesture
- adding minimum distance threshold
- using predicted end translation for velocity-based commit

### Haptics

Use:

- `UIImpactFeedbackGenerator` for pause/resume
- `UINotificationFeedbackGenerator` for set/session completion

### Safe areas

- Use safe-area-aware layout.
- Bottom action background can ignore bottom safe area visually.
- Text inside the action zone should respect bottom safe area.

### Live Activities / notifications

Optional future enhancement:

- iOS Live Activity for active workout timer.
- Local notification if workout remains active in background.

Do not add these until the base timer state machine is stable.

---

## 19. Android Implementation Notes

### Architecture

Recommended stack:

- Jetpack Compose for screen composition.
- KMP shared core for workout/session state.
- ViewModel exposes state as `StateFlow`.
- Compose collects state lifecycle-aware.

### Lifecycle

Handle:

- `onPause`
- `onStop`
- `onResume`
- process recreation

On background:

- Persist session state.
- Stop UI-only ticker.

On resume:

- Rehydrate from local storage.
- Recalculate remaining time.

### Gestures

Use Compose pointer input or draggable state on the bottom action zone.

Consider:

- horizontal drag thresholds
- velocity-based completion
- snapback animation
- conflict with Android back gesture navigation

### Android Back

Use a back handler while active:

- If active, show confirmation.
- If paused, allow exit after saving paused state.
- If session complete, navigate back to overview/home.

### Haptics

Use platform haptic feedback through Compose/local view APIs:

- light haptic for pause/resume
- stronger/success feedback for completion where available

### Foreground service consideration

If the timer must remain highly reliable while backgrounded for long durations, evaluate an Android foreground service. If the timer can be recalculated on resume from timestamps, a foreground service may not be necessary for the initial implementation.

---

## 20. Shared KMP Core Responsibilities

The shared core should own workout business logic.

Recommended responsibilities:

- set sequence
- current set index
- total set count
- workout status
- timer calculation
- pause/resume/complete rules
- offline event queue
- sync state model
- persisted session restoration
- localized strings where already established

The native apps should own:

- rendering
- platform gestures
- haptics
- safe-area behavior
- platform lifecycle adapters
- native accessibility APIs

---

## 21. Suggested State Model

```kotlin
data class WorkoutSessionState(
    val sessionId: String,
    val currentSetIndex: Int,
    val totalSets: Int,
    val activityType: String,
    val intervalLabel: String,
    val targetPaceLabel: String,
    val timerState: TimerState,
    val remainingMs: Long,
    val elapsedMs: Long,
    val networkState: NetworkState,
    val syncState: SyncState,
    val isOfflineCached: Boolean
)

enum class TimerState {
    Ready,
    Active,
    Paused,
    Overtime,
    CompletedSet,
    Rest,
    CompletedSession,
    Error
}

enum class SyncState {
    Synced,
    Pending,
    Failed
}

enum class NetworkState {
    Online,
    Offline
}
```

---

## 22. Suggested Events

```kotlin
sealed interface WorkoutSessionEvent {
    data object StartSet : WorkoutSessionEvent
    data object PauseSet : WorkoutSessionEvent
    data object ResumeSet : WorkoutSessionEvent
    data object CompleteSet : WorkoutSessionEvent
    data object ConfirmEarlyComplete : WorkoutSessionEvent
    data object CancelEarlyComplete : WorkoutSessionEvent
    data object StartNextSet : WorkoutSessionEvent
    data object SkipRest : WorkoutSessionEvent
    data object EndWorkout : WorkoutSessionEvent
    data object RetrySync : WorkoutSessionEvent
    data object BackRequested : WorkoutSessionEvent
}
```

---

## 23. Acceptance Criteria

The implementation is successful when:

1. The user can complete all 4 sets from start to finish.
2. The timer counts down accurately without visual drift.
3. Pause freezes the timer and Resume continues from the same remaining time.
4. Complete advances to the next set or session summary.
5. Set labels update correctly: Set 1 of 4 through Set 4 of 4.
6. Offline mode allows the full workout to be completed.
7. Local state survives app backgrounding.
8. Local state survives app restart where feasible.
9. Sync pending and sync failed states are visible and recoverable.
10. iOS back behavior does not accidentally discard workout progress.
11. Android back behavior does not accidentally discard workout progress.
12. Swipe gestures do not conflict with platform navigation gestures.
13. VoiceOver and TalkBack users can pause, resume, and complete sets.
14. Text remains readable with larger accessibility font settings.
15. All action zones meet platform touch-target guidelines.
16. Final session completion is shown after Set 4.

---

## 24. Implementation Checklist

### Shared core

- [ ] Define session state model.
- [ ] Define timer state machine.
- [ ] Use monotonic time for countdown calculation.
- [ ] Add pause/resume accounting.
- [ ] Add set completion event.
- [ ] Add session completion event.
- [ ] Add offline event queue.
- [ ] Add local persistence.
- [ ] Add sync status model.
- [ ] Add localized strings.

### iOS

- [ ] Bind SwiftUI screen to KMP state.
- [ ] Render active timer screen.
- [ ] Implement bottom action tap zones.
- [ ] Implement swipe-left pause gesture.
- [ ] Implement swipe-right complete gesture.
- [ ] Add confirmation sheet for early completion.
- [ ] Add active-back confirmation.
- [ ] Handle scene phase changes.
- [ ] Add haptics.
- [ ] Add VoiceOver labels and announcements.

### Android

- [ ] Bind Compose screen to KMP `StateFlow`.
- [ ] Render active timer screen.
- [ ] Implement bottom action tap zones.
- [ ] Implement swipe-left pause gesture.
- [ ] Implement swipe-right complete gesture.
- [ ] Add confirmation dialog/sheet for early completion.
- [ ] Add back handler.
- [ ] Handle lifecycle resume/stop.
- [ ] Add haptics.
- [ ] Add TalkBack labels and announcements.

### QA

- [ ] Test Set 1 → Set 4 full completion.
- [ ] Test pause/resume on each set.
- [ ] Test early complete on each set.
- [ ] Test timer expiry/overtime.
- [ ] Test offline full completion.
- [ ] Test reconnect sync.
- [ ] Test app background/foreground.
- [ ] Test app restart recovery.
- [ ] Test iOS system back/edge gesture.
- [ ] Test Android system back gesture.
- [ ] Test VoiceOver.
- [ ] Test TalkBack.
- [ ] Test large text.
- [ ] Test reduced motion.

---

## 25. Recommended Next Build Order

1. Implement shared KMP timer state machine.
2. Add local persistence for session state.
3. Wire iOS active screen to shared state.
4. Wire Android active screen to shared state.
5. Add pause/resume events.
6. Add complete/next-set events.
7. Add Set 4 completion summary.
8. Add offline event queue and sync states.
9. Add gesture polish and haptics.
10. Add accessibility announcements.
11. Run full four-set QA on both platforms.

