# Atlan Mobile — Screen Specifications

Per-screen specs for the implemented build, following the §15 template in
`atlan-mobile-design-patterns.md`. Grounded in the implemented SwiftUI (iOS) and Jetpack Compose
(Android) code and verified on the iPhone 17 simulator + Pixel 7 emulator.

The original setup-prompt milestone was 10 screens (§1–§10 below). The build has since added
in-flow surfaces that the active-session and offline work required — **Workout Prep** (§11),
**Gesture Tutorial** (§12, a one-time coach mark), a reusable **Generic Error** surface (§13), and a
brief **Branded Launch / splash** (§14) — plus a **sync-status** block in the Wet Mode summary, a
live **rest-between-sets** timer preference in Settings, and **preference persistence** (all
documented inline in the relevant sections and in the cross-cutting notes). These remain inside the
no-backend scope: fakes/in-memory only, with TODO anchors for the real storage/sync layer.

**Navigation model.** A brief branded launch (§14) precedes a single linear stack: `language →
welcome → calibration → profileSetup → tunedSummary → dashboard`. **Bootstrap:** returning users
(onboarding already complete) start straight on the `dashboard`, skipping the onboarding stack (§20).
From the dashboard, `sessionDetail → workoutPrep → wetMode` is the session flow; the dashboard also
opens `workoutPlan` (§15, the week's sessions), and `settings` opens `howItWorks` (§16), `history`
(§18), and `progress` (§19). Plus two modal sheets (Why Modal, Session Swapper) presented over any
screen and a one-time gesture coach mark drawn as an overlay on first Wet Mode entry. The Generic
Error surface is not a route — it is a reusable view rendered in place when a screen's load fails
(currently wired to Session Detail). iOS uses `NavigationStack` + `.sheet`; Android uses a route
`when(...)` switch + `ModalBottomSheet`. Routes: `AppRoute` (iOS) / `AtlanRoute` (Android), incl.
`workoutPlan` / `WORKOUT_PLAN` and `howItWorks` / `HOW_IT_WORKS`.

**Cross-cutting rules.** Foam/Paper light surfaces; Abyss primary; Coral rare (productive action
only); Tide for science/selected/Why. No streaks, leaderboards, peer ranking, "missed", "behind
plan", flames, trophies, or red failure states anywhere. EN + ES are co-equal; long Spanish strings
wrap, never shrink. Copy comes from the shared KMP layer (`AtlanCopy` / `LocalizedStringKey`) where
keyed; screen-local copy is inline only where not yet keyed (flagged per screen below).

**Localization status.** Onboarding, dashboard, Session Detail, Workout Prep, Settings, the
Why/Swapper sheets, **and the full Wet Mode timer surface** are bilingual. The Wet Mode screen — the
gesture coach mark, the active/rest timer, the session summary, the two-zone actions and their
accessibility labels, the confirmation dialogs, and the sync-status block — resolves all copy through
the shared `AtlanCopy` / `LocalizedStringKey` layer (Android `WetModeScreen` now takes a `language`
parameter; iOS reads `coordinator.language`). The sync-status strings ("Pending sync" / "Syncing…" /
"Saved locally" / "Retry sync" and their sentences) are keyed in both languages on both platforms.
Templated strings (e.g. "X of N sets", "Set N", "Target pace …") interpolate via `AtlanCopy.format`
so word order stays per-language. (The numeric VoiceOver "N minutes M seconds remaining" label on iOS
is still composed in English — number-word grammar is a separate, lower-priority TODO.)

**Persistence.** Non-sensitive preferences — language, haptics, keep-screen-awake, rest-between-sets,
and the tutorial-seen flag — survive relaunch: iOS via `UserDefaults` (`AppCoordinator` `didSet`/
`init`), Android via `SharedPreferences` (`AtlanNavGraph`, using `commit()` for observable flushes on
the emulator). TODO: migrate to a shared `PreferencesRepository` when the real storage layer lands;
encrypted storage for any sensitive data is a separate TODO.

---

## 1. Language Selection

### Purpose
Let the user choose EN or ES before anything else. First canonical screen; establishes brand + the
co-equal bilingual principle.

### Entry Points
App launch (root of the stack).

### Exit Points
Selecting a language sets `coordinator.language` and pushes `welcome` (iOS: `selectLanguage`;
Android: nav sets `route = WELCOME`).

### Layout
FoamWarm full-bleed background. Vertically centered stack: wordmark `atlan` + rare Coral underline →
co-equal heading ("Choose your language" / "Elige tu idioma", same size + weight) → two Abyss pill
buttons (English, Español). Respects safe areas; `xl` screen padding.

### Components
Wordmark (text + Coral rule), `AtlanButton` ×2.

### Content
"Choose your language" / "Elige tu idioma", "English", "Español". Inline (not yet keyed) — these are
intentionally shown in both languages simultaneously, so they are not localized through `AtlanCopy`.

### States
- Default: both options equally weighted, no pre-selection.
- Pressed: `AtlanButton` press treatment.
- Loading/Error/Empty/Disabled: n/a (no async).

### Interactions
Tap English/Español → set language → advance. No default selection; no locale/IP inference; no flag
icons.

### Accessibility
Each option exposes its label + a role hint ("Language option" / "Opción de idioma"). Headings are
center-aligned and Dynamic-Type friendly. Contrast: Abyss on Foam, Foam text on Abyss fill — both AA.

### iOS Notes
`LanguageSelectionView`; `navigationBarHidden(true)`. Headings use `AtlanTypography.title`.

### Android Notes
`LanguageSelectionScreen`; headings 18sp `SemiBold` (co-equal). Buttons via `AtlanButton`
(contentDescription = label, `Role.Button`).

### QA Checklist
Foam bg ✓ · Abyss primary ✓ · Coral only on the underline ✓ · EN/ES equal weight ✓ · 48dp targets ✓
· verified on sim + emulator ✓.

---

## 2. Welcome

### Purpose
Frame Atlan as adaptive and autonomy-supportive ("Built for athletes with lives").

### Entry Points
From Language Selection.

### Exit Points
"Begin" → `calibration`. Back → `language` (iOS native chevron; Android `AtlanBackButton`).

### Layout
FoamWarm bg, leading-aligned: eyebrow (Tide) → display title (serif) → body → spacer → primary CTA
pinned near the bottom safe area.

### Components
`AtlanBackButton` (Android), `AtlanButton` (CTA).

### Content
`ONBOARDING_WELCOME_EYEBROW`, `_TITLE`, `_BODY`, `_CTA` from `AtlanCopy` (EN + ES).

### States
Default; Pressed (CTA + back). No async.

### Interactions
Begin advances; back returns. Long Spanish body wraps vertically (`fixedSize`/no shrink).

### Accessibility
Title/body use `fixedSize(vertical)` (iOS) so they never truncate under large type. Back control is a
labeled 44/48 target.

### iOS Notes
`WelcomeView`; native `NavigationStack` back.

### Android Notes
`WelcomeScreen`; `AtlanBackButton(contentDescription = "Atrás"/"Back")`.

### QA Checklist
Shared copy ✓ · EN/ES wrap ✓ · CTA press state ✓ · back target ✓.

---

## 3. Calibration

### Purpose
Tune coaching posture without scoring or judging. Answers become settings, never grades. One example
question is scaffolded for the initial build (full 4-question set is future work).

### Entry Points
From Welcome.

### Exit Points
"Continue" → `tunedSummary` (always enabled — selection is optional, not a gate). Back → `welcome`.

### Layout
FoamWarm bg. Top row: back control + "2 of 4" progress `AtlanPill`. Then eyebrow → display title →
question → list of `AtlanSelectableRow` options → spacer → Continue CTA.

### Components
`AtlanBackButton` (Android), `AtlanPill`, `AtlanSelectableRow` ×3, `AtlanButton`.

### Content
`CALIBRATION_SUBTITLE`, `CALIBRATION_TITLE` from `AtlanCopy`; the example question + options are
inline EN/ES (not yet keyed — flagged for future extraction).

### States
- Default: no option selected.
- Selected: chosen row shows TidePale fill + Tide border + **checkmark** (color-not-alone).
- Pressed: row + CTA press treatment.

### Interactions
Tap a row to select (single-select; tapping another moves selection). Continue advances regardless of
selection — there are "no right answers."

### Accessibility
Selected row carries the selected trait/semantics (VoiceOver/TalkBack announce state); selection is
never color-only (checkmark + border). Rows meet 44/48 minimum and the whole row is the target.

### iOS Notes
`CalibrationView`; options via `AtlanSelectableRow`.

### Android Notes
`CalibrationScreen`; options via `AtlanSelectableRow` (`Role.RadioButton`, `selected` semantics).

### QA Checklist
No good/bad framing ✓ · color-not-alone selection ✓ · Continue not gated ✓ · EN/ES ✓.

---

## 4. Tuned Summary

### Purpose
Reflect the inferred settings back to the user and preview the first session, then hand off to the
dashboard.

### Entry Points
From Calibration.

### Exit Points
Coral CTA ("See your first session" / shared `TUNED_CTA`) → `dashboard`.

### Layout
FoamWarm bg, leading-aligned: eyebrow → display title → a stack of Paper setting cards (title +
detail) → spacer → Coral CTA. Each setting is modeled so it can become editable later.

### Components
Paper setting cards (inline layout), `AtlanButton` (coral).

### Content
`TUNED_TITLE`, `TUNED_CTA` from `AtlanCopy`; the four setting rows are inline EN/ES (adaptive
scheduling, depth on demand, notifications, first session preview) — flagged for future keying.

### States
Default; Pressed (CTA). No async.

### Interactions
CTA advances to the dashboard. Cards are display-only in this milestone.

### Accessibility
Detail text wraps (no shrink). The Coral CTA is the single high-signal action.

### iOS Notes
`TunedSummaryView`. ### Android Notes `TunedSummaryScreen`.

### QA Checklist
Coral used only on the productive CTA ✓ · settings framed as adaptive, never as judgement ✓.

---

## 5. Today Dashboard

### Purpose
Answer only two questions: *What should I do today?* and *Is the week still on track?* Nothing else.

### Entry Points
From Tuned Summary (and is the app's home once reached).

### Exit Points
Start session / today card → `sessionDetail`; "Adjust today" → Session Swapper sheet; metric/Today
`i` → Why Modal sheet; gear → `settings`.

### Layout
FoamWarm scroll. Quiet header (wordmark, date, gear). Abyss "today" hero card (label, big distance,
threshold + Why `i`, Coral "Start session", "Adjust today"). Paper weekly-arc card (calm Tide line +
single Coral current-week marker). Paper metric chips (each with inline Why `i`).

### Components
`AtlanButton` (coral Start), `AtlanInfoButton`, `AtlanMetricChip`, weekly-arc chart, gear button.

### Content
Driven by `DashboardState` from `GetTodayDashboardUseCase` (today session, weekly arc, metric chips,
`todayWhyConceptKey`, date label). Card labels currently inline.

### States
- Loading: `ProgressView`/spinner while `DashboardState` resolves.
- Loaded: today card + weekly arc + chips.
- No streak/leaderboard fields exist in the state by design (asserted in `GetTodayDashboardUseCaseTest`).

### Interactions
Start opens Session Detail; Adjust opens the Swapper; `i` opens the Why Modal; gear opens Settings.
The whole-card tap was removed so Start/Adjust are the unambiguous targets.

### Accessibility
Gear has a localized label + 44/48 target. Each chip's `i` has a context-specific label ("Why {metric}
— open explanation"). Weekly arc is decorative around the textual "on track" status.

### iOS Notes
`TodayDashboardView`; `navigationBarBackButtonHidden(true)`; `WeeklyArcChart` via `Path`.

### Android Notes
`TodayDashboardScreen`; arc via `Canvas`; gear is a labeled `Role.Button` with 48dp min.

### QA Checklist
Answers only today + week ✓ · no streak/leaderboard/red ✓ · Coral only on Start + arc marker ✓ ·
clear single targets ✓.

> **Resolved (no-streak):** the Consistency chip now reads "Consistency · Steady · Sustainable
> pattern" — a qualitative, non-counting signal. The earlier "72 days" consecutive-day count was
> replaced because a breakable counter punishes the disruption Atlan exists to absorb.

---

## 6. Session Detail

### Purpose
Show the session structure with minimal cognitive load, working from cache offline.

### Entry Points
From the dashboard (Start / today card).

### Exit Points
"Start session" → `wetMode`. Threshold `i` → Why Modal. Back → `dashboard`.

### Layout
FoamWarm scroll. Optional "Offline · Cached" pill (when `offlineAvailable`) → date eyebrow → display
title → distance · duration → Paper structure card (sets, target pace, Why `i` on the threshold set)
→ Coral "Start session" CTA.

### Components
`AtlanBackButton` (Android), `AtlanPill`, `AtlanInfoButton`, `AtlanButton` (coral).

### Content
`SessionDetailState` from `GetTodaySessionUseCase` (session title, sets, pace, `whyConceptKey`,
`offlineAvailable`). Date label currently inline.

### States
- Loading: spinner while state resolves.
- Loaded: structure card.
- Offline: calm "Offline · Cached" pill (never alarming).

### Interactions
Start opens Wet Mode; `i` opens the Why Modal for the threshold concept.

### Accessibility
`i` is a 44/48 target; sets read top-to-bottom. Offline pill is informational, not an error.

### iOS Notes
`SessionDetailView`. ### Android Notes `SessionDetailScreen`.

### QA Checklist
Offline-first framing ✓ · Coral only on Start ✓ · Why one tap from the term ✓.

---

## 7. Why Modal

### Purpose
Deliver precise, sourced training science exactly one tap below the surface — no motivational copy.

### Entry Points
Any `i` affordance (dashboard chips, today card, session detail). Presented as a native sheet /
`ModalBottomSheet`.

### Exit Points
Close button (iOS) / drag-down + scrim (Android); dismiss clears the concept key.

### Layout
Scrollable sheet (medium/large detents on iOS; `heightIn(max)` on Android). Tide eyebrow → display
title → body → "Mechanism" bullets → "Reference" citation.

### Components
Close button (iOS, 44pt + press), sheet container (`AtlanBottomSheet` on Android).

### Content
`WhyModalState` from `GetWhyConceptUseCase`, language-aware. Seeded example: Threshold concept with a
Maglischo reference.

### States
- Loading: spinner.
- Loaded: concept (eyebrow/body/mechanisms/reference).
- Empty: calm "This explanation isn't cached yet…" / ES equivalent — **no error styling** (a
  not-yet-synced concept is normal offline behavior).

### Interactions
Read + scroll; close. No actions inside the science block.

### Accessibility
Close has a localized label; content scrolls; mechanism bullets and reference wrap.

### iOS Notes
`WhyModalView`. **Fixed:** the sheet did not inherit `@EnvironmentObject` (AppCoordinator /
SharedContainer) across the presentation boundary and crashed on open; both are now re-injected on the
sheet content in `AtlanPerformanceApp.swift`.

### Android Notes
`WhyModalSheet`; receives `shared` as a parameter (no environment-inheritance failure mode).

### QA Checklist
Sourced, no motivation copy ✓ · Tide concept label ✓ · calm empty state ✓ · opens without crash on
both platforms ✓.

---

## 8. Wet Mode — 4-Set Threshold Timer

Implements `atlan-4-set-timer-mobile-interactions.md`: a stateful, offline, wet-hands timer, not a
static screen.

### Purpose
Run the 4 main (paced) sets with a live countdown; source of truth is the monotonic clock + the local
completion write.

### Entry Points
"Begin session" from **Workout Prep** (§11), which sits between Session Detail and Wet Mode;
auto-starts Set 1. On the first-ever entry per install, the one-time Gesture Tutorial coach mark
(§12) is drawn over the screen and intercepts gestures until dismissed.

### Exit Points
Long swipe down / accessible "Exit Wet Mode" → Session Detail. Session-complete summary → "Done".

### Layout
AbyssDeep full-bleed; Foam text; extra-large numerics. Offline label → "Set N of 4 · Threshold" → huge
main metric (100m) → intensity → **live M:SS timer** → target pace → Paused/Over-target indicator.
Bottom two-zone action grid (Pause/Resume | Complete), Complete in CoralBright.

### Components
`WetModeActionZone` ×2; early-complete confirmation dialog; session-complete summary.

### Content / State
`WorkoutTimerState` (shared, pure) built by `StartWorkoutTimerUseCase` from the session's paced sets.
Phases: `READY → ACTIVE → (PAUSED) → OVERTIME → COMPLETED_SESSION`. Per-set completion writes
local-first via `CompleteWorkoutSet`.

### States
- **Active** — counting down (M:SS), derived from monotonic `nowMs` (drift-free, no UI-tick counters).
- **Paused** — timer frozen, "Paused", left zone becomes Resume; paused time excluded from the count;
  an "End session" affordance appears.
- **Overtime** — at zero the set does **not** auto-complete; shows "+M:SS" + "Over target" in Coral.
- **Rest** — between sets: "Set N complete", rest countdown (0:30), "Next · Set N+1 …", End | Skip Rest
  zones. Auto-starts the next set at zero.
- **Completed session** — summary: "Workout complete" / "Session ended" (partial), "X of N sets ·
  total elapsed", **set-by-set breakdown** (✓/○ per set), an optional **perceived-effort reflection**
  (Easy/Moderate/Hard tappable chips — no keyboard, color-not-alone ✓+Tide, local-only/TODO persist),
  the sync-status block (below), Done.
- Loading — spinner until the session resolves.

**Sync-status block (in the summary).** Surfaces offline-resilience using `OfflineStatus`, calm and
never red — data is always safe locally:
- **Pending sync** — `AtlanPill("Pending sync")` + "Saved offline. We'll sync when you're back
  online." with a **Retry sync** affordance.
- **Syncing…** — Tide spinner while a (simulated) drain runs (~1.2s).
- **Saved locally** — `AtlanPill("Saved locally")` + "Sync failed — your results are safe. Retry when
  you're back online." Still no red; the result is durable on device.
- These strings are keyed in EN + ES on both platforms (via `AtlanCopy` / `LocalizedStringKey`).
  TODO: real sync-queue drain — there is no backend in this milestone, so a retry lands on
  "Saved locally".

### Interactions
Swipe right / tap = Complete (or Skip Rest while resting); swipe left / tap = Pause/Resume; long swipe
down / accessible Exit = **exit confirmation** (never discards silently). **Early-complete
confirmation**: completing with > 10s remaining asks "Complete set early?" first; near the end it
completes immediately. Completing a non-final set enters Rest; the last set finishes the session.
**End session** (from Paused or Rest) asks "End this session?" then shows a partial summary. Haptics on
pause/complete/skip.

### Accessibility
Custom accessibility actions (Complete/Pause/Exit) so swipe is never the only path; Exit routes through
confirmation. Timer exposes a spoken "M minutes S seconds remaining" label; huge high-contrast type;
large non-precision zones. Transitions announce ("Set N complete. Rest.", "Workout complete").

### iOS Notes
`WetModeView`; monotonic `nowMs` via `DispatchTime.now().uptimeNanoseconds`; 1s `Timer.publish`
ticker; `.confirmationDialog` for early complete. **Fixed:** swapped `.gesture` → `.simultaneousGesture`
so the big Pause/Complete button *taps* fire (the full-screen `DragGesture` was swallowing them — a
latent bug since Wet Mode was never run before).

### Android Notes
`WetModeScreen`; monotonic `SystemClock.elapsedRealtime()`; `LaunchedEffect` 1s ticker;
`detectDragGestures` on the container with `clickable` zones (children take taps first, so no gesture
conflict); `AlertDialog` for early complete; `LocalHapticFeedback`.

### Not yet (TODO anchors)
Monotonic timer-state persistence across background/process-death + restore (and resume-on-relaunch)
— note this is separate from the preference persistence already shipped (language/haptics/keep-awake/
rest-between-sets/tutorial-seen); and a **real** sync engine behind the sync-status block. (Wet Mode
copy is now fully keyed for EN + ES on both platforms — Android `WetModeScreen` takes a `language`
parameter. Rest duration is now user-configurable in Settings; **per-set** duration remains a 105s
default until `SessionSet` carries an explicit duration field.)

### QA Checklist
Drift-free monotonic timer ✓ · pause excludes paused time ✓ · overtime never auto-completes ✓ ·
early-complete confirm ✓ · tap **and** swipe both work ✓ · auto-advance + summary ✓ · Coral only on
Complete ✓ · accessible non-swipe path ✓. Verified Set 1→4→summary on sim + emulator.

---

## 9. Session Swapper

### Purpose
Absorb life disruption without shame. Neither action is failure.

### Entry Points
"Adjust today" on the dashboard. Presented as a sheet / `ModalBottomSheet`.

### Exit Points
"Accept swap" (commits locally + queues sync, then dismiss) or "Skip today" (local update, dismiss).

### Layout
Sheet: "Session Adjusted" pill → empathy line ("Life happens.") → Original / Replacement / Weekly load
rows → affirmation → Coral "Accept swap" → text "Skip today".

### Components
`AtlanPill`, `AtlanButton` (coral, with loading), "Skip today" text action.

### Content
`SessionSwapperState` from `ProposeSessionSwapUseCase` (tag, empathy, original/replacement, weekly
load status, affirmation). Asserted no-shame in `ProposeSessionSwapUseCaseTest`.

### States
- Loading (initial): spinner while proposal resolves.
- Accepting: Accept shows a spinner + disables; Skip disabled (no double-submit).
- No warning/red states — weekly load reads "Still on track."

### Interactions
Accept or Skip; both update local state and dismiss. Neither is framed as a miss.

### Accessibility
Accept announces loading; Skip is a 44/48 target.

### iOS Notes
`SessionSwapperView`. **Fixed** alongside the Why Modal env-object crash (same re-injection).

### Android Notes
`SessionSwapperSheet`; `accepting` state drives `loading`/`enabled`.

### QA Checklist
No failure framing ✓ · weekly load never red ✓ · loading prevents double-submit ✓ · opens without
crash on both platforms ✓.

---

## 10. Settings

### Purpose
A light control surface. Four controls are **live** (language, haptics, keep-screen-awake, rest
between sets); the remaining inferred onboarding settings are surfaced as display rows so they can
become editable later. No account system in this milestone.

### Entry Points
Gear on the dashboard.

### Exit Points
Back → `dashboard`.

### Layout
FoamWarm scroll: back control → "Settings"/"Ajustes" display title → **Language** Paper row with an
English | Español segmented control → **Haptics** and **Keep screen awake** toggle rows → **Rest
between sets** Paper row with a 30s | 45s | 60s segmented control → display-only Paper rows
(notification cadence, explanation density, offline cache, About v0.1). Rows have a 56dp comfortable
min height.

### Components
`AtlanBackButton` (Android), Paper rows (inline), pill **segmented controls** (Abyss selected fill /
TidePale track) for language and rest, Material `Switch` toggles tinted to Tide/TideSoft.

### Content
Bilingual rows. Live controls: language, haptics, keep-screen-awake, rest between sets ("Descanso
entre series"). Display rows backed conceptually by `NotificationCadence` / `ExplanationDensity` enums
for later editing.

### States
- **Language** — segmented control reflects the current `Language`; selecting the other option
  re-renders the **whole app live** (verified propagating to the dashboard's "Ajustar hoy").
- **Haptics** — toggle gates all Wet Mode haptics.
- **Keep screen awake** — toggle drives iOS `isIdleTimerDisabled` / Android `View.keepScreenOn`
  during Wet Mode.
- **Rest between sets** — 30/45/60s segment; the chosen value feeds `StartWorkoutTimerUseCase`'s
  `restDurationMs`, so the next Wet Mode session's REST countdown uses it (no model change required —
  `WorkoutTimerState.restDurationMs` already existed).
- All four persist across relaunch (see the Persistence cross-cutting note). No async; no error path.

### Interactions
Tap a segment to switch language / rest duration immediately; flip a toggle to apply its preference
immediately. Back returns to the dashboard. Display rows are informational for now.

### Accessibility
Segments carry `selected` semantics + `Role.Button`; toggles expose on/off state. Rows have a
comfortable 56dp height; values wrap/trailing-align. Back is a labeled 44/48 target (iOS native
chevron).

### iOS Notes
`SettingsView`; preferences held in `AppCoordinator` (persisted via `UserDefaults`); rest segment
reuses the shared `segment(...)` pill builder.
### Android Notes
`SettingsScreen`; preferences hoisted into `AtlanNavGraph` (persisted via `SharedPreferences`);
`LanguageSegment` / `RestSegment` reuse the shared `Segment` composable, toggles use `SwitchDefaults`
Tide colors.

### QA Checklist
No account/auth ✓ · live EN↔ES switch verified on both platforms ✓ · haptics + keep-awake apply
immediately ✓ · rest-duration choice changes the next session's REST countdown ✓ · preferences durable
across restart ✓ · display rows map to future-editable model fields ✓.

---

## 11. Workout Prep

### Purpose
A final, calm readiness step before the active timer — confirm the session shape and offline
availability, offer a warm-up reminder, then start Wet Mode. Reduces the cognitive jump from a quiet
detail screen straight into a full-screen countdown.

### Entry Points
"Start session" on Session Detail → `workoutPrep` / `AtlanRoute.WORKOUT_PREP` (iOS file registered in
the `.xcodeproj`).

### Exit Points
"Begin session"/"Empezar sesión" (Coral) → `wetMode`. Back → `sessionDetail` (nothing is lost).

### Layout
FoamWarm, leading-aligned: back control → optional "Offline · Ready"/"Sin conexión · Listo" pill (when
`offlineAvailable`) → eyebrow ("Ready to begin"/"Listo para empezar") → display title → Paper readiness
card (Type · Sets `N × distance` · Target pace · Estimated time) → warm-up reminder line → spacer →
Coral "Begin session".

### Components
`AtlanBackButton` (Android), `AtlanPill`, Paper readiness card (inline rows, 44dp min), `AtlanButton`
(coral).

### Content
Bilingual (EN/ES). Reads `SessionDetailState` from `GetTodaySession` (title, sets, target pace,
`durationEstimateLabel`, `offlineAvailable`); "Threshold" type + the warm-up line are inline.

### States
- Loading — back control shows immediately; the card area waits on `getTodaySession()` (`produceState`).
- Loaded — readiness card + warm-up reminder.
- Offline — calm "Offline · Ready" pill, never alarming.

### Interactions
Begin advances to Wet Mode; back returns to Session Detail. Display-only card.

### Accessibility
Rows are ≥44dp; the warm-up line wraps; the Coral CTA is the single high-signal action.

### iOS Notes
`WorkoutPrepView`. ### Android Notes `WorkoutPrepScreen`.

### QA Checklist
Bilingual ✓ · offline-first framing ✓ · Coral only on Begin ✓ · back loses nothing ✓.

---

## 12. Gesture Tutorial (one-time coach mark)

### Purpose
Teach the Wet Mode swipe model once, so the gestures are never the only-discoverable path. Shown a
single time per install, then never again.

### Entry Points
Drawn as an overlay the first time Wet Mode is entered, gated on the persisted `tutorialSeen` flag
(false by default). While visible it intercepts gestures so the underlying timer isn't driven by accident.

### Exit Points
"Got it"/"Entendido" (Coral) dismisses and sets `tutorialSeen = true` (persisted) — the underlying
timer is already running beneath it.

### Layout
Scrim over AbyssDeep Wet Mode. "How it works"/"Cómo funciona" → three rows pairing a directional glyph
with its action (→ Complete, ← Pause, ↓ Exit) → Coral "Got it".

### Components
Inline overlay (not a reusable component); Coral `AtlanButton`; directional glyph rows.

### Content / Localization
**Bilingual on both platforms.** The coach mark title and the three gesture rows ("How it works" /
"Cómo funciona", "Swipe right or tap to Complete" / "Desliza a la derecha o toca para Completar", …)
resolve through the shared `AtlanCopy` / `LocalizedStringKey` layer (keys `wetMode.tutorial.*`). iOS
reads `coordinator.language`; Android receives a `language` parameter.

### States
First-entry only (visible) vs. seen (never shown). Persisted across relaunch, so it shows once per
install rather than once per launch.

### Interactions
Read three rows; dismiss. No other actions; gestures pass through only after dismissal.

### Accessibility
Each row reads glyph + action text; "Got it" is a 44/48 Coral target. Because the gestures it teaches
have accessible non-swipe equivalents in Wet Mode, the coach mark is reinforcement, not a gate on
operability.

### iOS Notes
`WetModeView.tutorialOverlay`; `coordinator.wetModeTutorialSeen` (UserDefaults).
### Android Notes
`WetModeScreen` tutorial overlay; `tutorialSeen`/`onTutorialSeen` hoisted to `AtlanNavGraph`
(SharedPreferences).

### QA Checklist
Shows once per install ✓ · dismiss persists ✓ · intercepts gestures while visible ✓ · iOS bilingual
✓ · Android bilingual ✓.

---

## 13. Generic Error surface

### Purpose
A reusable, calm fallback for unrecoverable load failures so a screen never strands the user — no
blame, no red, data-is-safe reassurance, with Retry and an optional safe exit.

### Entry Points
Not a route. Rendered in place by a host screen when its load fails. Currently wired as the Session
Detail load-failure fallback; **unreachable with the fake repositories** (they always succeed), so it
is verified by construction, not by a live failure.

### Exit Points
Retry → re-runs the host screen's load (e.g. Session Detail bumps its reload key). Optional secondary
"exit" text action → host-defined safe destination.

### Layout
Centered: Tide retry glyph (`↻` / `arrow.clockwise.circle`, ~44pt) → display title → message →
`AtlanButton` Retry → optional text exit action.

### Components
`AtlanErrorView` (iOS) / `AtlanErrorScreen` (Android) — see component specs.

### Content
Caller-provided and bilingual at the call site (e.g. Session Detail passes "We couldn't load the
session"/"No pudimos cargar la sesión" + "Your data is safe. Try again."/"Tus datos están a salvo.
Inténtalo de nuevo." + "Retry"/"Reintentar").

### States
Single error state (calm). Retry text defaults to "Retry"; the exit action is omitted unless the host
supplies both `exitText`/`exitTitle` and `onExit`.

### Interactions
Retry re-attempts; optional exit leaves safely. No red, no shame framing.

### Accessibility
Title/message wrap (`fixedSize`/`textAlign center`); Retry is the primary `AtlanButton`; the exit text
action is a ≥44/48 target.

### iOS Notes
`AtlanErrorView` in `Design/AtlanComponents.swift`. ### Android Notes `AtlanErrorScreen` in
`design/AtlanComponents.kt`.

### QA Checklist
No red / no blame ✓ · data-safe reassurance ✓ · Retry + optional safe exit ✓ · bilingual via caller
✓ · wired (Session Detail) though unreachable with fakes ✓.

---

## 14. Branded Launch (splash)

### Purpose
Open on the calm Foam brand surface with the Atlan wordmark, rather than a blank system screen, then
auto-advance into Language Selection. Native on each platform; no artificial blocking of input.

### Entry Points
Cold start, before the first route renders.

### Exit Points
Auto-advances after ~0.8s: Android swaps `AtlanSplash` for `AtlanNavGraph`; iOS fades the overlay out
(`splashDone`). No user action required or possible.

### Layout
FoamWarm full-bleed; centered `atlan` wordmark (44pt/sp, Abyss, SemiBold) over a short Coral rule —
the same wordmark language as Language Selection. Wordmark fades in (~0.45s).

### Components
`AtlanSplash` (Android composable) / `AtlanSplashView` (iOS, defined in-file so no `.xcodeproj` entry
is needed).

### States
Single transient state (fading in, then gone). Language-agnostic — no copy, so nothing to localize.

### Interactions
None; it dismisses itself.

### Accessibility
Exposes a single "Atlan Performance" label and ignores child elements (decorative wordmark). Brief and
non-blocking, so it never traps focus.

### iOS Notes
Overlay inside the root `ZStack` in `AtlanPerformanceApp.swift`; `.task` sleeps ~0.8s then animates
`splashDone`. The window background should also be Foam for the pre-render frame (TODO: set the
generated launch screen's background color — currently the system default shows for the brief
pre-SwiftUI moment).

### Android Notes
`AtlanSplash` shown by `AtlanAndroidApp` until `splashDone`. The Activity `windowBackground`
(themes.xml) is already FoamWarm, so the pre-Compose cold-start frame matches — no white/black flash.
TODO(optional): adopt the Android 12 `SplashScreen` API for a system-driven icon splash.

### QA Checklist
Foam surface (no white/black flash) ✓ · wordmark + Coral rule on-brand ✓ · auto-advances to Language
Selection ✓ · no input trap ✓ · Android verified on emulator; **iOS pending device/sim verification**.

---

## 15. Workout Plan List

### Purpose
Show the current week's sessions so the plan reads as more than a single isolated timer. Completed and
upcoming sessions are calm, equal states — never a streak, score, or "missed" row.

### Entry Points
"View this week's plan"/"Ver el plan de la semana" affordance on the dashboard (below the weekly arc).
Routes: `AtlanRoute.WORKOUT_PLAN` / `AppRoute.workoutPlan`.

### Exit Points
Back → `dashboard`. Tapping **today's** session → `sessionDetail`. Completed/upcoming rows are
display-only (not actionable in this milestone).

### Layout
FoamWarm scroll: back control → "Your plan"/"Tu plan" display title → week subtitle ("Week 18 of 24 ·
On track") → a Paper row per session (title, distance · duration, optional "Offline · Ready" hint, and
a status pill: Completed / Today / Upcoming).

### Components
`AtlanBackButton` (Android), Paper rows (inline), `AtlanPill` (status), `ProgressView` while loading.

### Content / State
`TrainingWeek` from the new `GetTrainingPlanUseCase` (`getCurrentWeek()`); today's id from
`getTodaySession()` marks the actionable row. Seed expanded with a completed **Recovery** session and
an upcoming **Endurance** session alongside today's Threshold session.

### States
- Loading — spinner until the week resolves.
- Loaded — session rows; today's is tappable.
- **Empty** — calm card ("Nothing scheduled this week" / "Nada programado esta semana", "…enjoy the
  rest"), never alarming. This doubles as the inventory's no-content/offline-empty state.

### Interactions
Open today → Session Detail. Other rows are informational. No red, no "missed", no streak.

### Accessibility
Today's row is a ≥44/48 `Role.Button`; others are plain text. Status pills read as text. Offline hint
is informational.

### iOS Notes
`WorkoutPlanListView` (`SharedContainer.trainingWeek()`). ### Android Notes `WorkoutPlanListScreen`
(`shared.getTrainingPlan()`).

### QA Checklist
Completed/upcoming framed as calm equals ✓ · only today actionable ✓ · calm empty state ✓ · no
streak/missed/red ✓ · bilingual ✓ · built green on both platforms ✓.

---

## 16. How It Works

### Purpose
A calm primer on set-based threshold training — education on demand, never motivational pressure. The
inventory's separate **Pace Explanation** is folded in here as its own section.

### Entry Points
A tappable "How it works"/"Cómo funciona" row in Settings. Routes: `HOW_IT_WORKS` / `.howItWorks`.

### Exit Points
Back → `settings`.

### Layout
FoamWarm scroll: back control → display title → a stack of Paper sections (Set-based training · The
4-set structure · **Target pace** · Rest between sets · Works offline), each an uppercase Tide label +
body.

### Components
`AtlanBackButton` (Android), Paper section cards (inline).

### Content
Inline bilingual EN/ES (not yet keyed — flagged, consistent with other screen-local copy). Tone is
explanatory and non-judgemental; explicitly states that going over target pace is never a failure.

### States
Static content; no async, no error path.

### Interactions
Read + scroll; back. No actions in the body.

### Accessibility
Sections wrap (`fixedSize`/no shrink); Dynamic-Type/scalable. Back is a labeled 44/48 target.

### iOS Notes
`HowItWorksView`. ### Android Notes `HowItWorksScreen`.

### QA Checklist
Sourced/explanatory, no motivation pressure ✓ · pace explained (no "test"/failure framing) ✓ ·
bilingual ✓ · built green on both platforms ✓.

---

## 17. Resume / Session Recovery (dashboard banner)

### Purpose
Let a returning user pick up a session that was interrupted (app closed or killed mid-workout) —
resume or discard, both calm equals. Backed by **durable local persistence**, so it survives process
death (the "Session Recovery" need) without a launch interstitial.

### Entry Points
A banner at the top of the Today Dashboard, shown only when a saved in-progress snapshot exists
(`LoadSessionProgress` returns non-null on dashboard load).

### Exit Points
**Resume** → Wet Mode in resume mode (rebuilds the timer at the interrupted set). **Discard** → clears
the snapshot (`ClearSessionProgress`) and hides the banner.

### Layout
TidePale card: "Resume session · Set X of N" / "Reanudar sesión · Serie X de N" → Coral **Resume** +
text **Discard**.

### Content / State
`SessionProgress` (`sessionId`, `setIndex`, `setCount`, `completedCount`, `restSeconds`) from the
SQLDelight `sessionProgress` table. Wet Mode **saves** the snapshot at set granularity (on each set
complete / pause / rest transition) and **clears** it when the session reaches `COMPLETED_SESSION`
(full or ended-early). So a snapshot lingers only when the app was left/killed mid-session.

### Behaviour notes
- Resuming rebuilds via `ResumeWorkoutTimerUseCase`: a READY timer at `setIndex` with `completedCount`
  preserved; the interrupted set **starts fresh** (no time accrues while the app was closed — correct
  for interval training). The rest preference is restored from the snapshot.
- This is intentionally a dashboard banner, not a launch interstitial — a promote-to-launch screen is
  a small follow-up and avoids entangling onboarding routing.

### States
- None pending → no banner.
- Pending → banner with Resume/Discard.
- No async error path (local DB).

### Accessibility
Resume is the Coral primary; Discard is a ≥44/48 `Role.Button`. Copy is neutral — never implies failure.

### iOS Notes
`TodayDashboardView.resumeBanner` (`container.loadSessionProgress()` / `clearSessionProgress`);
`coordinator.wetResume` gates the Wet Mode `.task` rebuild.
### Android Notes
`TodayDashboardScreen` resume banner; `wetResume` nav flag selects resume vs fresh in `WetModeScreen`.

### QA Checklist
Survives process death (snapshot in SQLite) ✓ · resume rebuilds at the right set with prior sets still
counted ✓ · discard clears ✓ · finishing a session clears ✓ · calm, no failure framing ✓ · proven by
`SessionProgressPersistenceTest` + built green on both platforms ✓.

---

## 18. Workout History

### Purpose
A calm, durable record of finished sessions — set counts, elapsed, optional perceived effort. Builds
trust and lets the user look back. No streaks, no totals-as-pressure; partial (ended-early) sessions
are valid records, not failures.

### Entry Points
"Workout history" / "Historial de entrenos" row in Settings. Routes: `HISTORY` / `.history`.

### Exit Points
Back → `settings`.

### Layout
FoamWarm scroll: back → "History"/"Historial" → a Paper row per session (title + date, "X of N sets ·
elapsed", and pills: "Partial" when ended early, an effort tag when present). Empty → calm
"No sessions yet" card.

### Content / State
`List<CompletedSession>` from `GetWorkoutHistoryUseCase` (newest first, top 50) over the SQLDelight
`completedSession` table. Records are written by `RecordCompletedSessionUseCase` when the user taps
**Done** on the Wet Mode summary — capturing title, completed/total sets, total elapsed, the optional
effort reflection, finished-at timestamp, and a full/partial flag. The platform supplies the timestamp
(ISO-8601; the row shows its date prefix).

### States
- Loading — spinner until the query resolves.
- Loaded — rows, newest first.
- **Empty** — reassuring "No sessions yet" card (first run, or after clearing).
- No error path (local DB).

### Behaviour notes
- Recording happens **on completion** (when the session reaches `COMPLETED_SESSION`), so a session
  killed *on the summary* before Done is still logged. Tapping **Done** with a perceived-effort chip
  selected **upserts** the same record (same id via a stable completion timestamp) to attach the effort.
- Effort is stored as a canonical key (`easy`/`moderate`/`hard`) and localised at display.

### Accessibility
Rows read top-to-bottom; pills are text. Back is a labeled 44/48 target.

### iOS Notes
`HistoryView` (`container.workoutHistory()`). ### Android Notes `HistoryScreen` (`shared.getWorkoutHistory()`).

### QA Checklist
Persists across relaunch (SQLite) ✓ · newest-first ✓ · partials recorded + flagged calmly ✓ · effort
round-trips ✓ · empty state reassuring ✓ · proven by `WorkoutHistoryPersistenceTest` + built green on
both platforms ✓.

---

## 19. Progress Overview

### Purpose
A calm, qualitative summary of training over recent sessions — built from the persisted Workout
History. Reinforces the product stance: progress is a *pattern*, not a number to defend.

### Entry Points
"Progress" / "Progreso" row in Settings. Routes: `PROGRESS` / `.progress`.

### Exit Points
Back → `settings`.

### Layout
FoamWarm scroll: back → "Progress"/"Progreso" → a **Consistency** Paper card ("N sessions · M sets",
"X complete · Y adjusted") → an optional **Perceived effort** card (Easy/Moderate/Hard counts, shown
only when effort was logged) → a quiet closing line ("Progress is a pattern, not a number to defend").
Empty → reassuring "No progress yet" card.

### Content / State
`ProgressOverview` from `GetProgressOverviewUseCase`, aggregating `WorkoutHistoryRepository.recent()`:
session count, sets completed, full vs **adjusted** (ended-early, framed as valid), and the effort
distribution. (Aggregates over the recent window; full-range is a later refinement.)

### Principle guardrails (enforced)
**No streak, no consecutive-day counter, no goal deficit, no "behind", no rank, no red.** `ProgressOverview`
has no field for any of those. Ended-early sessions are "**adjusted**", never "missed"/failed. Counts
are calm tallies, not pressure. (Asserted structurally + in `ProgressOverviewTest`.)

### States
- Loading — spinner.
- Has data — consistency (+ optional effort) cards.
- **Empty** — reassuring card (first run).
- No error path (local DB).

### Accessibility
Cards read top-to-bottom; Dynamic-Type/scalable. Back is a labeled 44/48 target.

### iOS Notes
`ProgressView_Atlan` (named to avoid clashing with SwiftUI's `ProgressView`; `container.progressOverview()`).
### Android Notes
`ProgressScreen` (`shared.getProgressOverview()`).

### QA Checklist
Calm tallies only — no streak/deficit/red ✓ · partials shown as "adjusted" ✓ · effort distribution
when logged ✓ · reassuring empty state ✓ · proven by `ProgressOverviewTest` + built green on both
platforms ✓.

---

## 20. Profile Setup + bootstrap routing

### Purpose
A light, **optional** onboarding step that captures a name and training level to tune coaching tone —
never a score or gate. Plus the **bootstrap** behaviour: returning users skip onboarding and open on
the Dashboard.

### Entry Points
In the onboarding flow, between Calibration and Tuned Summary: `Language → Welcome → Calibration →
profileSetup → tunedSummary → dashboard`. Routes: `PROFILE_SETUP` / `.profileSetup`.

### Exit Points
"Continue" saves the (optional) name + level and advances to Tuned Summary. Back → Calibration.
Tuned Summary's CTA now **marks onboarding complete** before going to the Dashboard.

### Bootstrap (App start)
On launch, if onboarding was completed, the app starts on the **Dashboard**, skipping
Language→…→Tuned Summary. Android: `AtlanNavGraph` picks the initial route from the
`onboardingComplete` pref. iOS: `AppCoordinator.init` seeds `path = [.dashboard]`. Language and the
profile persist, so returning users keep their setup. (A returning user with a saved in-progress
session lands on the Dashboard, where the §17 Resume banner offers to continue — so no separate launch
interstitial is needed.)

### Layout
FoamWarm: eyebrow ("About you"/"Sobre ti") → display title → an "optional, never a grade" line → a
**Name** text field → **Training level** rows (Beginner/Intermediate/Advanced · color-not-alone ✓) →
Continue.

### Content / State
Name + level are stored locally via the platform-prefs adapter (`SharedPreferences` / `UserDefaults`),
the same store as language/haptics/rest — no account, no backend. Inline bilingual (not yet keyed).
Tuned Summary greets by name when given ("Tuned for you, {name}").

### States
Default (empty), name typed, level selected/deselected (toggle). No async; no error path.

### Accessibility
Level rows are ≥48dp `Role.Button` with `selected` semantics + a ✓ (never color-only). The name field
is optional and labeled.

### iOS Notes
`ProfileSetupView`; persists via `AppCoordinator.saveProfile`. ### Android Notes `ProfileSetupScreen`;
persists via `AtlanNavGraph.saveProfile`.

### QA Checklist
Optional, no gate/score ✓ · name + level persist ✓ · returning users skip to Dashboard ✓ · language +
profile survive relaunch ✓ · Tuned Summary greets by name ✓ · bilingual ✓ · built green on both
platforms ✓.

---

## 21. Permission Rationale (Notifications / Health)

### Purpose
Explain the **value** of an optional capability *before* any system permission prompt — and never
fire one cold. Two variants (Reminders, Health) from one reusable screen.

### Entry Points
"Reminders" / "Salud" rows in Settings. Routes: `NOTIFICATIONS_RATIONALE` / `HEALTH_RATIONALE`
(Android) and `.permissionRationale(.notifications | .health)` (iOS, associated value).

### Exit Points
Primary CTA ("Turn on reminders" / "Connect") records intent and shows a calm acknowledgement; "Done"
or "Not now" → back to Settings.

### Layout
FoamWarm: "Optional" eyebrow → title → body → a Paper card of calm value bullets → CTA + "Not now".

### Behaviour
- **Notifications — now functional (client-side, no backend).** "Turn on reminders" requests the OS
  permission (Android `POST_NOTIFICATIONS` via an activity-result launcher on API 33+; iOS
  `UNUserNotificationCenter.requestAuthorization`) and, on grant, schedules a calm **daily session
  reminder** (Android WorkManager `PeriodicWorkRequest` → `ReminderWorker` posting on the
  `atlan_reminders` channel ~7am; iOS `UNCalendarNotificationTrigger` at hour 7, repeating). The
  acknowledgement is result-aware ("Reminders on…" vs "No problem — you can turn them on anytime in
  settings"). Reminder copy is bilingual. **Local notifications only — no server push.**
- **Health — still rationale-only.** No HealthKit / Health Connect wiring yet (entitlements +
  capabilities); the CTA records intent + a calm acknowledgement. TODO: real sync.
- Both are always skippable and **never trigger a prompt cold** — the rationale is shown first (per the
  inventory's explicit rule).

### Content
Inline bilingual EN/ES. Calm, non-pressuring — reminders are "gentle, never nagging"; Health is
"optional, you stay in control, disconnect anytime."

### States
Rationale (default) → acknowledged (after CTA). No async; no error path.

### Accessibility
CTA + "Not now"/"Done" are ≥44/48 targets; bullets read in order.

### iOS Notes
`PermissionRationaleView(kind:)`; `PermissionKind` enum drives content.
### Android Notes
`PermissionRationaleScreen(kind)`; `PermissionKind` enum.

### QA Checklist
Explains value before any prompt ✓ · never fires a system prompt cold ✓ · calm, optional, skippable ✓
· bilingual ✓ · both variants from one screen ✓ · **notifications: real OS permission + daily local
reminder scheduled on grant** ✓ · Health: rationale-only (TODO real sync) ✓ · built green on both
platforms ✓.

---

## Brand assets (logo)

The Atlan logo (`assets/atlan_logo_bk.svg`, rendered to a 1024 PNG) is wired as:
- **App icon** — iOS `Assets.xcassets/AppIcon` (single 1024; `actool` derives the sizes); Android
  launcher via `android:icon`/`roundIcon` → `@drawable/atlan_logo`.
- **In-app brand** — the **Branded Launch** (§14) and **Language Selection** (§1) now show the logo
  badge in place of the earlier text wordmark, on both platforms (`Image("AtlanLogo")` / `painterResource`).

The iOS app icon is a **flattened opaque** square (Abyss ground, no alpha channel — store-ready),
generated from an icon-specific SVG (`assets/atlan_icon_opaque.svg`, an opaque-background variant).
The in-app logo keeps its transparent circular badge (it sits on the Foam surface).

---

## Coverage matrix

| Screen | Loading | Empty | Error | Disabled | Success | Notes |
|---|---|---|---|---|---|---|
| Branded Launch (§14) | — | — | — | — | auto-advance | Foam + wordmark; no copy |
| Language Selection | — | — | — | — | nav | no async |
| Welcome | — | — | — | — | nav | — |
| Calibration | — | — | — | — | nav | selection optional |
| Tuned Summary | — | — | — | — | nav | — |
| Today Dashboard | ✓ | — | — | — | content | no streak fields; "View plan" affordance |
| Workout Plan List (§15) | ✓ | ✓ (calm) | — | — | week sessions | only today actionable; no missed/streak |
| Session Detail | ✓ | — | ✓ (calm, via §13) | — | content | offline pill |
| Workout Prep | ✓ | — | — | — | content | offline-ready pill; bilingual |
| Why Modal | ✓ | ✓ (calm) | — | — | content | crash fixed |
| Wet Mode | ✓ | — | — | — | local write + summary + sync block | phases: active/paused/overtime/rest/complete; sync: pending/syncing/saved-locally |
| Gesture Tutorial | — | — | — | — | one-time overlay | once per install; bilingual (both platforms) |
| Session Swapper | ✓ | — | — | ✓ (accepting) | dismiss | crash fixed |
| Settings | — | — | — | — | live controls + display | language/haptics/keep-awake/rest persist; How-It-Works entry |
| How It Works (§16) | — | — | — | — | content | primer + pace explanation; bilingual |
| Resume / Recovery (§17) | — | (no banner) | — | — | resume / discard | dashboard banner; SQLite-backed, survives process death |
| Workout History (§18) | ✓ | ✓ (calm) | — | — | list | SQLite-backed; newest-first; partials flagged, not failed |
| Progress Overview (§19) | ✓ | ✓ (calm) | — | — | summary | calm tallies from history; no streak/deficit/red |
| Profile Setup (§20) | — | — | — | — | nav | optional name+level; persisted; bootstrap skips onboarding |
| Permission Rationale (§21) | — | — | — | — | rationale + ack | Notifications/Health; never prompts cold; UI-only |
| Generic Error (§13) | — | — | ✓ (reusable) | — | retry / safe exit | wired to Session Detail; unreachable with fakes |

"Error" states remain intentionally absent across onboarding/dashboard: offline-missing content is a
calm empty state, not an error, and disruption is never failure. The only error surface is the
reusable §13 view, reserved for genuine unrecoverable load failures and styled calm (no red).
