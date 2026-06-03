package com.atlan.performance.shared.domain.model

/**
 * A finished session as it appears in Workout History. A calm record — set counts, elapsed time, and
 * an optional perceived-effort reflection. Partial (ended-early) sessions are valid, not failures.
 */
data class CompletedSession(
    val id: String,
    val title: String,
    val completedSetCount: Int,
    val totalSetCount: Int,
    val totalElapsedLabel: String,
    val perceivedEffort: String?,
    val finishedAtIso: String,
    val fullyCompleted: Boolean
)
