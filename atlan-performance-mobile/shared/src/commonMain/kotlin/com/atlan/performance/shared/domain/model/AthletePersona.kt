package com.atlan.performance.shared.domain.model

/**
 * Calibration result. Captures coaching posture WITHOUT scoring or judging the user — these are
 * stored as settings, not as a "good athlete / bad athlete" score. Each field maps to a real,
 * later-editable product setting.
 */
data class AthletePersona(
    /** What pulls the user to training (autonomy/motivation framing — not a quality score). */
    val motivation: TrainingMotivation,
    /** How the user prefers the plan to flex when life intervenes. */
    val flexibility: ScheduleFlexibility,
    /** How much science the user wants on the surface. */
    val explanationDensity: ExplanationDensity,
    /** When Atlan may interrupt. */
    val notificationCadence: NotificationCadence
)

enum class TrainingMotivation {
    SPACE_FROM_WORK,
    PURSUIT_OF_BETTER,
    STRUCTURE_FOR_WEEK
}

enum class ScheduleFlexibility {
    FLEX_AROUND_CONSTRAINT,
    PRESERVE_KEY_SESSION,
    SHORTEST_USEFUL_SUBSTITUTE
}
