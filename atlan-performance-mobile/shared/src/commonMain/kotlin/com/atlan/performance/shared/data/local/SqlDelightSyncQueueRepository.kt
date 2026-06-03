package com.atlan.performance.shared.data.local

import com.atlan.performance.shared.data.sync.SyncOperation
import com.atlan.performance.shared.data.sync.SyncQueueItem
import com.atlan.performance.shared.data.sync.SyncState
import com.atlan.performance.shared.db.AtlanDatabase
import com.atlan.performance.shared.db.ioDispatcher
import com.atlan.performance.shared.domain.repository.SyncQueueRepository
import kotlinx.coroutines.withContext

/**
 * Local-first sync queue backed by SQLDelight — rows persist across process death (unlike the
 * in-memory fake). A platform worker (WorkManager / BackgroundTasks) will drain it once a backend
 * exists; for now [markSynced] is the drain primitive and there is no network.
 *
 * TODO(io): these calls run on the caller's thread. Offload to a background dispatcher once
 * kotlinx-coroutines-core is a shared dependency.
 */
class SqlDelightSyncQueueRepository(database: AtlanDatabase) : SyncQueueRepository {

    private val queries = database.syncQueueQueries

    override suspend fun enqueue(item: SyncQueueItem) = withContext(ioDispatcher) {
        queries.enqueue(
            id = item.id,
            operation = item.operation.name,
            payload = encodePayload(item.payload),
            createdAtIso = item.createdAtIso,
            state = item.state.name
        )
    }

    override suspend fun pending(): List<SyncQueueItem> = withContext(ioDispatcher) {
        queries.selectPending(::mapRow).executeAsList()
    }

    override suspend fun markSynced(id: String): Unit = withContext(ioDispatcher) {
        queries.updateState(state = SyncState.SYNCED.name, id = id)
    }

    private fun mapRow(
        id: String,
        operation: String,
        payload: String,
        createdAtIso: String,
        state: String
    ): SyncQueueItem = SyncQueueItem(
        id = id,
        operation = SyncOperation.valueOf(operation),
        payload = decodePayload(payload),
        createdAtIso = createdAtIso,
        state = SyncState.valueOf(state)
    )

    private companion object {
        // Record / field separators — ASCII control chars (RS=30, US=31) that never appear in our
        // ids or payload values. Built via Char(code) so no literal control char lives in source.
        private val RECORD: Char = Char(30)
        private val FIELD: Char = Char(31)

        fun encodePayload(map: Map<String, String>): String =
            map.entries.joinToString(RECORD.toString()) { "${it.key}$FIELD${it.value}" }

        fun decodePayload(encoded: String): Map<String, String> {
            if (encoded.isEmpty()) return emptyMap()
            return encoded.split(RECORD).associate { entry ->
                val parts = entry.split(FIELD, limit = 2)
                parts[0] to (parts.getOrNull(1) ?: "")
            }
        }
    }
}
