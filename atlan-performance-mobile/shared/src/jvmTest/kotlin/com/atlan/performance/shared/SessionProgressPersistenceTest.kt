package com.atlan.performance.shared

import com.atlan.performance.shared.data.local.SqlDelightSessionProgressRepository
import com.atlan.performance.shared.data.seed.SeedTrainingPlan
import com.atlan.performance.shared.db.DatabaseDriverFactory
import com.atlan.performance.shared.db.createAtlanDatabase
import com.atlan.performance.shared.domain.usecase.ClearSessionProgressUseCase
import com.atlan.performance.shared.domain.usecase.LoadSessionProgressUseCase
import com.atlan.performance.shared.domain.usecase.ResumeWorkoutTimerUseCase
import com.atlan.performance.shared.domain.usecase.SaveSessionProgressUseCase
import com.atlan.performance.shared.domain.usecase.StartWorkoutTimerUseCase
import com.atlan.performance.shared.presentation.SetTimerPhase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Proves active-session progress survives "process death" (a fresh repository on the same DB) and
 * that resuming rebuilds the timer at the interrupted set with the completed count preserved, then
 * clears cleanly. Runs on the JVM target via the in-memory driver.
 */
class SessionProgressPersistenceTest {

    private val session = SeedTrainingPlan.todaySession
    private val start = StartWorkoutTimerUseCase()

    @Test
    fun progress_persists_and_resumes_at_interrupted_set() = runTest {
        val db = createAtlanDatabase(DatabaseDriverFactory())
        val save = SaveSessionProgressUseCase(SqlDelightSessionProgressRepository(db))

        // Simulate completing set 1 (now on set 2 of 4) and saving.
        var timer = start(session, restDurationMs = 45_000L).started(0L)
        timer = timer.completedSet(1_000L) // -> REST, setIndex 2, completedCount 1
        save(timer, restSeconds = 45)

        // A fresh repository on the SAME db (i.e. after relaunch) still sees the snapshot.
        val load = LoadSessionProgressUseCase(SqlDelightSessionProgressRepository(db))
        val progress = load()
        assertEquals("session-today", progress?.sessionId)
        assertEquals(2, progress?.setIndex, "Should resume at the set that was in progress.")
        assertEquals(1, progress?.completedCount)
        assertEquals(45, progress?.restSeconds, "Rest preference is part of the snapshot.")

        // Resume rebuilds a READY timer at set 2, set 1 still counted complete.
        val resumed = ResumeWorkoutTimerUseCase(start)(session, progress!!)
        assertEquals(2, resumed.setIndex)
        assertEquals(1, resumed.completedCount)
        assertEquals(SetTimerPhase.READY, resumed.phase, "Resumed set starts fresh; time doesn't accrue while closed.")
        assertEquals(45_000L, resumed.restDurationMs)

        // Completing the session clears the snapshot.
        val clear = ClearSessionProgressUseCase(SqlDelightSessionProgressRepository(db))
        clear("session-today")
        assertNull(load(), "Finishing/discarding clears the snapshot.")
    }
}
