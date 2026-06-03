package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.SessionProgress
import com.atlan.performance.shared.domain.repository.SessionProgressRepository
import com.atlan.performance.shared.presentation.WorkoutTimerState

/**
 * Persists an in-progress session snapshot (local-first) so it can be resumed after the app is
 * closed or killed. A finished session is never persisted — completion clears progress instead.
 */
class SaveSessionProgressUseCase(
    private val repository: SessionProgressRepository
) {
    suspend operator fun invoke(timer: WorkoutTimerState, restSeconds: Int, savedAtIso: String = "") {
        if (timer.isComplete) return
        repository.save(
            SessionProgress(
                sessionId = timer.sessionId,
                setIndex = timer.setIndex,
                setCount = timer.setCount,
                completedCount = timer.completedCount,
                restSeconds = restSeconds,
                savedAtIso = savedAtIso
            )
        )
    }
}
