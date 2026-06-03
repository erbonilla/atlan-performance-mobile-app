package com.atlan.performance.shared.domain.model

/**
 * A calm, qualitative summary of training over recent sessions — for the Progress Overview.
 *
 * Deliberately NOT a streak, rank, or "behind plan" surface (principle #1 + the hard no-streak rule):
 * there is no consecutive-day counter, no goal-deficit, no red. `adjustedSessions` (ended-early /
 * partial) are framed as valid adjustments, never failures. Counts are calm tallies, not pressure.
 */
data class ProgressOverview(
    val sessionCount: Int,
    val setsCompleted: Int,
    val fullSessions: Int,
    val adjustedSessions: Int,
    val effortEasy: Int,
    val effortModerate: Int,
    val effortHard: Int
) {
    val hasAny: Boolean get() = sessionCount > 0
    val effortLogged: Int get() = effortEasy + effortModerate + effortHard
}
