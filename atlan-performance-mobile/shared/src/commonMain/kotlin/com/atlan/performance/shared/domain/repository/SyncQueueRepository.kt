package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.data.sync.SyncQueueItem

/**
 * Contract for the offline write queue. The initial fake just records items in memory; a real
 * implementation persists them and a platform worker drains them (TODO). Enqueuing must never block
 * the UI.
 */
interface SyncQueueRepository {
    suspend fun enqueue(item: SyncQueueItem)
    suspend fun pending(): List<SyncQueueItem>
}
