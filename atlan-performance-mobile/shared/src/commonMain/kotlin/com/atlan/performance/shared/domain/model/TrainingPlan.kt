package com.atlan.performance.shared.domain.model

data class TrainingPlan(
    val id: String,
    val title: String,
    val weeks: List<TrainingWeek>
) {
    val currentWeek: TrainingWeek?
        get() = weeks.firstOrNull { it.isCurrent }
}
