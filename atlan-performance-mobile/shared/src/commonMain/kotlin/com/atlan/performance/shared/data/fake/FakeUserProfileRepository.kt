package com.atlan.performance.shared.data.fake

import com.atlan.performance.shared.data.seed.SeedUser
import com.atlan.performance.shared.domain.model.AthletePersona
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.UserProfile
import com.atlan.performance.shared.domain.repository.UserProfileRepository

/**
 * In-memory user profile. Replaceable by a real local-DB repository without changing use cases.
 * Writes are immediate and local (offline-first).
 */
class FakeUserProfileRepository(
    seed: UserProfile = SeedUser.profile
) : UserProfileRepository {

    private var profile: UserProfile = seed

    override suspend fun getProfile(): UserProfile = profile

    override suspend fun setLanguage(language: Language) {
        profile = profile.copy(language = language)
    }

    override suspend fun setPersona(persona: AthletePersona) {
        profile = profile.copy(
            persona = persona,
            notificationCadence = persona.notificationCadence,
            explanationDensity = persona.explanationDensity
        )
    }

    override suspend fun markOnboardingCompleted() {
        profile = profile.copy(onboardingCompleted = true)
    }
}
