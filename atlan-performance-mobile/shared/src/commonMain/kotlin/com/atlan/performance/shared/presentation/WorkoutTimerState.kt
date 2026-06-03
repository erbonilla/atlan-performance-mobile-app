package com.atlan.performance.shared.presentation

import com.atlan.performance.shared.domain.model.OfflineStatus
import kotlin.math.abs

/** Phase of the active 4-set workout timer (atlan-4-set-timer-mobile-interactions.md §2). */
enum class SetTimerPhase {
    /** Set loaded, countdown not started. */
    READY,
    /** Counting down. */
    ACTIVE,
    /** Temporarily stopped; remaining time preserved. */
    PAUSED,
    /** Countdown reached zero but the set has not been manually completed. */
    OVERTIME,
    /** Rest interval between sets — counts down to the next set, skippable. */
    REST,
    /** Session finished (all sets, or ended early) — show the summary. */
    COMPLETED_SESSION
}

/** Minimal per-set data the timer needs (id for the local-first completion write). */
data class TimerSet(
    val id: String,
    val mainMetric: String,        // e.g. "100m"
    val targetPaceLabel: String?,  // e.g. "1:35"
    val durationMs: Long
)

/**
 * Wet Mode 4-set threshold timer — a pure, monotonic state machine.
 *
 * Remaining time is always DERIVED from a monotonic `nowMs` the platform supplies, never from
 * decrementing UI ticks — drift-free across backgrounding and load (spec §4). Every transition is a
 * pure function returning a new state, so the whole machine is testable in `commonTest`.
 *
 * Flow: READY → ACTIVE → (PAUSED) → OVERTIME → complete → REST → ACTIVE … → COMPLETED_SESSION.
 * Completing a non-final set enters a skippable REST that auto-starts the next set at zero.
 *
 * TODO(offline-first): persist this state locally on every transition and on background, and queue
 * set-/session-completion events for later sync. Today the per-set completion write goes through the
 * fake repos via CompleteWorkoutSetUseCase; real local DB + WorkManager/BackgroundTasks come later.
 */
data class WorkoutTimerState(
    val sessionId: String,
    val sets: List<TimerSet>,
    val setIndex: Int,                  // 1-based; during REST this is the UPCOMING set
    val intensityLabel: String,
    val offlineStatus: OfflineStatus,
    val phase: SetTimerPhase = SetTimerPhase.READY,
    val startedAtMs: Long? = null,      // monotonic start of the current ACTIVE set or REST window
    val accumulatedPausedMs: Long = 0L, // total paused ms accrued this set
    val pauseStartedAtMs: Long? = null, // monotonic ms the current pause began (when PAUSED)
    val restDurationMs: Long = 30_000L, // rest between sets
    val completedCount: Int = 0,        // sets completed so far (for partial summaries)
    val sessionStartedAtMs: Long? = null, // monotonic when the first set started
    val completedAtMs: Long? = null     // monotonic when the session finished
) {
    val setCount: Int get() = sets.size
    val current: TimerSet get() = sets[(setIndex - 1).coerceIn(0, sets.size - 1)]
    val currentSetId: String get() = current.id
    val mainMetric: String get() = current.mainMetric
    val targetPaceLabel: String? get() = current.targetPaceLabel
    val setDurationMs: Long get() = current.durationMs

    val setLabel: String get() = "Set $setIndex of $setCount · $intensityLabel"
    val justCompletedSetNumber: Int get() = (setIndex - 1).coerceAtLeast(1)
    val isLastSet: Boolean get() = setIndex >= setCount
    val isPaused: Boolean get() = phase == SetTimerPhase.PAUSED
    val isResting: Boolean get() = phase == SetTimerPhase.REST
    val isRunningOrOvertime: Boolean
        get() = phase == SetTimerPhase.ACTIVE || phase == SetTimerPhase.OVERTIME
    val isComplete: Boolean get() = phase == SetTimerPhase.COMPLETED_SESSION

    /** Duration of whatever is currently counting down (a set, or the rest window). */
    private fun phaseDurationMs(): Long =
        if (phase == SetTimerPhase.REST) restDurationMs else setDurationMs

    /** Active (unpaused) elapsed ms for the current window at [nowMs]. */
    fun elapsedActiveMs(nowMs: Long): Long {
        val start = startedAtMs ?: return 0L
        val pausedNow = pauseStartedAtMs?.let { (nowMs - it).coerceAtLeast(0L) } ?: 0L
        return (nowMs - start - accumulatedPausedMs - pausedNow).coerceAtLeast(0L)
    }

    /** Remaining ms for the current set or rest window; negative = overtime. */
    fun remainingMs(nowMs: Long): Long {
        if (startedAtMs == null) return phaseDurationMs()
        return phaseDurationMs() - elapsedActiveMs(nowMs)
    }

    /** Formatted timer for display: "M:SS" while counting down, "+M:SS" in overtime. */
    fun timerLabel(nowMs: Long): String = format(remainingMs(nowMs))

    /** Total wall-clock duration of the session, once finished (null until then). */
    fun totalElapsedMs(): Long? {
        val start = sessionStartedAtMs ?: return null
        val end = completedAtMs ?: return null
        return (end - start).coerceAtLeast(0L)
    }

    fun totalElapsedLabel(): String = totalElapsedMs()?.let { format(it) } ?: "—"

    /**
     * True when completing now would be "early" enough to warrant a confirmation (more than
     * [thresholdMs] remaining while running). Near the end, completing is immediate (spec §7).
     */
    fun requiresEarlyCompleteConfirm(nowMs: Long, thresholdMs: Long = 10_000L): Boolean =
        isRunningOrOvertime && remainingMs(nowMs) > thresholdMs

    // --- Pure transitions (return a new state) ---

    /** READY → ACTIVE, anchoring the monotonic start (and the session start on the first set). */
    fun started(nowMs: Long): WorkoutTimerState =
        if (phase == SetTimerPhase.READY)
            copy(phase = SetTimerPhase.ACTIVE, startedAtMs = nowMs,
                 accumulatedPausedMs = 0L, pauseStartedAtMs = null,
                 sessionStartedAtMs = sessionStartedAtMs ?: nowMs)
        else this

    /** ACTIVE/OVERTIME → PAUSED, recording when the pause began. */
    fun paused(nowMs: Long): WorkoutTimerState =
        if (isRunningOrOvertime) copy(phase = SetTimerPhase.PAUSED, pauseStartedAtMs = nowMs) else this

    /** PAUSED → ACTIVE/OVERTIME, folding the pause span into the paused accumulator. */
    fun resumed(nowMs: Long): WorkoutTimerState {
        if (phase != SetTimerPhase.PAUSED) return this
        val pausedSpan = pauseStartedAtMs?.let { (nowMs - it).coerceAtLeast(0L) } ?: 0L
        val resumedState = copy(accumulatedPausedMs = accumulatedPausedMs + pausedSpan,
                                pauseStartedAtMs = null, phase = SetTimerPhase.ACTIVE)
        return if (resumedState.remainingMs(nowMs) <= 0L)
            resumedState.copy(phase = SetTimerPhase.OVERTIME) else resumedState
    }

    /**
     * Per-tick advance: ACTIVE flips to OVERTIME at zero (never auto-completes — spec §3); REST
     * auto-starts the next set at zero (spec §9 Option B).
     */
    fun ticked(nowMs: Long): WorkoutTimerState = when {
        phase == SetTimerPhase.ACTIVE && remainingMs(nowMs) <= 0L ->
            copy(phase = SetTimerPhase.OVERTIME)
        phase == SetTimerPhase.REST && remainingMs(nowMs) <= 0L ->
            beganSet(nowMs)
        else -> this
    }

    /** Completes the current set: rest before the next, or finish on the last set. */
    fun completedSet(nowMs: Long): WorkoutTimerState {
        val completed = completedCount + 1
        return if (isLastSet)
            copy(phase = SetTimerPhase.COMPLETED_SESSION, completedCount = completed,
                 completedAtMs = nowMs, pauseStartedAtMs = null)
        else
            copy(setIndex = setIndex + 1, phase = SetTimerPhase.REST, startedAtMs = nowMs,
                 accumulatedPausedMs = 0L, pauseStartedAtMs = null, completedCount = completed)
    }

    /** REST → ACTIVE immediately (Skip Rest). */
    fun skipRest(nowMs: Long): WorkoutTimerState =
        if (phase == SetTimerPhase.REST) beganSet(nowMs) else this

    /** End the session early — keeps however many sets were completed (partial summary). */
    fun endedSession(nowMs: Long): WorkoutTimerState =
        copy(phase = SetTimerPhase.COMPLETED_SESSION, completedAtMs = nowMs, pauseStartedAtMs = null)

    private fun beganSet(nowMs: Long): WorkoutTimerState =
        copy(phase = SetTimerPhase.ACTIVE, startedAtMs = nowMs,
             accumulatedPausedMs = 0L, pauseStartedAtMs = null)

    companion object {
        /** "M:SS" for remaining ≥ 0 (ceiling); "+M:SS" for overtime (floor of the overage). */
        fun format(remainingMs: Long): String {
            return if (remainingMs >= 0L) {
                val total = (remainingMs + 999L) / 1000L     // ceil so 0:00 shows only at the end
                mmss(total)
            } else {
                val total = abs(remainingMs) / 1000L          // floor for the count-up overage
                "+" + mmss(total)
            }
        }

        private fun mmss(totalSeconds: Long): String {
            val m = totalSeconds / 60
            val s = totalSeconds % 60
            return "$m:${s.toString().padStart(2, '0')}"
        }
    }
}
