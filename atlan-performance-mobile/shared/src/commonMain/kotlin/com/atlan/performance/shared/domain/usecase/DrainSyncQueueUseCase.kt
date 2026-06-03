package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.repository.SyncQueueRepository
import com.atlan.performance.shared.domain.sync.SyncUploader

/** Outcome of a drain pass — surfaced calmly; `remaining > 0` is normal offline behaviour, not an error. */
data class SyncDrainResult(
    val attempted: Int,
    val synced: Int,
    val remaining: Int
) {
    val isFullyDrained: Boolean get() = remaining == 0
}

/**
 * Drains the local sync queue: hands each pending item to the [SyncUploader] and marks the accepted
 * ones synced. Pure orchestration — the platform decides *when* to run it (a WorkManager job on
 * Android, a BGTask / foreground refresh on iOS). Local data is never lost: items that aren't accepted
 * simply stay pending for the next pass.
 */
class DrainSyncQueueUseCase(
    private val syncQueueRepository: SyncQueueRepository,
    private val uploader: SyncUploader
) {
    suspend operator fun invoke(): SyncDrainResult {
        val pending = syncQueueRepository.pending()
        var synced = 0
        for (item in pending) {
            if (uploader.upload(item)) {
                syncQueueRepository.markSynced(item.id)
                synced++
            }
        }
        return SyncDrainResult(
            attempted = pending.size,
            synced = synced,
            remaining = syncQueueRepository.pending().size
        )
    }
}
