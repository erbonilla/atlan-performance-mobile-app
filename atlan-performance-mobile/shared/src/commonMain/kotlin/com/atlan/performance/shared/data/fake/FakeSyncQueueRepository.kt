package com.atlan.performance.shared.data.fake

import com.atlan.performance.shared.data.sync.SyncQueueItem
import com.atlan.performance.shared.domain.repository.SyncQueueRepository

/**
 * In-memory sync queue. Records pending writes only; no backend drains it yet. A platform worker
 * (WorkManager / BackgroundTasks) will drain a persistent implementation later — TODO.
 */
class FakeSyncQueueRepository : SyncQueueRepository {

    private val items = mutableListOf<SyncQueueItem>()

    override suspend fun enqueue(item: SyncQueueItem) {
        items += item
    }

    override suspend fun pending(): List<SyncQueueItem> =
        items.filter { it.state != com.atlan.performance.shared.data.sync.SyncState.SYNCED }

    override suspend fun markSynced(id: String) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) items[idx] = items[idx].copy(state = com.atlan.performance.shared.data.sync.SyncState.SYNCED)
    }
}
