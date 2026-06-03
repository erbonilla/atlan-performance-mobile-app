package com.atlan.performance.shared.localization

/**
 * Stable identifiers for user-facing copy. Both EN and ES must provide a value for every key
 * (enforced by LocalizationParityTest). String form matches the keys in LOCALIZATION.md.
 */
enum class LocalizedStringKey(val raw: String) {
    LANGUAGE_CHOOSE_ENGLISH("language.choose.english"),
    LANGUAGE_CHOOSE_SPANISH("language.choose.spanish"),
    ONBOARDING_WELCOME_EYEBROW("onboarding.welcome.eyebrow"),
    ONBOARDING_WELCOME_TITLE("onboarding.welcome.title"),
    ONBOARDING_WELCOME_BODY("onboarding.welcome.body"),
    ONBOARDING_WELCOME_CTA("onboarding.welcome.cta"),
    CALIBRATION_TITLE("calibration.title"),
    CALIBRATION_SUBTITLE("calibration.subtitle"),
    TUNED_TITLE("tuned.title"),
    TUNED_CTA("tuned.cta"),
    DASHBOARD_TODAY_LABEL("dashboard.today.label"),
    DASHBOARD_START_SESSION("dashboard.startSession"),
    SESSION_WHY_THRESHOLD_TITLE("session.whyThreshold.title"),
    WET_MODE_OFFLINE_CACHED("wetMode.offlineCached"),
    WET_MODE_PAUSE("wetMode.pause"),
    WET_MODE_COMPLETE("wetMode.complete"),
    SWAPPER_ACCEPT("swapper.accept"),
    SWAPPER_SKIP_TODAY("swapper.skipToday"),

    // Session Detail
    SESSION_DETAIL_DATE("sessionDetail.date"),

    // Wet Mode — offline / sync status (shared by the timer pill and the summary block)
    WET_MODE_OFFLINE_ONLINE("wetMode.offline.online"),
    WET_MODE_OFFLINE_SYNC_PENDING("wetMode.offline.syncPending"),
    WET_MODE_OFFLINE_SAVED_LOCALLY("wetMode.offline.savedLocally"),
    WET_MODE_SYNCING("wetMode.sync.syncing"),
    WET_MODE_RETRY_SYNC("wetMode.sync.retry"),
    WET_MODE_SYNC_SAVED_OFFLINE("wetMode.sync.savedOffline"),
    WET_MODE_SYNC_FAILED("wetMode.sync.failed"),

    // Wet Mode — gesture tutorial (one-time coach mark)
    WET_MODE_TUTORIAL_TITLE("wetMode.tutorial.title"),
    WET_MODE_TUTORIAL_COMPLETE("wetMode.tutorial.complete"),
    WET_MODE_TUTORIAL_PAUSE("wetMode.tutorial.pause"),
    WET_MODE_TUTORIAL_EXIT("wetMode.tutorial.exit"),
    WET_MODE_TUTORIAL_GOT_IT("wetMode.tutorial.gotIt"),

    // Wet Mode — active / rest timer surface
    WET_MODE_REST("wetMode.rest"),
    WET_MODE_NEXT("wetMode.next"),
    WET_MODE_PAUSED("wetMode.paused"),
    WET_MODE_OVER_TARGET("wetMode.overTarget"),
    WET_MODE_OVER_TARGET_PACE("wetMode.overTargetPace"),
    WET_MODE_TARGET_PACE("wetMode.targetPace"),          // "Target pace %1$s"
    WET_MODE_SET("wetMode.set"),                         // "Set %1$s"
    WET_MODE_SET_COMPLETE("wetMode.setComplete"),        // "Set %1$s complete"

    // Wet Mode — two-zone actions (visible titles, swipe hints, accessibility labels)
    WET_MODE_RESUME("wetMode.resume"),
    WET_MODE_END("wetMode.end"),
    WET_MODE_END_SESSION("wetMode.endSession"),
    WET_MODE_SKIP_REST("wetMode.skipRest"),
    WET_MODE_SWIPE_LEFT_HINT("wetMode.swipeLeftHint"),
    WET_MODE_SWIPE_RIGHT_HINT("wetMode.swipeRightHint"),
    WET_MODE_LABEL_PAUSE("wetMode.label.pause"),
    WET_MODE_LABEL_RESUME("wetMode.label.resume"),
    WET_MODE_LABEL_COMPLETE("wetMode.label.complete"),
    WET_MODE_LABEL_SKIP_REST("wetMode.label.skipRest"),
    WET_MODE_ACTION_COMPLETE("wetMode.action.complete"),
    WET_MODE_ACTION_PAUSE("wetMode.action.pause"),
    WET_MODE_ACTION_EXIT("wetMode.action.exit"),

    // Wet Mode — session summary
    WET_MODE_SUMMARY_COMPLETE("wetMode.summary.complete"),
    WET_MODE_SUMMARY_ENDED("wetMode.summary.ended"),
    WET_MODE_SUMMARY_SETS("wetMode.summary.sets"),       // "%1$s of %2$s sets"
    WET_MODE_DONE("wetMode.summary.done"),

    // Wet Mode — confirmation dialogs
    WET_MODE_EARLY_TITLE("wetMode.early.title"),
    WET_MODE_EARLY_MESSAGE("wetMode.early.message"),     // "This marks %1$s as complete with %2$s remaining."
    WET_MODE_EARLY_CONFIRM("wetMode.early.confirm"),
    WET_MODE_KEEP_GOING("wetMode.keepGoing"),
    WET_MODE_EXIT_TITLE("wetMode.exit.title"),
    WET_MODE_EXIT_MESSAGE("wetMode.exit.message"),

    // Wet Mode — screen-reader announcements
    WET_MODE_ANNOUNCE_PAUSED("wetMode.announce.paused"),
    WET_MODE_ANNOUNCE_SET_STARTED("wetMode.announce.setStarted"),          // "%1$s started"
    WET_MODE_ANNOUNCE_SET_COMPLETE_REST("wetMode.announce.setCompleteRest") // "Set %1$s complete. Rest."
}
