package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.SessionProgress
import com.atlan.performance.shared.domain.model.TrainingSession
import com.atlan.performance.shared.presentation.WorkoutTimerState

/**
 * Rebuilds a Wet Mode timer from a saved [SessionProgress], at the start of the interrupted set with
 * the completed-set count preserved. Pure: the platform calls `.started(nowMs)` to begin, exactly as
 * for a fresh start — so resuming re-anchors to the live monotonic clock (no time accrues while the
 * app was closed, which is the correct behaviour for interval training).
 */
class ResumeWorkoutTimerUseCase(
    private val startWorkoutTimer: StartWorkoutTimerUseCase
) {
    operator fun invoke(
        session: TrainingSession,
        progress: SessionProgress,
        setDurationMs: Long = 105_000L
    ): WorkoutTimerState =
        startWorkoutTimer(
            session = session,
            startIndex = progress.setIndex,
            setDurationMs = setDurationMs,
            restDurationMs = progress.restSeconds * 1000L
        ).copy(completedCount = progress.completedCount)
}
