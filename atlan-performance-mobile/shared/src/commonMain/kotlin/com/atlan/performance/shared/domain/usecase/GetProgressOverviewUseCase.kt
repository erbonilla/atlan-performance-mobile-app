package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.ProgressOverview
import com.atlan.performance.shared.domain.repository.WorkoutHistoryRepository

/**
 * Aggregates recent Workout History into a calm [ProgressOverview]. Pure tallies — sessions, sets,
 * full vs adjusted, and an effort distribution. No streak, deficit, or comparison logic exists here.
 *
 * (Operates over the repository's recent window; full-range aggregation is a later refinement.)
 */
class GetProgressOverviewUseCase(
    private val workoutHistoryRepository: WorkoutHistoryRepository
) {
    suspend operator fun invoke(): ProgressOverview {
        val sessions = workoutHistoryRepository.recent()
        return ProgressOverview(
            sessionCount = sessions.size,
            setsCompleted = sessions.sumOf { it.completedSetCount },
            fullSessions = sessions.count { it.fullyCompleted },
            adjustedSessions = sessions.count { !it.fullyCompleted },
            effortEasy = sessions.count { it.perceivedEffort == "easy" },
            effortModerate = sessions.count { it.perceivedEffort == "moderate" },
            effortHard = sessions.count { it.perceivedEffort == "hard" }
        )
    }
}
