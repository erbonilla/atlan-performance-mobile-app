package com.atlan.performance.shared

import com.atlan.performance.shared.domain.model.OfflineStatus
import com.atlan.performance.shared.presentation.SetTimerPhase
import com.atlan.performance.shared.presentation.TimerSet
import com.atlan.performance.shared.presentation.WorkoutTimerState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * The Wet Mode timer is a pure, monotonic state machine: callers inject `nowMs`, so its math is
 * fully testable without a real clock. Covers countdown, drift-free pause/resume accounting,
 * overtime (no auto-complete), early-complete confirmation, set advance, and session completion.
 */
class WorkoutTimerTest {

    private fun state() = WorkoutTimerState(
        sessionId = "s1",
        sets = listOf(
            TimerSet("set-1", "100m", "1:35", 100_000L),
            TimerSet("set-2", "100m", "1:35", 100_000L),
            TimerSet("set-3", "100m", "1:35", 100_000L),
            TimerSet("set-4", "100m", "1:35", 100_000L)
        ),
        setIndex = 1,
        intensityLabel = "Threshold",
        offlineStatus = OfflineStatus.OFFLINE_USABLE
    )

    @Test
    fun countsDownFromMonotonicElapsed_noUiTicks() {
        val s = state().started(1_000L)
        assertEquals(SetTimerPhase.ACTIVE, s.phase)
        // 8s later → 92s remaining → "1:32" (matches the reference screen).
        assertEquals(92_000L, s.remainingMs(9_000L))
        assertEquals("1:32", s.timerLabel(9_000L))
    }

    @Test
    fun pauseAndResumeExcludePausedTimeFromCountdown() {
        var s = state().started(0L)
        // run 10s, pause for 30s, resume — only the 10s active should have counted.
        s = s.paused(10_000L)
        assertEquals(SetTimerPhase.PAUSED, s.phase)
        assertEquals(90_000L, s.remainingMs(40_000L)) // still 90s remaining while paused
        s = s.resumed(40_000L)
        assertEquals(SetTimerPhase.ACTIVE, s.phase)
        // 5s of active time after resume → 85s remaining.
        assertEquals(85_000L, s.remainingMs(45_000L))
    }

    @Test
    fun reachesOvertimeWithoutAutoCompleting() {
        var s = state().started(0L)
        s = s.ticked(101_000L) // 1s past the 100s set
        assertEquals(SetTimerPhase.OVERTIME, s.phase)
        assertTrue(s.remainingMs(104_000L) < 0)
        assertEquals("+0:04", s.timerLabel(104_000L))
        // Overtime never advances the set on its own.
        assertEquals(1, s.setIndex)
    }

    @Test
    fun earlyCompleteConfirmOnlyWhenWellBeforeZero() {
        val s = state().started(0L)
        assertTrue(s.requiresEarlyCompleteConfirm(20_000L))   // 80s remaining → confirm
        assertFalse(s.requiresEarlyCompleteConfirm(95_000L))  // 5s remaining → immediate
    }

    @Test
    fun completingAdvancesThroughRestThenFinishes() {
        var s = state().started(0L)
        assertEquals("set-1", s.currentSetId)
        s = s.completedSet(10_000L)                       // set 1 done → REST, upcoming set 2
        assertEquals(SetTimerPhase.REST, s.phase)
        assertEquals(2, s.setIndex)
        assertEquals(1, s.completedCount)
        s = s.skipRest(11_000L)                            // → ACTIVE set 2
        assertEquals(SetTimerPhase.ACTIVE, s.phase)
        s = s.completedSet(20_000L).skipRest(21_000L)      // set 2 → rest → set 3
        assertEquals(3, s.setIndex)
        s = s.completedSet(30_000L).skipRest(31_000L)      // set 3 → rest → set 4
        assertEquals(4, s.setIndex)
        assertTrue(s.isLastSet)
        s = s.completedSet(40_000L)                        // last set → COMPLETED_SESSION
        assertEquals(SetTimerPhase.COMPLETED_SESSION, s.phase)
        assertEquals(4, s.completedCount)
        assertTrue(s.isComplete)
    }

    @Test
    fun restAutoStartsNextSetAtZero() {
        val s = state().started(0L).completedSet(5_000L)   // REST window starts at 5_000 (30s default)
        assertEquals(SetTimerPhase.REST, s.phase)
        assertEquals(SetTimerPhase.REST, s.ticked(20_000L).phase) // 15s in — still resting
        val next = s.ticked(36_000L)                       // past 35_000 → auto-start set 2
        assertEquals(SetTimerPhase.ACTIVE, next.phase)
        assertEquals(2, next.setIndex)
    }

    @Test
    fun endSessionEarlyKeepsCompletedCountForPartialSummary() {
        var s = state().started(0L)
        s = s.completedSet(10_000L).skipRest(11_000L)      // set 1 done; now on set 2
        s = s.endedSession(20_000L)                        // bail mid set-2
        assertEquals(SetTimerPhase.COMPLETED_SESSION, s.phase)
        assertEquals(1, s.completedCount)                  // only set 1 counted
    }

    @Test
    fun totalElapsedSpansFirstStartToCompletion() {
        var s = state().started(1_000L)
        s = s.completedSet(2_000L).skipRest(2_500L)
            .completedSet(3_000L).skipRest(3_500L)
            .completedSet(4_000L).skipRest(4_500L)
            .completedSet(5_000L)                          // finish set 4
        assertTrue(s.isComplete)
        assertEquals(4_000L, s.totalElapsedMs())           // 5_000 − 1_000
    }

    @Test
    fun formatHandlesBoundaries() {
        assertEquals("0:00", WorkoutTimerState.format(0L))
        assertEquals("0:08", WorkoutTimerState.format(8_000L))
        assertEquals("1:32", WorkoutTimerState.format(92_000L))
        assertEquals("+0:04", WorkoutTimerState.format(-4_000L))
    }
}
