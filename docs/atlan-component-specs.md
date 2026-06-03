# Atlan Mobile — Component Specifications

Per-component specs for the custom Atlan component library, following the §16 template in
`atlan-mobile-design-patterns.md`. Grounded in `Design/AtlanComponents.swift` (iOS) and
`design/AtlanComponents.kt` (Android), both verified on device.

**Shared rules.** No component introduces streak / rank / flame / trophy / badge pressure. Coral is
reserved for productive action; Tide marks Why/science/selected. Every interactive control: a pressed
state (§9.2), a ≥44pt (iOS) / ≥48dp (Android) touch target (§11.2), and no color-only state (§11.5).
Tokens come from the shared `AtlanColors/Spacing/Radii` constants.

---

## AtlanButton

### Purpose
Primary call-to-action.

### Anatomy
Pill container (Capsule / `RoundedCornerShape(pill)`), centered label, optional spinner overlay.

### Variants
- **Primary** — Abyss fill (default).
- **Productive** — CoralBright fill (`coral = true`); reserved for Start / Complete / Accept.

### States
- Default
- Pressed — opacity ~0.82 + slight scale (iOS `AtlanPressStyle`; Android `interactionSource` + scale/alpha)
- Disabled — TideSoft fill, non-interactive (`isEnabled`/`enabled = false`)
- Loading — spinner replaces the label, **width preserved**, taps blocked (no double-submit)

### Behavior
`action`/`onClick` fires only when enabled and not loading. Min height pins the 44/48 target.

### Accessibility
Label = title; loading announced via accessibility value (iOS) / role + state; `Role.Button`
(Android). Never relies on color alone — disabled is also non-interactive and announced.

### Tokens
Fill: Abyss / CoralBright / TideSoft(disabled). Text: Foam. Vertical padding `md`; radius `pill`;
min height 44/48.

### iOS Implementation
`AtlanButton(title:coral:isEnabled:isLoading:action:)`; `ZStack` keeps the hidden label sizing the
button during loading.

### Android Implementation
`AtlanButton(text,onClick,modifier,coral,enabled,loading)`; `CircularProgressIndicator` while loading;
`indication = null` so the Atlan press treatment replaces Material ripple.

### Do
Use exactly one productive (Coral) button per screen, on the genuinely productive action.

### Don't
Use Coral for cancel/secondary/error. Stack multiple Coral buttons. Change width on loading.

---

## AtlanPressStyle (iOS)

### Purpose
Shared calm pressed-state treatment for non-`AtlanButton` controls.

### Anatomy / Behavior
A `ButtonStyle` applying opacity ~0.82 + scale 0.98 on press, eased ~0.12s. No layout movement.

### Usage
Applied to `AtlanInfoButton`, `AtlanSelectableRow`, the Why close button, gear, and text actions so
every tappable control has consistent feedback (§9.2). Android achieves the equivalent inline via
`collectIsPressedAsState`.

---

## AtlanPill

### Purpose
Small, calm status/label (e.g. "2 of 4", "Offline · Cached", "Session Adjusted").

### Anatomy
TidePale capsule, TideDeep caption text.

### States
Static (no interaction).

### Accessibility
Read as text; informational, never an alert.

### Tokens
Bg TidePale; text TideDeep; padding `md`/`xs`; radius `pill`.

### iOS / Android
`AtlanPill(text)` in both. Do: short status. Don't: use for errors or as a button.

---

## AtlanInfoButton

### Purpose
The "Why" affordance — opens sourced science. Tide, never Coral.

### Anatomy
24pt Tide-outlined circle with an italic `i`, centered inside a 44/48 hit target.

### States
Default; Pressed (`AtlanPressStyle` / inline). 

### Behavior
Tap opens the Why Modal for the associated concept key.

### Accessibility
Labeled "Why … — open explanation" (context-specific where a metric title is available); `isButton`/
`Role.Button`. The visible ring stays small for calm density, but the **tap target is expanded** to
the platform minimum.

### Tokens
Stroke/text Tide; ring 24; target 44/48; radius `pill`.

### iOS Implementation
`AtlanInfoButton(accessibilityLabel:action:)` — double `frame` (24 visual → 44 target) + `contentShape`.

### Android Implementation
`AtlanInfoButton(onClick,modifier,accessibleLabel)` — 48dp `Box` wrapping the 24dp ring.

### Do
Place beside the term it explains. Don't: color it Coral or shrink the target to the ring.

---

## AtlanSelectableRow

### Purpose
Single-select option (Calibration; language-style choices).

### Anatomy
Full-width row: label (weighted) + trailing indicator (checkmark when selected, empty circle when not).

### States
- Default — Paper fill, TideSoft thin border, empty circle.
- Selected — TidePale fill, Tide 1.5 border, TideDeep label, **Tide checkmark**.
- Pressed — press treatment.

### Behavior
Whole row is the target; tapping selects. Single-select is managed by the parent.

### Accessibility
Carries the selected trait/semantics (`.isSelected` / `selected` + `Role.RadioButton`) so VoiceOver/
TalkBack announce state. **Never color-only** — checkmark + border reinforce. ≥44/48 height.

### Tokens
Fill Paper/TidePale; border TideSoft/Tide; text Abyss/TideDeep; radius `lg`.

### iOS / Android
`AtlanSelectableRow(title:isSelected:…)` in both.

### Do
Use for "no right answer" choices. Don't: signal selection with color alone; don't gate navigation on
a selection unless the screen truly requires it.

---

## AtlanMetricChip

### Purpose
Dashboard metric card with an inline Why affordance.

### Anatomy
Paper card: uppercase Tide label + optional `AtlanInfoButton`, large numeric value, supporting detail.

### States
Static display (the embedded `i` is interactive).

### Behavior
`i` opens the Why Modal for the chip's concept key.

### Accessibility
`i` label is metric-specific. Numeric uses the tabular `numeric` type role.

### Tokens
Bg Paper; label TideDeep; value/detail Abyss; radius `lg`; padding `lg`.

### iOS / Android
`AtlanMetricChip(chip,onWhy)`. Do: keep to calm, factual metrics. Don't: introduce streak/rank/“behind”
metrics or red values.

---

## AtlanBackButton (Android)

### Purpose
Back affordance for pushed screens (Welcome, Calibration, Session Detail, Settings). iOS uses the
native `NavigationStack` chevron + swipe-back instead, so this is Android-only — a platform-appropriate
difference, not divergence.

### Anatomy
A "‹" glyph inside a 48dp target.

### States
Default; Pressed (Compose interaction). 

### Accessibility
Localized `contentDescription` ("Atrás"/"Back") + `Role.Button`; 48dp target.

### Tokens
Glyph Abyss; target 48; radius `pill`.

### Android Implementation
`AtlanBackButton(onClick,modifier,contentDescription)`. Replaces the previous bare `Text("←")` (which
was sub-minimum and unlabeled).

### Do
Pass a localized description. Don't: reintroduce a tiny unlabeled glyph; don't suppress system back.

---

## AtlanSessionCard (Android)

### Purpose
Abyss card wrapper for session content (e.g. the dashboard hero).

### Anatomy
Full-width Abyss column, `phone` radius, `xl` padding, `sm` item spacing; `ColumnScope` content slot.

### States
Static container.

### Tokens
Bg Abyss; radius `phone`. iOS composes the equivalent inline in `TodayDashboardView`.

---

## AtlanBottomSheet (Android)

### Purpose
Native bottom-sheet wrapper for the Why Modal and Session Swapper.

### Anatomy
Material3 `ModalBottomSheet` (skip-partially-expanded), Paper container, top `sheet`-radius corners,
`xl` content padding.

### Behavior
Dismiss via drag-down / scrim → `onDismiss`. Receives screen content as a slot; the hosting screen is
passed `shared` directly (so it never depends on environment inheritance).

### Accessibility
Standard sheet semantics + drag handle.

### iOS equivalent
`.sheet` with `.presentationDetents([.medium,.large])` + drag indicator in `AtlanPerformanceApp.swift`.
**Note:** iOS sheets must re-inject `@EnvironmentObject`s on their content (fixed) — they do not inherit
them across the presentation boundary.

---

## WetModeActionZone

### Purpose
One half of the Wet Mode bottom action grid — a large, imprecise, wet-hands target.

### Anatomy
Full-height column: large title + small hint, full-bleed colored background.

### Variants
- **Productive** (Complete) — CoralBright.
- **Neutral** (Pause/Resume) — Abyss.

### States
Default; Pressed.

### Behavior
Tap activates; pairs with swipe gestures but is independently operable (swipe is never the only path).

### Accessibility
Explicit accessible label per zone; large non-precision target; complements the screen-level custom
actions.

### Tokens
Bg CoralBright/Abyss; text Foam; very large title (28pt).

### iOS / Android
`WetModeActionZone(title,hint,accessibleLabel,productive,action)`.

### Do
Keep zones huge and reachable. Don't: require precise aim; don't use Coral on the neutral zone.

---

## AtlanErrorView / AtlanErrorScreen

### Purpose
A reusable, calm fallback for unrecoverable load failures (§13 screen spec). No blame, no red, no
shame framing — explain the issue, reassure that data is safe, offer Retry (and an optional safe exit).

### Anatomy
Centered column: Tide retry glyph (`↻` / `arrow.clockwise.circle`, ~44pt) → display title → message →
`AtlanButton` (Retry) → optional secondary text exit action.

### Variants
- **Retry-only** (default) — title + message + Retry.
- **Retry + safe exit** — supply both `exitText`/`exitTitle` and `onExit` to add a calm text exit.

### States
Single calm error state. Retry uses the default (Abyss) `AtlanButton` — **not** Coral (an error is not
a productive action). No red anywhere.

### Behavior
`onRetry` re-runs the host screen's load (e.g. Session Detail bumps a reload key). The exit action,
when present, routes to a host-defined safe destination.

### Accessibility
Title and message wrap (`fixedSize` / `textAlign center`); Retry is the primary button; the optional
exit text action is a ≥44/48 target.

### Tokens
Glyph TideDeep; title Abyss (Display); message TideDeep; Retry = Abyss `AtlanButton`. No Coral, no red.

### Content / Localization
Copy is **caller-provided** — the component is language-agnostic and the host passes already-localized
strings (Session Detail passes EN/ES title, message, and "Retry"/"Reintentar"). `retryText`/
`retryTitle` defaults to "Retry" if the caller omits it.

### iOS Implementation
`AtlanErrorView(title:message:retryTitle:onRetry:exitTitle:onExit:)` in `Design/AtlanComponents.swift`.

### Android Implementation
`AtlanErrorScreen(title,message,onRetry,modifier,retryText,exitText,onExit)` in
`design/AtlanComponents.kt`.

### Do
Pass localized strings; reserve it for genuine unrecoverable failures. Don't: color Retry Coral; use
red; introduce blame ("you", "failed to", "missed"); show it for normal offline-missing content (that
is a calm empty state, not an error).

---

## Token reference (quick)

| Token | Value |
|---|---|
| Touch target min | 44pt (iOS) / 48dp (Android) |
| Radii | sm 4 · md 8 · lg 16 · sheet 24 · phone 32 · pill 999 |
| Spacing | xs 4 · sm 8 · md 12 · lg 16 · xl 24 · 2xl 32 · 3xl 48 · 4xl 64 |
| Productive fill | CoralBright (`#FF7E50`) — rare |
| Primary fill | Abyss (`#0B2A3C`) |
| Science / selected | Tide (`#0E8A9A`) / TidePale (`#DDEEF1`) |
| Light surfaces | Foam / FoamWarm / Paper |
