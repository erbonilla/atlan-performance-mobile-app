# Atlan Performance — External Beta Testing Guide

How to package and distribute the Atlan Performance mobile app to **external beta testers** on iOS
(**TestFlight**) and Android (**Google Play** closed/open testing, with **Firebase App Distribution**
as a faster alternative).

This guide is specific to *this* repository: a Kotlin Multiplatform shared core with a native SwiftUI
iOS app and a native Jetpack Compose Android app, generated/built via XcodeGen + Gradle.

> **Important context for this build:** Atlan currently has **no backend** — all data is stored
> **locally on the device** (SQLDelight + platform preferences). There are no accounts, no network
> calls, and no analytics. The sync queue + drain engine are real but the "upload" is simulated.
> Local notifications work. Communicate this to testers (see [§9](#9-what-to-tell-your-testers)).

---

## Contents
1. [Prerequisites](#1-prerequisites)
2. [App identity & version reference](#2-app-identity--version-reference)
3. [Pre-flight checklist (do this once)](#3-pre-flight-checklist-do-this-once)
4. [iOS — TestFlight (external testers)](#4-ios--testflight-external-testers)
5. [Android — Google Play closed/open testing](#5-android--google-play-closedopen-testing)
6. [Android/iOS — Firebase App Distribution (faster alternative)](#6-androidios--firebase-app-distribution-faster-alternative)
7. [Versioning & re-releasing builds](#7-versioning--re-releasing-builds)
8. [Privacy & store data declarations](#8-privacy--store-data-declarations)
9. [What to tell your testers](#9-what-to-tell-your-testers)
10. [Troubleshooting](#10-troubleshooting)
11. [Quick checklist](#11-quick-checklist)

---

## 1. Prerequisites

| You need | iOS | Android |
|---|---|---|
| Paid developer account | **Apple Developer Program** — $99/yr ([developer.apple.com](https://developer.apple.com/programs/)) | **Google Play Console** — $25 one-time ([play.google.com/console](https://play.google.com/console/signup)) |
| Build machine | macOS + **full Xcode** (not just CLT) | Any OS with **JDK 17** + Android SDK (Android Studio) |
| Repo tooling | `xcodegen` (`brew install xcodegen`) | Committed Gradle wrapper (`./gradlew`) |
| Distribution surface | App Store Connect → **TestFlight** | Play Console → **Testing** tracks (or Firebase) |
| Max external testers | 10,000 (email or public link) | Internal 100 · Closed/Open: large lists or opt-in URL |
| Review before testers get it | **Beta App Review** (first external build) | Light review on closed/open tracks; minimal on internal |

> If you only need a handful of testers *fast* and want to skip store review, jump to
> [§6 Firebase App Distribution](#6-androidios--firebase-app-distribution-faster-alternative).

---

## 2. App identity & version reference

These values are already set in the repo — they must **match** the records you create in App Store
Connect / Play Console.

| Field | Value | Defined in |
|---|---|---|
| iOS bundle ID | `com.atlan.performance.ios` | `iosApp/project.yml` |
| iOS display name | `Atlan Performance` | `iosApp/project.yml` (`INFOPLIST_KEY_CFBundleDisplayName`) |
| iOS min version | iOS **16.0** | `iosApp/project.yml` |
| iOS marketing / build | `MARKETING_VERSION` / `CURRENT_PROJECT_VERSION` | `iosApp/project.yml` |
| Android application ID | `com.atlan.performance.android` | `androidApp/build.gradle.kts` |
| Android min / target SDK | **26** / **35** | `androidApp/build.gradle.kts` |
| Android version | `versionCode` / `versionName` | `androidApp/build.gradle.kts` |
| App icon | ✅ set (iOS `Assets.xcassets/AppIcon`, opaque; Android `@drawable/atlan_logo`) | — |

---

## 3. Pre-flight checklist (do this once)

### 3.1 iOS — add the export-compliance key (avoids a prompt on every upload)
Atlan makes no use of non-exempt encryption (no network/crypto today). Declare it so TestFlight stops
asking. Edit **`iosApp/project.yml`** → `targets.AtlanPerformance.settings.base`:

```yaml
        INFOPLIST_KEY_ITSAppUsesNonExemptEncryption: NO
```

Then regenerate the project: `cd iosApp && xcodegen generate`.

> The notification permission (local reminders) does **not** require an Info.plist usage string —
> only a runtime authorization request, which the app already does. (If you later add a *real* Health
> integration, you must add `NSHealthShareUsageDescription` / `NSHealthUpdateUsageDescription` and the
> HealthKit capability.)

### 3.2 Android — create an upload keystore and a release signing config
Release builds must be **signed**. Create a keystore (keep it safe — losing it complicates updates):

```bash
keytool -genkeypair -v -keystore atlan-upload.keystore \
  -alias atlan -keyalg RSA -keysize 2048 -validity 10000
```

Add a release `signingConfig` to **`androidApp/build.gradle.kts`** (reads secrets from the
environment so they never get committed):

```kotlin
android {
    // …
    signingConfigs {
        create("release") {
            storeFile = System.getenv("ATLAN_KEYSTORE")?.let { file(it) }
            storePassword = System.getenv("ATLAN_KEYSTORE_PASSWORD")
            keyAlias = System.getenv("ATLAN_KEY_ALIAS") ?: "atlan"
            keyPassword = System.getenv("ATLAN_KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false            // (enable R8/minify later for production)
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

> **Use Google Play App Signing** (recommended/default): you upload with the *upload key* above, and
> Google manages the real app-signing key. Enable it when you create the app in Play Console.

### 3.3 Both — bump the version for every upload
Each store rejects a build number it has already seen. See [§7](#7-versioning--re-releasing-builds).

---

## 4. iOS — TestFlight (external testers)

### 4.1 One-time setup in App Store Connect
1. Enroll in the **Apple Developer Program**.
2. [App Store Connect](https://appstoreconnect.apple.com) → **My Apps → + → New App**.
   - Platform **iOS**, name **Atlan Performance**, primary language, **Bundle ID
     `com.atlan.performance.ios`** (register it under Certificates/Identifiers first if it isn't
     listed), SKU (any unique string, e.g. `atlan-performance`).
3. Under **App Information**, you can leave most store-listing fields blank for TestFlight, but you
   must complete **App Privacy** ([§8](#8-privacy--store-data-declarations)).

### 4.2 Build & upload (Xcode GUI — recommended)
```bash
cd iosApp
xcodegen generate            # regenerate the project from project.yml
open AtlanPerformance.xcodeproj
```
In Xcode:
1. Select the **AtlanPerformance** target → **Signing & Capabilities** → check **Automatically manage
   signing** and pick your **Team**. (Xcode creates the distribution cert + provisioning profile.)
2. Toolbar device selector → **Any iOS Device (arm64)**.
3. **Product → Archive.** (The pre-build script builds the KMP `Shared` framework for the device arch;
   first archive is slow.)
4. In the **Organizer** window that opens → **Distribute App → App Store Connect → Upload** → follow
   the prompts (automatic signing) → **Upload**.

> The KMP framework is linked automatically by the project's pre-build script
> (`./gradlew :shared:embedAndSignAppleFrameworkForXcode`). No manual framework steps.

### 4.3 Build & upload (command line — advanced)
```bash
cd iosApp && xcodegen generate
xcodebuild -project AtlanPerformance.xcodeproj -scheme AtlanPerformance \
  -configuration Release -destination 'generic/platform=iOS' \
  -archivePath build/AtlanPerformance.xcarchive archive

# Create ExportOptions.plist with method=app-store-connect and your teamID, then:
xcodebuild -exportArchive -archivePath build/AtlanPerformance.xcarchive \
  -exportOptionsPlist ExportOptions.plist -exportPath build/export
# Upload the resulting .ipa with the Transporter app, or:
xcrun altool --upload-app -f build/export/AtlanPerformance.ipa -t ios \
  --apiKey <KEY_ID> --apiIssuer <ISSUER_ID>
```

### 4.4 Turn on external testing
1. App Store Connect → your app → **TestFlight** tab. Wait for the build to finish **processing**
   (a few minutes to ~1 hr; you'll get an email).
2. Provide **export compliance** answer if prompted (No, per [§3.1](#31-ios--add-the-export-compliance-key-avoids-a-prompt-on-every-upload)).
3. **External Testing → + (Create a group)**, e.g. "Atlan Beta". Add the build to the group.
4. Add **What to Test** notes (paste from [§9](#9-what-to-tell-your-testers)) and submit for
   **Beta App Review** (usually < 24 h; required for the *first* external build).
5. Once approved: invite testers by **email**, or enable the **Public Link** and share the URL.
6. Testers install the free **TestFlight** app, open your invite/link, and tap **Install**.

---

## 5. Android — Google Play closed/open testing

### 5.1 Build a signed App Bundle (AAB)
```bash
# from atlan-performance-mobile/, with signing env vars exported (see §3.2):
export ATLAN_KEYSTORE=$PWD/atlan-upload.keystore
export ATLAN_KEYSTORE_PASSWORD=********
export ATLAN_KEY_ALIAS=atlan
export ATLAN_KEY_PASSWORD=********

./gradlew :androidApp:bundleRelease
# Output: androidApp/build/outputs/bundle/release/androidApp-release.aab
```

### 5.2 One-time setup in Play Console
1. [Play Console](https://play.google.com/console) → **Create app** (name **Atlan Performance**, app,
   free). Accept declarations.
2. **Set up → App signing**: keep **Play App Signing** enabled (default).
3. Complete the required **App content** declarations: privacy policy URL, data safety
   ([§8](#8-privacy--store-data-declarations)), ads (none), content rating questionnaire, target
   audience, government app (no), etc. These are required before a testing track can roll out.

### 5.3 Pick a testing track and upload
- **Internal testing** — fastest, up to **100** testers, near-instant, minimal review. Best to start.
- **Closed testing** — testers via an **email list** or **Google Group**; light review.
- **Open testing** — anyone with the opt-in URL; more review/requirements.

Steps (Closed testing shown; Internal is identical and faster):
1. **Testing → Closed testing → Create track** (or use the default) → **Create new release**.
2. **Upload** `androidApp-release.aab`. Add **release notes**.
3. **Testers** tab → create an email list (or Google Group) and add tester emails.
4. **Review release → Start rollout to Closed testing.**
5. Copy the **"Copy link"** opt-in URL and send it to testers. They open it, **become a tester**,
   then install from the Play Store (link or search once joined).

> Direct APK install (no store) is also possible for quick checks — see [§6](#6-androidios--firebase-app-distribution-faster-alternative).

---

## 6. Android/iOS — Firebase App Distribution (faster alternative)

Good for early external betas without store review. Android (APK/AAB) is seamless; iOS requires
**ad-hoc provisioning** (each tester device UDID registered in your Apple Developer account), so for
iOS, TestFlight ([§4](#4-ios--testflight-external-testers)) is usually easier for *external* testers.

1. Create a Firebase project, add the app(s) (`com.atlan.performance.android` /
   `com.atlan.performance.ios`).
2. Build artifacts:
   ```bash
   ./gradlew :androidApp:assembleRelease   # APK: androidApp/build/outputs/apk/release/
   ```
3. Upload via the Firebase console (App Distribution → upload) or the CLI:
   ```bash
   firebase appdistribution:distribute androidApp/build/outputs/apk/release/androidApp-release.apk \
     --app <FIREBASE_APP_ID> --groups "atlan-beta" --release-notes "Atlan beta build"
   ```
4. Testers get an email invite + install the **App Tester** app (iOS) or install the APK (Android).

> Firebase App Distribution is a *distribution* tool only — it adds **no** analytics/backend to the
> app and keeps the no-backend posture intact.

---

## 7. Versioning & re-releasing builds

Increment **before every upload** (stores reject duplicate build numbers).

**iOS** — `iosApp/project.yml`:
```yaml
        MARKETING_VERSION: "1.0"       # user-facing, bump per beta cycle (e.g. 1.0 → 1.1)
        CURRENT_PROJECT_VERSION: "2"   # build number, MUST increase every upload
```
Then `cd iosApp && xcodegen generate` and re-archive.

**Android** — `androidApp/build.gradle.kts`:
```kotlin
        versionCode = 2          // MUST increase every upload
        versionName = "0.1.1"    // user-facing
```

After approval, **new builds to the same external group/track usually don't need full re-review** —
only the first external iOS build does Beta App Review.

---

## 8. Privacy & store data declarations

Both stores require these forms **even for beta** — but they're simple here because Atlan currently
**collects no data**.

| Declaration | Answer for this build | Where |
|---|---|---|
| Data collected / shared | **None** — all data is on-device; no accounts, no analytics, no network | App Store Connect → **App Privacy**; Play Console → **Data safety** |
| Tracking (ATT) | **No** tracking | App Store Connect |
| Privacy policy URL | **Required** — host a short page stating "Atlan stores your training data only on your device and does not collect, share, or transmit personal data." | Both consoles |
| Account required | **No** | Both |
| Encryption (export compliance) | **No** non-exempt encryption (see [§3.1](#31-ios--add-the-export-compliance-key-avoids-a-prompt-on-every-upload)) | App Store Connect |
| Permissions used | **Notifications** (local reminders, optional). No location, camera, contacts, Health (yet). | Disclose in store listing notes if asked |

> A free privacy-policy host (e.g. a GitHub Pages page or a Notion public page) is sufficient for beta.
> Keep it accurate: if/when you add a backend, accounts, Health sync, or analytics, **update these
> declarations**.

---

## 9. What to tell your testers

Paste this into TestFlight "What to Test" / Play release notes / your invite email:

> **Welcome to the Atlan Performance beta 🌊**
>
> Atlan is a calm, offline-first coaching app for endurance athletes. This is an early beta.
>
> **Please know:**
> - **Everything is stored on your device.** There's no account and no internet sync yet — your data
>   won't move between devices, and reinstalling clears it.
> - **It's bilingual** — switch English/Español anytime in **Settings** (or on first launch).
> - **Try the full flow:** choose a language → onboarding → today's session → **Start session** →
>   the Wet Mode 4-set timer (swipe or tap to Complete/Pause; long-swipe down to exit) → session
>   summary. Then explore **Workout plan**, **History**, **Progress**, and **Settings**.
> - **Reminders are optional and local** (Settings → Reminders) — no spam, ever.
>
> **What we'd love feedback on:** does it feel calm and clear? Is anything confusing? Any rough edges
> on your device? Reply to this invite / use the in-app TestFlight **feedback** (screenshot + note).
>
> **Known limitations (by design, for now):** no sign-in, no cross-device sync, no Apple
> Health/Health Connect, no real push notifications.

Testers can send feedback via **TestFlight** (screenshot → annotate → send) or your chosen channel
(email/Google Form). Set expectations on cadence and how to report bugs.

---

## 10. Troubleshooting

| Symptom | Fix |
|---|---|
| **iOS:** "No account for team" / signing errors | Sign in to your Apple ID in Xcode → Settings → Accounts; enable **Automatically manage signing**; pick the right Team. |
| **iOS:** archive missing the `Shared` framework / link errors | Archive against **Any iOS Device (arm64)**, not a simulator; let the pre-build Gradle step finish; ensure `JAVA_HOME` points at JDK 17 (the script targets Homebrew `openjdk@17`). |
| **iOS:** "Missing compliance" on the build | Add `INFOPLIST_KEY_ITSAppUsesNonExemptEncryption: NO` ([§3.1](#31-ios--add-the-export-compliance-key-avoids-a-prompt-on-every-upload)) and regenerate. |
| **iOS:** build stuck "Processing" | Normal — wait for the email; can take up to ~1 hr. |
| **Android:** "App not signed" / upload rejected | Configure the release `signingConfig` ([§3.2](#32-android--create-an-upload-keystore-and-a-release-signing-config)) and export the keystore env vars before `bundleRelease`. |
| **Android:** "Version code already used" | Bump `versionCode` ([§7](#7-versioning--re-releasing-builds)). |
| **Android:** testers can't find the app | They must first **accept the opt-in link**, then install from Play; propagation can take minutes. |
| **Either:** new APK/icon not showing | Increment the version; for iOS re-run `xcodegen generate` after editing `project.yml`. |
| `local.properties` / `.xcodeproj` not in repo | Expected — both are git-ignored and regenerated (`sdk.dir` auto / `xcodegen generate`). |

---

## 11. Quick checklist

**Once:**
- [ ] Apple Developer Program enrolled · App Store Connect app record (bundle `com.atlan.performance.ios`)
- [ ] Google Play Console app created · Play App Signing enabled
- [ ] iOS: `INFOPLIST_KEY_ITSAppUsesNonExemptEncryption: NO` added + `xcodegen generate`
- [ ] Android: upload keystore created + release `signingConfig` added
- [ ] Privacy policy URL hosted · App Privacy + Data Safety forms completed (no data collected)

**Every beta build:**
- [ ] Bump iOS `CURRENT_PROJECT_VERSION` / Android `versionCode`
- [ ] iOS: `xcodegen generate` → Archive → Distribute → TestFlight → add to external group → (first time) Beta App Review → invite/link
- [ ] Android: `./gradlew :androidApp:bundleRelease` → Play Console → testing track → upload AAB → add testers → roll out → share opt-in link
- [ ] Update "What to Test" / release notes (use the [§9](#9-what-to-tell-your-testers) blurb)
- [ ] Confirm at least one tester can install on each platform

---

*Atlan Performance is offline-first and local-only in this build. Keep the privacy declarations in
sync with reality as the backend, accounts, Health, and real notifications land.*
