package com.atlan.performance.shared.data.seed

import com.atlan.performance.shared.domain.model.ExplanationDensity
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.NotificationCadence
import com.atlan.performance.shared.domain.model.UserProfile

/** Seed profile for the initial setup. Language is unset-by-intent (defaults EN) and chosen on first run. */
object SeedUser {
    val profile = UserProfile(
        id = "user-local",
        language = Language.EN,
        onboardingCompleted = false,
        notificationCadence = NotificationCadence.MINIMAL,
        explanationDensity = ExplanationDensity.STANDARD,
        persona = null
    )
}
