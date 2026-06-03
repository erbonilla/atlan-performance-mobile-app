package com.atlan.performance.shared.domain.model

data class UserProfile(
    val id: String,
    val language: Language,
    val onboardingCompleted: Boolean,
    val notificationCadence: NotificationCadence,
    val explanationDensity: ExplanationDensity,
    /** Calibration answers, kept as adjustable settings. Null until calibration completes. */
    val persona: AthletePersona? = null
)
