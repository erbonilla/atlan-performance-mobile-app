package com.atlan.performance.shared

import com.atlan.performance.shared.data.local.SqlDelightWorkoutHistoryRepository
import com.atlan.performance.shared.data.seed.SeedTrainingPlan
import com.atlan.performance.shared.db.DatabaseDriverFactory
import com.atlan.performance.shared.db.createAtlanDatabase
import com.atlan.performance.shared.domain.usecase.GetWorkoutHistoryUseCase
import com.atlan.performance.shared.domain.usecase.RecordCompletedSessionUseCase
import com.atlan.performance.shared.domain.usecase.StartWorkoutTimerUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Proves finished sessions persist to Workout History (readable from a fresh repository on the same
 * DB), are returned newest-first, and that partial (ended-early) sessions are recorded as valid
 * outcomes — never dropped, never flagged as failures.
 */
class WorkoutHistoryPersistenceTest {

    private val session = SeedTrainingPlan.todaySession
    private val start = StartWorkoutTimerUseCase()

    @Test
    fun completed_sessions_persist_newest_first_including_partials() = runTest {
        val db = createAtlanDatabase(DatabaseDriverFactory())
        val record = RecordCompletedSessionUseCase(SqlDelightWorkoutHistoryRepository(db))

        // A full session.
        var full = start(session).started(0L)
        repeat(full.setCount) { full = full.completedSet(1_000L * (it + 1)) }
        record(full, title = "Pool · Threshold", perceivedEffort = "moderate", finishedAtIso = "2026-05-26T08:00:00")

        // A partial session ended after 1 set.
        var partial = start(session).started(0L)
        partial = partial.completedSet(1_000L).endedSession(2_000L)
        record(partial, title = "Pool · Threshold", perceivedEffort = null, finishedAtIso = "2026-05-28T08:00:00")

        // A fresh repository on the same DB still sees both, newest first.
        val history = GetWorkoutHistoryUseCase(SqlDelightWorkoutHistoryRepository(db))()
        assertEquals(2, history.size, "Both sessions must persist.")
        assertEquals("2026-05-28T08:00:00", history[0].finishedAtIso, "Newest first.")

        val partialRow = history[0]
        assertFalse(partialRow.fullyCompleted, "Ended-early session is partial, not full.")
        assertEquals(1, partialRow.completedSetCount)
        assertEquals(4, partialRow.totalSetCount)

        val fullRow = history[1]
        assertTrue(fullRow.fullyCompleted)
        assertEquals("moderate", fullRow.perceivedEffort, "Optional reflection round-trips.")
    }
}
