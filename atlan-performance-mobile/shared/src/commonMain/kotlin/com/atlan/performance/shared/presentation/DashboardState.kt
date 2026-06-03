package com.atlan.performance.shared.presentation

import com.atlan.performance.shared.domain.model.OfflineStatus
import com.atlan.performance.shared.domain.model.TrainingSession

/**
 * UI-facing dashboard state. By construction it answers only the two dashboard questions:
 * "What should I do today?" (todaySession) and "Is the week still on track?" (weeklyArc).
 *
 * There are intentionally NO streak, leaderboard, peer-rank, or missed-session fields. Tests assert
 * their absence.
 */
data class DashboardState(
    val dateLabel: String,
    val todaySession: TrainingSession?,
    val weeklyArc: WeeklyArcState,
    val metricChips: List<MetricChip>,
    val offlineStatus: OfflineStatus,
    /** Concept key powering the Today card's Why affordance, if any. */
    val todayWhyConceptKey: String?
)

/** Calm weekly-arc summary. `currentWeekMarker` drives the single Coral marker. */
data class WeeklyArcState(
    val label: String,
    val status: String,
    val currentWeekIndex: Int,
    val totalWeeks: Int,
    /** Normalized 0..1 points for the calm line/area placeholder chart. */
    val points: List<Float>,
    val currentWeekMarker: Int
)

/** A dashboard metric chip with an inline Why affordance. Never a streak/rank/badge. */
data class MetricChip(
    val key: String,
    val title: String,
    val value: String,
    val detail: String,
    val whyConceptKey: String?
)
