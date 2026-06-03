package com.atlan.performance.android.navigation

/**
 * Native route state for the Android shell. A small, explicit Compose navigation layer (per the
 * setup prompt) covering the eight primary destinations. Why Modal and Session Swapper are modal
 * overlays, not routes (see AtlanNavGraph).
 */
enum class AtlanRoute {
    LANGUAGE,
    WELCOME,
    CALIBRATION,
    TUNED_SUMMARY,
    DASHBOARD,
    WORKOUT_PLAN,
    SESSION_DETAIL,
    WORKOUT_PREP,
    WET_MODE,
    SETTINGS,
    HOW_IT_WORKS,
    HISTORY
}
