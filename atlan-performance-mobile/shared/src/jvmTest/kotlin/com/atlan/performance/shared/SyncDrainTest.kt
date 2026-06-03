package com.atlan.performance.shared

import com.atlan.performance.shared.data.local.SqlDelightSyncQueueRepository
import com.atlan.performance.shared.data.sync.SyncOperation
import com.atlan.performance.shared.data.sync.SyncQueueItem
import com.atlan.performance.shared.db.DatabaseDriverFactory
import com.atlan.performance.shared.db.createAtlanDatabase
import com.atlan.performance.shared.domain.sync.SimulatedSyncUploader
import com.atlan.performance.shared.domain.sync.SyncUploader
import com.atlan.performance.shared.domain.usecase.DrainSyncQueueUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Proves the drain engine empties the persistent queue when the uploader accepts items, and keeps
 * items pending (never lost) when it doesn't — the honest offline behaviour while no backend exists.
 */
class SyncDrainTest {

    private fun repo() = SqlDelightSyncQueueRepository(createAtlanDatabase(DatabaseDriverFactory()))

    private fun item(id: String) =
        SyncQueueItem(id, SyncOperation.COMPLETE_SET, mapOf("setId" to id), "2026-05-26T07:0$id:00")

    @Test
    fun drain_clears_pending_when_uploads_are_accepted() = runTest {
        val repo = repo()
        repo.enqueue(item("1"))
        repo.enqueue(item("2"))

        val result = DrainSyncQueueUseCase(repo, SimulatedSyncUploader())()

        assertEquals(2, result.attempted)
        assertEquals(2, result.synced)
        assertEquals(0, result.remaining)
        assertTrue(result.isFullyDrained)
        assertTrue(repo.pending().isEmpty(), "Accepted items must drop out of the queue.")
    }

    @Test
    fun items_stay_pending_when_upload_is_not_accepted() = runTest {
        val repo = repo()
        repo.enqueue(item("1"))
        val rejectingUploader = object : SyncUploader {
            override suspend fun upload(item: SyncQueueItem) = false
        }

        val result = DrainSyncQueueUseCase(repo, rejectingUploader)()

        assertEquals(0, result.synced)
        assertEquals(1, result.remaining, "Unaccepted items stay safely pending — never dropped.")
        assertEquals(1, repo.pending().size)
    }
}
