package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.SessionProgress
import com.atlan.performance.shared.domain.repository.SessionProgressRepository

/** Returns the active session snapshot to offer Resume/Discard, or null when none is pending. */
class LoadSessionProgressUseCase(
    private val repository: SessionProgressRepository
) {
    suspend operator fun invoke(): SessionProgress? = repository.loadActive()
}
