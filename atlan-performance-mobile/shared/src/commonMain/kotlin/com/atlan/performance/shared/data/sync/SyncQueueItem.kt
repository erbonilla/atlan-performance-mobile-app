package com.atlan.performance.shared.data.sync

/**
 * A single queued offline write. Workout actions are written locally first, then enqueued here for a
 * platform background worker to drain later (WorkManager / BackgroundTasks — TODO, not wired yet).
 *
 * @param payload opaque key/value describing the change (e.g. sessionId, setId). Kept simple for the
 *   initial setup; a typed payload + serialization arrive with real persistence.
 */
data class SyncQueueItem(
    val id: String,
    val operation: SyncOperation,
    val payload: Map<String, String>,
    val createdAtIso: String,
    val state: SyncState = SyncState.PENDING
)
