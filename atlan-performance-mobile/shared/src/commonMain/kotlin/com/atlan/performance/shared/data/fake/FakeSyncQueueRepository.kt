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

    override suspend fun pending(): List<SyncQueueItem> = items.toList()
}
