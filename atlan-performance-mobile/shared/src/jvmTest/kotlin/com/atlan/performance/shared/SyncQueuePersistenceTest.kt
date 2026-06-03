package com.atlan.performance.shared

import com.atlan.performance.shared.data.local.SqlDelightSyncQueueRepository
import com.atlan.performance.shared.data.sync.SyncOperation
import com.atlan.performance.shared.data.sync.SyncQueueItem
import com.atlan.performance.shared.data.sync.SyncState
import com.atlan.performance.shared.db.DatabaseDriverFactory
import com.atlan.performance.shared.db.createAtlanDatabase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Proves the SQLDelight-backed sync queue is real local persistence, not an in-memory list:
 * an item enqueued through one repository instance is visible from a *second* repository created
 * on the same database, survives a payload round-trip, and drops out of `pending()` once synced.
 *
 * Runs on the JVM target via the in-memory JDBC driver (`DatabaseDriverFactory()` jvm actual).
 */
class SyncQueuePersistenceTest {

    private fun newDatabase() = createAtlanDatabase(DatabaseDriverFactory())

    @Test
    fun item_persists_in_db_across_repository_instances() = runTest {
        val db = newDatabase()
        val writer = SqlDelightSyncQueueRepository(db)
        writer.enqueue(
            SyncQueueItem(
                id = "q1",
                operation = SyncOperation.COMPLETE_SET,
                payload = mapOf("sessionId" to "session-today", "setId" to "set-main-1"),
                createdAtIso = "2026-05-26T07:05:00",
                state = SyncState.PENDING
            )
        )

        // A brand-new repository on the SAME database sees the row — it lives in storage, not in a field.
        val reader = SqlDelightSyncQueueRepository(db)
        val pending = reader.pending()

        assertEquals(1, pending.size, "Enqueued item must be readable from a fresh repository instance.")
        val item = pending.first()
        assertEquals("q1", item.id)
        assertEquals(SyncOperation.COMPLETE_SET, item.operation)
        assertEquals("session-today", item.payload["sessionId"], "Payload must round-trip through the DB.")
        assertEquals("set-main-1", item.payload["setId"])
    }

    @Test
    fun synced_items_drop_out_of_pending() = runTest {
        val repo = SqlDelightSyncQueueRepository(newDatabase())
        repo.enqueue(SyncQueueItem("q1", SyncOperation.COMPLETE_SESSION, emptyMap(), "2026-05-26T07:30:00"))
        repo.enqueue(SyncQueueItem("q2", SyncOperation.SAVE_POST_SESSION, mapOf("effort" to "moderate"), "2026-05-26T07:31:00"))
        assertEquals(2, repo.pending().size)

        repo.markSynced("q1")

        val pending = repo.pending()
        assertEquals(1, pending.size, "A synced item must drop out of pending.")
        assertEquals("q2", pending.first().id)
        assertTrue(pending.none { it.state == SyncState.SYNCED })
    }
}
