package com.atlan.performance.android.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.atlan.performance.android.screen.HowItWorksScreen
import com.atlan.performance.android.screen.dashboard.TodayDashboardScreen
import com.atlan.performance.android.screen.onboarding.CalibrationScreen
import com.atlan.performance.android.screen.onboarding.LanguageSelectionScreen
import com.atlan.performance.android.screen.onboarding.TunedSummaryScreen
import com.atlan.performance.android.screen.onboarding.WelcomeScreen
import com.atlan.performance.android.screen.session.SessionDetailScreen
import com.atlan.performance.android.screen.session.SessionSwapperSheet
import com.atlan.performance.android.screen.session.WetModeScreen
import com.atlan.performance.android.screen.session.WhyModalSheet
import com.atlan.performance.android.screen.session.WorkoutPlanListScreen
import com.atlan.performance.android.screen.session.WorkoutPrepScreen
import com.atlan.performance.android.screen.settings.SettingsScreen
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.domain.model.Language

/**
 * Holds route + modal + language state and dispatches between screens. Modal overlays (Why,
 * Swapper) render on top of the current route. The shared core is created once and passed down.
 */
@Composable
fun AtlanNavGraph(shared: AtlanShared) {
    // Non-sensitive preferences persist via SharedPreferences — a platform storage adapter.
    // TODO(persistence): move to a shared PreferencesRepository when the real storage layer lands.
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("atlan_prefs", Context.MODE_PRIVATE) }

    var route by remember { mutableStateOf(AtlanRoute.LANGUAGE) }
    var language by remember {
        mutableStateOf(if (prefs.getString("language", "en") == "es") Language.ES else Language.EN)
    }
    var wetTutorialSeen by remember { mutableStateOf(prefs.getBoolean("tutorialSeen", false)) }
    var hapticsEnabled by remember { mutableStateOf(prefs.getBoolean("haptics", true)) }
    var keepAwake by remember { mutableStateOf(prefs.getBoolean("keepAwake", true)) }
    var restSeconds by remember { mutableStateOf(prefs.getInt("restSeconds", 30)) }
    // True when Wet Mode should rebuild from a saved snapshot (resume) rather than start fresh.
    var wetResume by remember { mutableStateOf(false) }

    fun setLanguage(value: Language) {
        language = value
        prefs.edit().putString("language", if (value == Language.ES) "es" else "en").commit()
    }
    fun setHaptics(value: Boolean) { hapticsEnabled = value; prefs.edit().putBoolean("haptics", value).commit() }
    fun setKeepAwake(value: Boolean) { keepAwake = value; prefs.edit().putBoolean("keepAwake", value).commit() }
    fun setRestSeconds(value: Int) { restSeconds = value; prefs.edit().putInt("restSeconds", value).commit() }
    fun markTutorialSeen() { wetTutorialSeen = true; prefs.edit().putBoolean("tutorialSeen", true).commit() }

    // Modal overlays, keyed by the concept/session they explain.
    var whyConceptKey by remember { mutableStateOf<String?>(null) }
    var swapperForSessionId by remember { mutableStateOf<String?>(null) }

    when (route) {
        AtlanRoute.LANGUAGE -> LanguageSelectionScreen(
            onSelect = { selected ->
                setLanguage(selected)
                route = AtlanRoute.WELCOME
            }
        )

        AtlanRoute.WELCOME -> WelcomeScreen(
            language = language,
            onBack = { route = AtlanRoute.LANGUAGE },
            onBegin = { route = AtlanRoute.CALIBRATION }
        )

        AtlanRoute.CALIBRATION -> CalibrationScreen(
            language = language,
            onBack = { route = AtlanRoute.WELCOME },
            onContinue = { route = AtlanRoute.TUNED_SUMMARY }
        )

        AtlanRoute.TUNED_SUMMARY -> TunedSummaryScreen(
            language = language,
            onSeeFirstSession = { route = AtlanRoute.DASHBOARD }
        )

        AtlanRoute.DASHBOARD -> TodayDashboardScreen(
            shared = shared,
            language = language,
            onStartSession = { route = AtlanRoute.SESSION_DETAIL },
            onWhy = { key -> whyConceptKey = key },
            onOpenSwapper = { sessionId -> swapperForSessionId = sessionId },
            onViewPlan = { route = AtlanRoute.WORKOUT_PLAN },
            onResume = { wetResume = true; route = AtlanRoute.WET_MODE },
            onSettings = { route = AtlanRoute.SETTINGS }
        )

        AtlanRoute.WORKOUT_PLAN -> WorkoutPlanListScreen(
            shared = shared,
            language = language,
            onBack = { route = AtlanRoute.DASHBOARD },
            onOpenToday = { route = AtlanRoute.SESSION_DETAIL }
        )

        AtlanRoute.SESSION_DETAIL -> SessionDetailScreen(
            shared = shared,
            language = language,
            onBack = { route = AtlanRoute.DASHBOARD },
            onWhy = { key -> whyConceptKey = key },
            onStart = { route = AtlanRoute.WORKOUT_PREP }
        )

        AtlanRoute.WORKOUT_PREP -> WorkoutPrepScreen(
            shared = shared,
            language = language,
            onBack = { route = AtlanRoute.SESSION_DETAIL },
            onBegin = { wetResume = false; route = AtlanRoute.WET_MODE }
        )

        AtlanRoute.WET_MODE -> WetModeScreen(
            shared = shared,
            language = language,
            onExit = { route = AtlanRoute.WORKOUT_PREP },
            tutorialSeen = wetTutorialSeen,
            onTutorialSeen = { markTutorialSeen() },
            hapticsEnabled = hapticsEnabled,
            keepScreenAwake = keepAwake,
            restSeconds = restSeconds,
            resume = wetResume
        )

        AtlanRoute.SETTINGS -> SettingsScreen(
            language = language,
            onBack = { route = AtlanRoute.DASHBOARD },
            onLanguageChange = { setLanguage(it) },
            hapticsEnabled = hapticsEnabled,
            onHapticsChange = { setHaptics(it) },
            keepAwake = keepAwake,
            onKeepAwakeChange = { setKeepAwake(it) },
            restSeconds = restSeconds,
            onRestSecondsChange = { setRestSeconds(it) },
            onHowItWorks = { route = AtlanRoute.HOW_IT_WORKS }
        )

        AtlanRoute.HOW_IT_WORKS -> HowItWorksScreen(
            language = language,
            onBack = { route = AtlanRoute.SETTINGS }
        )
    }

    // Modal: Why Modal (bottom sheet).
    whyConceptKey?.let { key ->
        WhyModalSheet(
            shared = shared,
            conceptKey = key,
            language = language,
            onDismiss = { whyConceptKey = null }
        )
    }

    // Modal: Session Swapper (bottom sheet).
    swapperForSessionId?.let { sessionId ->
        SessionSwapperSheet(
            shared = shared,
            sessionId = sessionId,
            onDismiss = { swapperForSessionId = null }
        )
    }
}
