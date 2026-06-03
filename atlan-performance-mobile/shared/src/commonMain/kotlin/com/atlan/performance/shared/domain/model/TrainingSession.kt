package com.atlan.performance.shared.domain.model

data class TrainingSession(
    val id: String,
    val scheduledAtIso: String,
    val discipline: WorkoutDiscipline,
    val intensity: WorkoutIntensity,
    val title: String,
    val distanceLabel: String,
    val durationEstimateLabel: String,
    val status: SessionStatus,
    /** True when the session can be opened, started, and completed without connectivity. */
    val offlineAvailable: Boolean,
    val sets: List<SessionSet>,
    /** Concept key (e.g. "threshold") whose Why content backs this session, if any. */
    val whyConceptKey: String? = null
)
