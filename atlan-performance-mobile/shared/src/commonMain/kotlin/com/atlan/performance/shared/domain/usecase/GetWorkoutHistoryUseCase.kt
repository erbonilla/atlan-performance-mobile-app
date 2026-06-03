package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.CompletedSession
import com.atlan.performance.shared.domain.repository.WorkoutHistoryRepository

/** Returns recent finished sessions, newest first, for the Workout History screen. */
class GetWorkoutHistoryUseCase(
    private val repository: WorkoutHistoryRepository
) {
    suspend operator fun invoke(): List<CompletedSession> = repository.recent()
}
