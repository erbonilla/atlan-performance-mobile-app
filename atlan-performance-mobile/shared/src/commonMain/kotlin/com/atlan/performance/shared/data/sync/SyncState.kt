package com.atlan.performance.shared.data.sync

/** Lifecycle of a queued write. Surfaced calmly ("Sync pending") — never as an alarming error. */
enum class SyncState {
    PENDING,
    IN_FLIGHT,
    SYNCED,
    FAILED_SAVED_LOCALLY
}
