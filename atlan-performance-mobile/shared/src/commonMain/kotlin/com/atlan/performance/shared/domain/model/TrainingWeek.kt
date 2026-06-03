package com.atlan.performance.shared.domain.model

/**
 * A week of the plan. `loadStatus` is intentionally a calm string ("On track") — never a deficit,
 * percentage-behind, or red failure indicator.
 */
data class TrainingWeek(
    val index: Int,
    val totalWeeks: Int,
    val loadStatus: String,
    val sessions: List<TrainingSession>,
    /** Marks the current week for the weekly-arc Coral marker. */
    val isCurrent: Boolean
)
