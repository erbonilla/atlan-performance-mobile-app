package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.repository.SessionProgressRepository

/** Clears a saved snapshot — on session completion, or when the user discards a resumable session. */
class ClearSessionProgressUseCase(
    private val repository: SessionProgressRepository
) {
    suspend operator fun invoke(sessionId: String) = repository.clear(sessionId)
}
