# Localization — Atlan Performance

## Rules
- English and Spanish are **first-class**. Neither is a fallback for the other.
- **No flag icons.**
- **Do not** auto-select language by device locale or IP. The user chooses before account creation.
- Long Spanish strings **wrap**; they do not shrink to fit.
- Product voice preserves **emotional parity**, not literal translation.
- Language selection is persisted locally and editable later in Settings.

## Architecture
Shared Kotlin owns copy keys and per-language copy:
- `localization/LocalizedStringKey.kt` — enum of keys.
- `localization/AtlanCopy.kt` — resolver: `AtlanCopy.get(key, language)`.
- `localization/EnglishCopy.kt`, `SpanishCopy.kt` — per-language maps.

Platform layers read resolved strings from the shared layer for the initial setup, then can migrate to
native string catalogs (`.strings` / `strings.xml`) later while keeping the shared keys authoritative.

## Initial copy keys
```
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

sessionDetail.date

# Wet Mode — fully keyed timer surface (EN + ES, both platforms)
wetMode.offline.online
wetMode.offline.syncPending
wetMode.offline.savedLocally
wetMode.sync.syncing
wetMode.sync.retry
wetMode.sync.savedOffline
wetMode.sync.failed
wetMode.tutorial.title
wetMode.tutorial.complete
wetMode.tutorial.pause
wetMode.tutorial.exit
wetMode.tutorial.gotIt
wetMode.rest
wetMode.next
wetMode.paused
wetMode.overTarget
wetMode.overTargetPace
wetMode.targetPace            # "Target pace %1$s"
wetMode.set                  # "Set %1$s"
wetMode.setComplete          # "Set %1$s complete"
wetMode.resume
wetMode.end
wetMode.endSession
wetMode.skipRest
wetMode.swipeLeftHint
wetMode.swipeRightHint
wetMode.label.pause
wetMode.label.resume
wetMode.label.complete
wetMode.label.skipRest
wetMode.action.complete
wetMode.action.pause
wetMode.action.exit
wetMode.summary.complete
wetMode.summary.ended
wetMode.summary.sets         # "%1$s of %2$s sets"
wetMode.summary.done
wetMode.early.title
wetMode.early.message        # "This marks %1$s as complete with %2$s remaining."
wetMode.early.confirm
wetMode.keepGoing
wetMode.exit.title
wetMode.exit.message
wetMode.announce.paused
wetMode.announce.setStarted        # "%1$s started"
wetMode.announce.setCompleteRest   # "Set %1$s complete. Rest."
```

Templated keys carry positional `%1$s` / `%2$s` placeholders, resolved via `AtlanCopy.format(key,
language, …)` (Kotlin) / `localized(key, language, …)` (Swift) so word order stays per-language.

## Parity enforcement
`LocalizationParityTest` asserts: EN and ES onboarding keys both exist; Spanish welcome title is
non-empty; language options have equal priority; Spanish is not treated as an optional fallback.
