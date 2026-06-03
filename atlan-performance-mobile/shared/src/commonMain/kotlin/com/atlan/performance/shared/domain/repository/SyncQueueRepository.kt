package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.data.sync.SyncQueueItem

/**
 * Contract for the offline write queue. The initial fake just records items in memory; a real
 * implementation persists them and a platform worker drains them (TODO). Enqueuing must never block
 * the UI.
 */
interface SyncQueueRepository {
    suspend fun enqueue(item: SyncQueueItem)

    /** Items not yet synced (state != SYNCED), oldest first. */
    suspend fun pending(): List<SyncQueueItem>

    /** Drain primitive: mark an item synced so it drops out of [pending]. Real drain wiring is TODO. */
    suspend fun markSynced(id: String)
}
