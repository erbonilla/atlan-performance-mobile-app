# Atlan Mobile Design Patterns Documentation

**Document type:** Mobile design system implementation guide  
**Product area:** Atlan mobile app  
**Platforms:** iOS and Android  
**Architecture context:** Kotlin Multiplatform shared core with native Swift and Android UI layers  
**Current validated baseline:** The iOS app launches on the iPhone 17 simulator and renders the Language Selection screen correctly. Copy is provided from the KMP shared core, confirming the Swift ↔ Kotlin bridge works end to end.

---

## 1. Purpose

This document defines the mobile design patterns required to implement the Atlan mobile experience consistently across iOS and Android.

It covers:

- Screen structure
- Navigation behavior
- Layout rules
- Component usage
- Interaction patterns
- Loading, empty, error, disabled, and success states
- Accessibility expectations
- iOS-specific implementation notes
- Android-specific implementation notes
- Kotlin Multiplatform shared-core considerations

The goal is to ensure that every mobile screen is consistent with the Atlan product principles: calm, bilingual, accessible, trustworthy, and visually aligned with the brand system.

---

## 2. Current Known-Good Baseline

The current implementation milestone confirms the following:

- The iOS application launches successfully in the iPhone 17 simulator.
- The Language Selection screen renders correctly.
- The Atlan wordmark appears correctly.
- The rare Coral accent is applied as intended.
- English and Spanish are presented as co-equal languages.
- Abyss buttons render correctly on a Foam background.
- Screen text comes from the Kotlin Multiplatform shared core.
- The Swift ↔ Kotlin bridge works end to end.
- No signature fixes are currently required.

This state should be treated as a stable baseline before expanding navigation, persistence, onboarding, or additional localized screens.

---

## 3. Brand and Visual Principles

### 3.1 Core Visual Direction

The mobile interface should feel:

- Calm
- Trustworthy
- Clear
- Human
- Minimal but not empty
- Bilingual by design
- Accessible by default

### 3.2 Brand Color Roles

Use brand colors by semantic purpose, not decoration.

| Role | Usage |
|---|---|
| Foam | Primary app background |
| Abyss | Primary button fill, strong text, high-emphasis UI |
| Coral | Rare accent, emphasis, small moments of delight |
| Neutral tones | Supporting surfaces, borders, dividers, disabled states |

### 3.3 Coral Usage Rule

Coral is a rare accent. It should not dominate screens.

Use Coral for:

- Small brand emphasis
- Selected accents
- Focus moments
- Illustrative highlights
- Key confirmation details

Avoid Coral for:

- Large backgrounds
- Primary action fills
- Dense repeated UI controls
- Error states unless explicitly defined

---

## 4. Language and Content Principles

### 4.1 English and Spanish as Co-Equal Languages

English and Spanish must be presented as equal options. Avoid layouts that visually imply English is primary and Spanish is secondary unless the user has already selected a language preference.

Correct pattern:

```text
Choose your language
Elige tu idioma
```

Incorrect pattern:

```text
Choose your language
Spanish translation below in smaller text
```

### 4.2 Copy Source of Truth

User-facing copy should come from the Kotlin Multiplatform shared core where practical.

Native layers should avoid hardcoded product copy except for:

- Temporary debug labels
- Native platform-only permission copy
- System API labels that cannot be shared
- Emergency fallback strings

### 4.3 Tone

Use copy that is:

- Clear
- Direct
- Warm but restrained
- Easy to localize
- Free of idioms that do not translate well

---

## 5. Screen Architecture

Every mobile screen should follow a predictable structure.

### 5.1 Standard Screen Regions

Recommended structure:

1. System safe area
2. Optional top app bar or brand header
3. Primary content area
4. Optional supporting content
5. Primary action area
6. Optional secondary action area
7. Bottom safe area

### 5.2 Screen Types

| Screen type | Purpose | Examples |
|---|---|---|
| Entry screen | First meaningful product screen | Language Selection |
| Onboarding screen | Introduces setup steps | Welcome, preferences |
| Authentication screen | Handles sign-in or account access | Sign in, verification |
| Permission screen | Requests platform permission | Notifications, location |
| Main screen | Core product destination | Home, dashboard |
| Detail screen | Focused object or record view | Profile, item detail |
| Settings screen | Preferences and configuration | Language, notifications |
| Feedback screen | Communicates status | Error, empty, success |

### 5.3 Language Selection Screen Pattern

The current Language Selection screen establishes the first canonical pattern.

Required elements:

- Foam background
- Atlan wordmark
- Rare Coral accent
- Co-equal bilingual heading
- English language option
- Spanish language option
- Abyss primary controls
- Clear touch targets
- Shared KMP text source

Recommended hierarchy:

1. Brand mark
2. Bilingual heading
3. Short supporting explanation, if needed
4. Language choices
5. Optional legal or secondary information

---

## 6. Navigation Patterns

### 6.1 Navigation Model

Use a simple progressive navigation model for early mobile flows.

Recommended initial flow:

```text
Launch
→ Language Selection
→ Welcome / Onboarding
→ Authentication or Account Setup
→ Main App Shell
```

### 6.2 Navigation Principles

- Keep early flows linear.
- Avoid premature tab navigation before the user has completed required setup.
- Preserve back navigation where users may need to revise a choice.
- Avoid trapping users without an obvious exit or continuation path.
- Keep platform navigation behavior native.

### 6.3 Back Navigation

#### iOS

Use native navigation-stack behavior where applicable.

Expected behavior:

- Swipe back works when the screen is pushed onto a stack.
- Back affordance appears only when a previous screen exists.
- Avoid custom back gestures unless necessary.

#### Android

Use Android system back behavior consistently.

Expected behavior:

- System back returns to the previous screen.
- Predictive back should be supported where the Android version allows it.
- Back should not unexpectedly exit the app during setup unless the user is at the first screen.

### 6.4 Modal Navigation

Use modals for:

- Short blocking decisions
- Confirmation
- Permission explanations
- Temporary focused tasks

Avoid modals for:

- Long forms
- Multi-step onboarding
- Primary navigation destinations

---

## 7. Layout System

### 7.1 Safe Areas

All screens must respect platform safe areas.

#### iOS

Respect:

- Top notch / Dynamic Island area
- Bottom home indicator
- Keyboard safe area

#### Android

Respect:

- Status bar
- Navigation bar
- Gesture navigation area
- Display cutouts
- IME keyboard insets

### 7.2 Spacing

Use a consistent spacing scale. Recommended base: 4pt / 4dp increments.

| Token | Value | Usage |
|---|---:|---|
| space.1 | 4 | Tight internal spacing |
| space.2 | 8 | Compact gaps |
| space.3 | 12 | Small component gaps |
| space.4 | 16 | Standard padding |
| space.5 | 20 | Medium screen rhythm |
| space.6 | 24 | Section spacing |
| space.8 | 32 | Large section spacing |
| space.10 | 40 | Major vertical separation |

### 7.3 Screen Padding

Recommended horizontal padding:

| Device class | Padding |
|---|---:|
| Compact phones | 20 |
| Standard phones | 24 |
| Large phones | 24–32 |
| Tablets | 32–48 |

### 7.4 Vertical Rhythm

Use vertical spacing to establish hierarchy:

- Brand-to-heading: generous
- Heading-to-supporting-copy: moderate
- Copy-to-actions: generous
- Button-to-button: compact but clear
- Bottom content-to-safe-area: comfortable

### 7.5 Responsive Behavior

Mobile layouts should adapt by:

- Maintaining readable line lengths
- Keeping actions reachable
- Avoiding cramped bilingual text
- Allowing text expansion for Spanish strings
- Supporting Dynamic Type / font scaling
- Avoiding absolute positioning except for decorative accents

---

## 8. Component Patterns

### 8.1 Button

Buttons are the primary action mechanism.

#### Button roles

| Role | Visual treatment | Usage |
|---|---|---|
| Primary | Abyss fill, high contrast text | Main action |
| Secondary | Outlined or soft treatment | Alternative action |
| Tertiary | Text-only or low emphasis | Optional action |
| Destructive | Explicit danger treatment | Delete, remove, irreversible actions |

#### Button requirements

- Minimum touch target: 44pt on iOS, 48dp on Android.
- Clear pressed state.
- Clear disabled state.
- Loading state for async actions.
- Text must remain readable under font scaling.
- Do not rely on color alone.

### 8.2 Language Option Button

Used for language selection.

Required behavior:

- Entire row/card is tappable.
- Language name is readable in its own language.
- Selection state is visually obvious.
- English and Spanish options have equal visual weight.

Possible structure:

```text
[ English ]
[ Español ]
```

or

```text
English
Español
```

Use the same component structure for both languages.

### 8.3 Text

Text styles should be semantic.

Recommended roles:

| Role | Usage |
|---|---|
| Display | Brand or major onboarding statement |
| Title | Screen title |
| Body | Main explanatory copy |
| Label | Form labels and small controls |
| Caption | Supporting metadata or helper text |

### 8.4 Wordmark / Brand Mark

The Atlan wordmark should:

- Appear crisp at mobile sizes.
- Maintain clear spacing around it.
- Avoid being crowded by system bars.
- Not be recolored unless brand-approved.
- Be positioned consistently across entry/onboarding screens.

### 8.5 Cards and Surfaces

Use cards sparingly on early onboarding screens.

Cards are appropriate for:

- Grouped choices
- Settings groups
- Summaries
- Confirmation blocks

Avoid excessive card nesting.

### 8.6 Forms

Form fields should include:

- Label
- Input area
- Helper text when needed
- Error text when invalid
- Clear focus state
- Disabled state
- Keyboard type appropriate to content

### 8.7 Alerts

Use alerts for important system feedback.

Alert types:

- Informational
- Success
- Warning
- Error

Alerts should include:

- Clear title or message
- Optional supporting text
- Optional action
- Accessible announcement where appropriate

---

## 9. Interaction Patterns

### 9.1 Tap

Use tap for primary selection and actions.

Requirements:

- Tap targets must meet platform minimums.
- Interactive elements should provide visual feedback.
- Avoid tiny text-only targets.

### 9.2 Pressed State

Pressed states should feel immediate.

Recommended treatment:

- Slight background shift
- Slight opacity change
- No disruptive animation
- No layout movement that causes jitter

### 9.3 Focus State

Focus must be visible for accessibility and keyboard navigation.

Focus indicators should:

- Use a clear outline or ring
- Meet contrast expectations
- Not be hidden by clipping
- Work on both light and dark backgrounds

### 9.4 Loading Interaction

Use loading states when an action requires async work.

For buttons:

- Replace or supplement label with spinner.
- Prevent duplicate submissions.
- Preserve button width to avoid layout shift.
- Keep accessible label meaningful.

### 9.5 Language Selection Interaction

When a user selects a language:

1. Update selected state immediately.
2. Persist the language choice through shared state where applicable.
3. Continue to the next step only after state is valid.
4. Avoid changing the entire UI language before the selection is confirmed unless the product explicitly requires live preview.

---

## 10. State Patterns

### 10.1 Default State

Default state should communicate readiness without unnecessary emphasis.

### 10.2 Hover State

Hover is not primary on mobile, but may apply to:

- iPad pointer interactions
- Android ChromeOS
- External mouse or trackpad environments

Do not depend on hover to reveal critical actions.

### 10.3 Pressed State

Pressed state confirms touch feedback.

### 10.4 Focus State

Focus state supports:

- Keyboard users
- Switch Control
- External hardware input
- Accessibility navigation

### 10.5 Disabled State

Disabled controls should:

- Clearly appear unavailable
- Maintain readable text where possible
- Not be the only way to understand what is required
- Be paired with helper text when the reason is not obvious

### 10.6 Loading State

Loading states should communicate progress without blocking unnecessarily.

Use:

- Button-level loading for action submission
- Screen-level loading for initial content fetch
- Skeletons for structured content
- Spinners for short indeterminate operations

### 10.7 Empty State

Empty states should explain:

- What is missing
- Why it matters
- What the user can do next

### 10.8 Error State

Error states should:

- Explain the issue clearly
- Avoid blame
- Provide recovery action
- Preserve user input where relevant
- Use accessible announcements for critical errors

### 10.9 Success State

Success states should:

- Confirm completion
- Avoid excessive celebration
- Clarify the next step if needed

---

## 11. Accessibility Requirements

### 11.1 Contrast

All text and meaningful UI indicators must meet WCAG AA contrast expectations.

Minimum targets:

- Normal text: 4.5:1
- Large text: 3:1
- Non-text UI indicators: 3:1

### 11.2 Touch Targets

Minimum target sizes:

| Platform | Minimum |
|---|---:|
| iOS | 44pt |
| Android | 48dp |

### 11.3 Dynamic Type and Font Scaling

The app must support font scaling.

Requirements:

- Text should not clip.
- Buttons should grow vertically if needed.
- Spanish strings must be tested for expansion.
- Layouts should avoid fixed heights for text-heavy areas.

### 11.4 VoiceOver and TalkBack

Every interactive element needs a meaningful accessibility label.

Language selection example:

```text
English, language option
Español, opción de idioma
```

### 11.5 Color Not Alone

Do not use color as the only indicator of:

- Selected state
- Error state
- Success state
- Disabled state
- Required fields

Use labels, icons, borders, or text reinforcement.

---

## 12. iOS Implementation Notes

### 12.1 Recommended UI Layer

Use SwiftUI unless a specific UIKit feature is required.

### 12.2 Safe Area

Use SwiftUI safe-area-aware layout primitives.

Avoid placing primary actions under the home indicator.

### 12.3 Typography

Use Dynamic Type-compatible text styles where possible.

If using custom fonts:

- Verify font availability.
- Provide fallback behavior.
- Test large accessibility sizes.

### 12.4 Navigation

Use native `NavigationStack` for standard push navigation.

Use sheets only for temporary tasks or focused decisions.

### 12.5 State Bridge

Text and shared state coming from Kotlin Multiplatform should be adapted into Swift-friendly observable state.

Implementation expectations:

- Avoid duplicating shared copy in Swift.
- Keep bridge signatures stable.
- Isolate platform adapter logic from view layout.
- Keep Swift views declarative and thin.

### 12.6 Haptics

Use haptics sparingly.

Appropriate uses:

- Successful confirmation
- Important selection
- Error feedback when not disruptive

Avoid haptics for every tap.

---

## 13. Android Implementation Notes

### 13.1 Recommended UI Layer

Use Jetpack Compose for native Android UI.

### 13.2 Insets

Use Compose inset handling for:

- Status bar
- Navigation bar
- Gesture area
- IME keyboard

### 13.3 Typography

Use scalable text units and support user font scaling.

Avoid fixed-height containers that clip localized text.

### 13.4 Navigation

Use Navigation Compose or the selected app navigation abstraction.

Requirements:

- Preserve system back behavior.
- Support predictable route structure.
- Keep setup flow linear.

### 13.5 KMP Shared Core

Android should consume the same shared strings and state models used by iOS.

Implementation expectations:

- Avoid Android-only product copy unless required.
- Keep Compose screens bound to shared state where appropriate.
- Map shared events into platform navigation actions.

### 13.6 Material Defaults

Do not accidentally inherit Material visual identity if it conflicts with Atlan design tokens.

Use Material primitives only as implementation infrastructure when needed, while overriding:

- Color
- Shape
- Typography
- Button treatments
- Surface styles

---

## 14. Kotlin Multiplatform Shared-Core Notes

### 14.1 Shared Responsibilities

The shared core should own:

- Localized product copy
- Language option models
- User language preference state
- Business rules for setup progression
- Validation rules
- Platform-independent UI state

### 14.2 Native Responsibilities

Native iOS and Android layers should own:

- Rendering
- Native navigation execution
- Platform permissions
- Platform accessibility APIs
- Haptics
- System UI integration
- Keyboard and inset handling

### 14.3 Shared UI State Pattern

Recommended structure:

```text
Shared ViewModel / Presenter
→ UI State
→ Native Adapter
→ SwiftUI or Compose Screen
→ User Event
→ Shared Core
```

### 14.4 Event Pattern

Events should be explicit.

Examples:

```text
LanguageSelected(languageCode)
ContinueTapped
BackTapped
RetryTapped
```

Avoid platform-specific events in shared core unless abstracted.

---

## 15. Screen Documentation Template

Use this template for every new mobile screen.

```markdown
# Screen Name

## Purpose
Explain what this screen helps the user do.

## Entry Points
Where users come from.

## Exit Points
Where users can go next.

## Layout
Describe screen regions, spacing, safe-area behavior, and responsive behavior.

## Components
List all components used.

## Content
List copy source and localization requirements.

## States
- Default
- Loading
- Error
- Empty
- Disabled
- Success

## Interactions
Describe taps, gestures, navigation, validation, and async behavior.

## Accessibility
Labels, focus order, contrast, Dynamic Type/font scaling, VoiceOver/TalkBack notes.

## iOS Notes
SwiftUI, NavigationStack, safe area, haptics, bridge notes.

## Android Notes
Compose, navigation, insets, back behavior, TalkBack notes.

## QA Checklist
- Layout matches design tokens
- Copy comes from shared core where applicable
- English and Spanish tested
- Font scaling tested
- VoiceOver/TalkBack tested
- Light/dark mode tested when available
- Error/loading states tested
```

---

## 16. Component Documentation Template

Use this template for every mobile component.

```markdown
# Component Name

## Purpose
What problem this component solves.

## Anatomy
List internal parts.

## Variants
List supported variants.

## States
- Default
- Pressed
- Focused
- Disabled
- Loading
- Error, if applicable
- Selected, if applicable

## Behavior
Describe interaction rules.

## Accessibility
Labels, roles, focus, contrast, touch target.

## Tokens
List color, spacing, typography, radius, and elevation tokens.

## iOS Implementation
SwiftUI notes.

## Android Implementation
Compose notes.

## Do
Correct usage examples.

## Don’t
Incorrect usage examples.
```

---

## 17. QA Checklist

Use this checklist before treating a mobile screen as complete.

### Design QA

- Screen uses Foam background where appropriate.
- Abyss is used for primary action emphasis.
- Coral is used rarely and intentionally.
- Layout respects safe areas.
- Spacing follows the mobile spacing scale.
- Components match documented patterns.
- English and Spanish have equal visual treatment where required.

### Content QA

- Copy comes from KMP shared core where applicable.
- English copy is correct.
- Spanish copy is correct.
- No hardcoded duplicate product strings exist in native views unless justified.
- Text expansion has been tested.

### Accessibility QA

- Touch targets meet platform minimums.
- Text contrast passes AA.
- Focus order is logical.
- VoiceOver labels are meaningful.
- TalkBack labels are meaningful.
- Dynamic Type / font scaling does not break layout.
- Color is not the only state indicator.

### Platform QA

- iOS simulator renders correctly.
- Android emulator renders correctly.
- iOS back behavior is correct.
- Android system back behavior is correct.
- Keyboard behavior is correct.
- Safe-area and inset behavior is correct.

### KMP QA

- Shared copy loads correctly.
- Shared state flows to native UI.
- Native events reach shared core.
- Swift ↔ Kotlin bridge signatures remain stable.
- Android ↔ Kotlin usage remains direct and consistent.

---

## 18. Recommended Next Screens to Document

After Language Selection, document these next:

1. Welcome screen
2. Language confirmation behavior
3. Onboarding intro screen
4. Sign-in screen
5. Account setup screen
6. Permission explanation screen
7. Home shell
8. Settings / language preference screen
9. Error recovery screen
10. Offline or connection issue screen

---

## 19. Implementation Guardrails

Do:

- Use shared KMP text for product copy.
- Keep native UI layers thin.
- Test English and Spanish together.
- Respect platform navigation conventions.
- Use Atlan semantic colors consistently.
- Treat the current iOS success as a baseline.

Do not:

- Hardcode copy separately in Swift and Android.
- Treat Spanish as secondary.
- Overuse Coral.
- Place actions inside unsafe screen areas.
- Depend on hover for mobile interaction.
- Suppress native back behavior without a product reason.
- Create platform-specific divergence without documenting it.

---

## 20. Definition of Done

A mobile screen or pattern is complete when:

- It follows the documented layout model.
- It uses approved brand colors and semantic component roles.
- It handles default, loading, error, disabled, and success states where applicable.
- It supports English and Spanish correctly.
- It respects iOS and Android platform conventions.
- It consumes shared KMP copy/state where applicable.
- It passes accessibility checks.
- It is documented with screen-level and component-level notes.
- It has been validated on simulator/emulator.

---

## 21. Baseline Status

Current status:

```text
Language Selection screen: validated on iOS simulator
KMP shared text bridge: validated
Swift ↔ Kotlin bridge: validated
Brand rendering: validated
Bilingual co-equal language principle: validated
```

This baseline should be committed before expanding additional mobile flows.
