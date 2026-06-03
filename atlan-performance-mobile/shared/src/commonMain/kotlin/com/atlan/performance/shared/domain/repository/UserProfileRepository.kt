package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.domain.model.AthletePersona
import com.atlan.performance.shared.domain.model.Language
import com.atlan.performance.shared.domain.model.UserProfile

/**
 * Contract for user-profile persistence. The initial fake is in-memory; a real local-database
 * implementation can be substituted without changing use cases. Suspend functions keep the contract
 * ready for async local I/O.
 */
interface UserProfileRepository {
    suspend fun getProfile(): UserProfile
    suspend fun setLanguage(language: Language)
    suspend fun setPersona(persona: AthletePersona)
    suspend fun markOnboardingCompleted()
}
