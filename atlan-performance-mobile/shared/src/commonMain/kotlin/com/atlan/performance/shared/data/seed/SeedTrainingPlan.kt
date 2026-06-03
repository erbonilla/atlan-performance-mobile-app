package com.atlan.performance.shared.data.seed

import com.atlan.performance.shared.domain.model.SessionSet
import com.atlan.performance.shared.domain.model.SessionStatus
import com.atlan.performance.shared.domain.model.TrainingPlan
import com.atlan.performance.shared.domain.model.TrainingSession
import com.atlan.performance.shared.domain.model.TrainingWeek
import com.atlan.performance.shared.domain.model.WorkoutDiscipline
import com.atlan.performance.shared.domain.model.WorkoutIntensity

/**
 * Seed plan: a 24-week build, current week 18, with today's Pool · Threshold · 1,000m session.
 * Today's session is offline-available so it can be opened and executed without connectivity.
 * Load status stays calm ("On track") — no deficit or behind-plan framing.
 */
object SeedTrainingPlan {

    val todaySession = TrainingSession(
        id = "session-today",
        scheduledAtIso = "2026-05-26T07:00:00",
        discipline = WorkoutDiscipline.SWIM,
        intensity = WorkoutIntensity.THRESHOLD,
        title = "Pool · Threshold",
        distanceLabel = "1,000m",
        durationEstimateLabel = "~30 min",
        status = SessionStatus.PLANNED,
        offlineAvailable = true,
        whyConceptKey = "threshold",
        sets = listOf(
            SessionSet(
                id = "set-warmup",
                order = 1,
                label = "Warm-up · 400m easy",
                distanceLabel = "400m",
                targetPaceLabel = null,
                completed = false
            ),
            SessionSet(
                id = "set-main-1",
                order = 2,
                label = "Main set · 100m at threshold",
                distanceLabel = "100m",
                targetPaceLabel = "1:35",
                completed = false
            ),
            SessionSet(
                id = "set-main-2",
                order = 3,
                label = "Main set · 100m at threshold",
                distanceLabel = "100m",
                targetPaceLabel = "1:35",
                completed = false
            ),
            SessionSet(
                id = "set-main-3",
                order = 4,
                label = "Main set · 100m at threshold",
                distanceLabel = "100m",
                targetPaceLabel = "1:35",
                completed = false
            ),
            SessionSet(
                id = "set-main-4",
                order = 5,
                label = "Main set · 100m at threshold",
                distanceLabel = "100m",
                targetPaceLabel = "1:35",
                completed = false
            )
        )
    )

    val plan = TrainingPlan(
        id = "plan-seed",
        title = "Endurance build",
        weeks = listOf(
            TrainingWeek(
                index = 18,
                totalWeeks = 24,
                loadStatus = "On track",
                isCurrent = true,
                sessions = listOf(todaySession)
            )
        )
    )

    /** The Vasa-erg threshold-equivalent used by the seed swap proposal. */
    val swapReplacement = TrainingSession(
        id = "session-today-swap",
        scheduledAtIso = "2026-05-26T19:00:00",
        discipline = WorkoutDiscipline.VASA,
        intensity = WorkoutIntensity.THRESHOLD,
        title = "Vasa erg · Threshold equivalent",
        distanceLabel = "45-min",
        durationEstimateLabel = "~45 min",
        status = SessionStatus.SWAPPED,
        offlineAvailable = true,
        whyConceptKey = "threshold",
        sets = todaySession.sets
    )
}
