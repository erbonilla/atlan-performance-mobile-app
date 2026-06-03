package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.OfflineStatus
import com.atlan.performance.shared.domain.model.TrainingSession
import com.atlan.performance.shared.presentation.TimerSet
import com.atlan.performance.shared.presentation.WorkoutTimerState

/**
 * Builds the initial [WorkoutTimerState] for Wet Mode from a session's main (paced) sets. Pure and
 * stateless — the platform drives the monotonic clock and applies the state machine's transitions.
 *
 * `setDurationMs` is a per-set default until session metadata carries explicit set durations
 * (SessionSet has no duration field yet — a future model addition). `restDurationMs` is the
 * between-sets rest window, surfaced as a user preference (Settings → "Rest between sets").
 */
class StartWorkoutTimerUseCase {
    operator fun invoke(
        session: TrainingSession,
        startIndex: Int = 1,
        setDurationMs: Long = 105_000L, // 1:45 default per threshold set
        restDurationMs: Long = 30_000L, // 0:30 default; user-configurable
        offlineStatus: OfflineStatus = OfflineStatus.OFFLINE_USABLE
    ): WorkoutTimerState {
        val mainSets = session.sets.filter { it.targetPaceLabel != null }
        val source = if (mainSets.isNotEmpty()) mainSets else session.sets
        val timerSets = source.map {
            TimerSet(
                id = it.id,
                mainMetric = it.distanceLabel,
                targetPaceLabel = it.targetPaceLabel,
                durationMs = setDurationMs
            )
        }
        val safeSets = if (timerSets.isNotEmpty()) timerSets else listOf(
            TimerSet(id = session.id, mainMetric = session.distanceLabel, targetPaceLabel = null, durationMs = setDurationMs)
        )
        return WorkoutTimerState(
            sessionId = session.id,
            sets = safeSets,
            setIndex = startIndex.coerceIn(1, safeSets.size),
            intensityLabel = "Threshold",
            offlineStatus = offlineStatus,
            restDurationMs = restDurationMs
        )
    }
}
