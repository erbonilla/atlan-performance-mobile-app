package com.atlan.performance.shared

import com.atlan.performance.shared.data.local.SqlDelightWorkoutHistoryRepository
import com.atlan.performance.shared.db.DatabaseDriverFactory
import com.atlan.performance.shared.db.createAtlanDatabase
import com.atlan.performance.shared.domain.model.CompletedSession
import com.atlan.performance.shared.domain.usecase.GetProgressOverviewUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Progress Overview aggregates history into calm tallies — sessions, sets, full vs adjusted, effort
 * distribution. No streak/deficit fields exist to assert; this checks the sums and the empty case.
 */
class ProgressOverviewTest {

    private fun repo() = SqlDelightWorkoutHistoryRepository(createAtlanDatabase(DatabaseDriverFactory()))

    private fun completed(id: String, sets: Int, total: Int, full: Boolean, effort: String?) =
        CompletedSession(id, "Pool · Threshold", sets, total, "12:00", effort, "2026-05-2${id}T08:00:00", full)

    @Test
    fun overview_tallies_sessions_sets_and_effort() = runTest {
        val repo = repo()
        repo.record(completed("1", sets = 4, total = 4, full = true, effort = "moderate"))
        repo.record(completed("2", sets = 4, total = 4, full = true, effort = "hard"))
        repo.record(completed("3", sets = 2, total = 4, full = false, effort = null))

        val overview = GetProgressOverviewUseCase(repo)()

        assertEquals(3, overview.sessionCount)
        assertEquals(10, overview.setsCompleted, "4 + 4 + 2 sets completed.")
        assertEquals(2, overview.fullSessions)
        assertEquals(1, overview.adjustedSessions, "Ended-early session is 'adjusted', not failed.")
        assertEquals(1, overview.effortModerate)
        assertEquals(1, overview.effortHard)
        assertEquals(0, overview.effortEasy)
        assertEquals(2, overview.effortLogged)
        assertTrue(overview.hasAny)
    }

    @Test
    fun overview_is_empty_with_no_history() = runTest {
        val overview = GetProgressOverviewUseCase(repo())()
        assertEquals(0, overview.sessionCount)
        assertFalse(overview.hasAny)
    }
}
