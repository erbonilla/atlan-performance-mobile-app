package com.atlan.performance.shared.domain.model

/**
 * A persisted snapshot of an in-progress Wet Mode session — enough to resume at the start of the
 * interrupted set. Calm, never a "you stopped" judgement; resuming or discarding are equal choices.
 */
data class SessionProgress(
    val sessionId: String,
    val setIndex: Int,
    val setCount: Int,
    val completedCount: Int,
    val restSeconds: Int,
    val savedAtIso: String
)
