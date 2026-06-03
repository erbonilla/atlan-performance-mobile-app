package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.domain.model.CompletedSession

/** Durable store of finished sessions for Workout History. */
interface WorkoutHistoryRepository {
    suspend fun record(session: CompletedSession)
    suspend fun recent(): List<CompletedSession>
}
