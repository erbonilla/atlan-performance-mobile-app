package com.atlan.performance.shared.localization

import com.atlan.performance.shared.localization.LocalizedStringKey.*

/** First-class English copy. Not a fallback for Spanish; equal priority. */
internal object EnglishCopy {
    val values: Map<LocalizedStringKey, String> = mapOf(
        LANGUAGE_CHOOSE_ENGLISH to "English",
        LANGUAGE_CHOOSE_SPANISH to "Español",
        ONBOARDING_WELCOME_EYEBROW to "Welcome",
        ONBOARDING_WELCOME_TITLE to "Built for athletes with lives.",
        ONBOARDING_WELCOME_BODY to "Atlan adapts the plan around your week — not the other way around. " +
            "Before we start, a few short questions to tune the app to how you actually train.",
        ONBOARDING_WELCOME_CTA to "Begin",
        CALIBRATION_TITLE to "No right answers. Just calibration.",
        CALIBRATION_SUBTITLE to "A few short questions",
        TUNED_TITLE to "Here's how I'll work with you.",
        TUNED_CTA to "See your first session",
        DASHBOARD_TODAY_LABEL to "Today · Pool",
        DASHBOARD_START_SESSION to "Start session",
        SESSION_WHY_THRESHOLD_TITLE to "Why threshold",
        WET_MODE_OFFLINE_CACHED to "Offline · Cached",
        WET_MODE_PAUSE to "Pause",
        WET_MODE_COMPLETE to "Complete",
        SWAPPER_ACCEPT to "Accept swap",
        SWAPPER_SKIP_TODAY to "Skip today",

        SESSION_DETAIL_DATE to "Today · Tuesday, May 26",

        WET_MODE_OFFLINE_ONLINE to "Online",
        WET_MODE_OFFLINE_SYNC_PENDING to "Pending sync",
        WET_MODE_OFFLINE_SAVED_LOCALLY to "Saved locally",
        WET_MODE_SYNCING to "Syncing…",
        WET_MODE_RETRY_SYNC to "Retry sync",
        WET_MODE_SYNC_SAVED_OFFLINE to "Saved offline. We'll sync when you're back online.",
        WET_MODE_SYNC_FAILED to "Sync failed — your results are safe. Retry when you're back online.",

        WET_MODE_TUTORIAL_TITLE to "How it works",
        WET_MODE_TUTORIAL_COMPLETE to "Swipe right or tap to Complete",
        WET_MODE_TUTORIAL_PAUSE to "Swipe left or tap to Pause",
        WET_MODE_TUTORIAL_EXIT to "Swipe down to Exit",
        WET_MODE_TUTORIAL_GOT_IT to "Got it",

        WET_MODE_REST to "Rest",
        WET_MODE_NEXT to "Next",
        WET_MODE_PAUSED to "Paused",
        WET_MODE_OVER_TARGET to "Over target",
        WET_MODE_OVER_TARGET_PACE to "Over target pace",
        WET_MODE_TARGET_PACE to "Target pace %1\$s",
        WET_MODE_SET to "Set %1\$s",
        WET_MODE_SET_COMPLETE to "Set %1\$s complete",

        WET_MODE_RESUME to "Resume",
        WET_MODE_END to "End",
        WET_MODE_END_SESSION to "End session",
        WET_MODE_SKIP_REST to "Skip Rest",
        WET_MODE_SWIPE_LEFT_HINT to "← Swipe Left",
        WET_MODE_SWIPE_RIGHT_HINT to "Swipe Right →",
        WET_MODE_LABEL_PAUSE to "Pause set. Swipe left or activate.",
        WET_MODE_LABEL_RESUME to "Resume set. Swipe left or activate.",
        WET_MODE_LABEL_COMPLETE to "Complete set. Swipe right or activate.",
        WET_MODE_LABEL_SKIP_REST to "Skip rest. Swipe right or activate.",
        WET_MODE_ACTION_COMPLETE to "Complete set",
        WET_MODE_ACTION_PAUSE to "Pause set",
        WET_MODE_ACTION_EXIT to "Exit Wet Mode",

        WET_MODE_SUMMARY_COMPLETE to "Workout complete",
        WET_MODE_SUMMARY_ENDED to "Session ended",
        WET_MODE_SUMMARY_SETS to "%1\$s of %2\$s sets",
        WET_MODE_DONE to "Done",

        WET_MODE_EARLY_TITLE to "Complete set early?",
        WET_MODE_EARLY_MESSAGE to "This marks %1\$s as complete with %2\$s remaining.",
        WET_MODE_EARLY_CONFIRM to "Complete set",
        WET_MODE_KEEP_GOING to "Keep going",
        WET_MODE_EXIT_TITLE to "End this session?",
        WET_MODE_EXIT_MESSAGE to "Your completed sets are saved. The rest of the session won't be recorded.",

        WET_MODE_ANNOUNCE_PAUSED to "Set paused",
        WET_MODE_ANNOUNCE_SET_STARTED to "%1\$s started",
        WET_MODE_ANNOUNCE_SET_COMPLETE_REST to "Set %1\$s complete. Rest."
    )
}
