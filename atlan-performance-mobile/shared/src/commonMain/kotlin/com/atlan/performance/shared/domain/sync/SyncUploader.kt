package com.atlan.performance.shared.domain.sync

import com.atlan.performance.shared.data.sync.SyncQueueItem

/**
 * Seam between the local sync queue and a backend. The drain engine hands each pending item to an
 * uploader; a `true` result means the write was accepted remotely and can be marked synced.
 *
 * This milestone has **no backend**, so the only implementation is [SimulatedSyncUploader]. Swapping
 * in a real HTTP uploader here turns the queue into a live sync engine without touching the drain
 * logic, the workers, or the UI.
 */
interface SyncUploader {
    suspend fun upload(item: SyncQueueItem): Boolean
}

/**
 * No-backend stand-in: pretends a backend accepted the write so the queue can be exercised and the
 * workers verified end-to-end. NOT a real upload — see [SyncUploader]. TODO(remote): replace.
 */
class SimulatedSyncUploader : SyncUploader {
    override suspend fun upload(item: SyncQueueItem): Boolean = true
}
