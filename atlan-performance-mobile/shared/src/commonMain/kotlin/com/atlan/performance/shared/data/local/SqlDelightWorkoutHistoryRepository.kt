package com.atlan.performance.shared.data.local

import com.atlan.performance.shared.db.AtlanDatabase
import com.atlan.performance.shared.domain.model.CompletedSession
import com.atlan.performance.shared.domain.repository.WorkoutHistoryRepository

/**
 * SQLDelight-backed Workout History — finished sessions persist across relaunch. INTEGER columns map
 * to Long; the domain uses Int/Boolean.
 *
 * TODO(io): offload to a background dispatcher once kotlinx-coroutines-core is a shared dependency.
 */
class SqlDelightWorkoutHistoryRepository(database: AtlanDatabase) : WorkoutHistoryRepository {

    private val queries = database.workoutHistoryQueries

    override suspend fun record(session: CompletedSession) {
        queries.record(
            id = session.id,
            title = session.title,
            completedSetCount = session.completedSetCount.toLong(),
            totalSetCount = session.totalSetCount.toLong(),
            totalElapsedLabel = session.totalElapsedLabel,
            perceivedEffort = session.perceivedEffort,
            finishedAtIso = session.finishedAtIso,
            fullyCompleted = if (session.fullyCompleted) 1L else 0L
        )
    }

    override suspend fun recent(): List<CompletedSession> =
        queries.selectRecent(::mapRow).executeAsList()

    private fun mapRow(
        id: String,
        title: String,
        completedSetCount: Long,
        totalSetCount: Long,
        totalElapsedLabel: String,
        perceivedEffort: String?,
        finishedAtIso: String,
        fullyCompleted: Long
    ): CompletedSession = CompletedSession(
        id = id,
        title = title,
        completedSetCount = completedSetCount.toInt(),
        totalSetCount = totalSetCount.toInt(),
        totalElapsedLabel = totalElapsedLabel,
        perceivedEffort = perceivedEffort,
        finishedAtIso = finishedAtIso,
        fullyCompleted = fullyCompleted == 1L
    )
}
