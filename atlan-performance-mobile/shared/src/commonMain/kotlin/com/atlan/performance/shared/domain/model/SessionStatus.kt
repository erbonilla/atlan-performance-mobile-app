package com.atlan.performance.shared.domain.model

/**
 * Session lifecycle. SWAPPED and SKIPPED are never failure states — they are calm, valid outcomes.
 * There is deliberately no "MISSED" status: disruption is absorbed, not marked red.
 */
enum class SessionStatus {
    PLANNED,
    SWAPPED,
    COMPLETED,
    SKIPPED,
    PAUSED
}
