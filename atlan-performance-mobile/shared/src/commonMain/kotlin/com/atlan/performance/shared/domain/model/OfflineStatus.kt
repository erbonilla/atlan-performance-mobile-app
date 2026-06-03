package com.atlan.performance.shared.domain.model

/**
 * Network/cache posture surfaced to the UI without alarm. There is no "error" framing for being
 * offline — offline is a normal, supported state (see OFFLINE_FIRST.md).
 */
enum class OfflineStatus {
    ONLINE,
    OFFLINE_USABLE,
    OFFLINE_PARTIAL_CACHE,
    SYNC_PENDING,
    SYNC_FAILED_SAVED_LOCALLY
}
