package com.atlan.performance.shared.presentation

import com.atlan.performance.shared.domain.model.ExplanationDensity
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.NotificationCadence
import com.atlan.performance.shared.domain.model.ScheduleFlexibility
import com.atlan.performance.shared.domain.model.TrainingMotivation

/**
 * Accumulates onboarding answers as adjustable settings (not scores). Drives the Tuned Summary.
 */
data class OnboardingState(
    val language: Language? = null,
    val motivation: TrainingMotivation? = null,
    val flexibility: ScheduleFlexibility? = null,
    val explanationDensity: ExplanationDensity? = null,
    val notificationCadence: NotificationCadence? = null
) {
    val isComplete: Boolean
        get() = language != null && motivation != null && flexibility != null &&
            explanationDensity != null && notificationCadence != null
}
