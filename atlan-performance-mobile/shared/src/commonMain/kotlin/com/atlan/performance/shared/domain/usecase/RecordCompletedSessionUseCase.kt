package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.CompletedSession
import com.atlan.performance.shared.domain.repository.WorkoutHistoryRepository
import com.atlan.performance.shared.presentation.WorkoutTimerState

/**
 * Records a finished session into Workout History. Captures the calm facts — sets completed, total
 * elapsed, optional perceived effort. Partial (ended-early) sessions are recorded too, as valid
 * outcomes. The platform supplies `finishedAtIso` (it owns the clock).
 */
class RecordCompletedSessionUseCase(
    private val repository: WorkoutHistoryRepository
) {
    suspend operator fun invoke(
        timer: WorkoutTimerState,
        title: String,
        perceivedEffort: String?,
        finishedAtIso: String
    ) {
        repository.record(
            CompletedSession(
                id = "${timer.sessionId}@$finishedAtIso",
                title = title,
                completedSetCount = timer.completedCount,
                totalSetCount = timer.setCount,
                totalElapsedLabel = timer.totalElapsedLabel(),
                perceivedEffort = perceivedEffort,
                finishedAtIso = finishedAtIso,
                fullyCompleted = timer.completedCount >= timer.setCount
            )
        )
    }
}
