package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.AthletePersona
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.repository.UserProfileRepository
import com.atlan.performance.shared.presentation.OnboardingState

/**
 * Persists language + calibration answers as adjustable settings (never scores) and marks onboarding
 * complete. Writes locally first.
 */
class CompleteOnboardingUseCase(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(state: OnboardingState) {
        require(state.isComplete) { "Onboarding state must be complete before finishing onboarding." }
        userProfileRepository.setLanguage(state.language ?: Language.EN)
        userProfileRepository.setPersona(
            AthletePersona(
                motivation = state.motivation!!,
                flexibility = state.flexibility!!,
                explanationDensity = state.explanationDensity!!,
                notificationCadence = state.notificationCadence!!
            )
        )
        userProfileRepository.markOnboardingCompleted()
    }
}
