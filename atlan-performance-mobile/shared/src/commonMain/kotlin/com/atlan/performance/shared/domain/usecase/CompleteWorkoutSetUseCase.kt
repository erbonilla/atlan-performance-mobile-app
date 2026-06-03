package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.data.sync.SyncOperation
import com.atlan.performance.shared.data.sync.SyncQueueItem
import com.atlan.performance.shared.domain.model.TrainingSession
import com.atlan.performance.shared.domain.repository.SessionRepository
import com.atlan.performance.shared.domain.repository.SyncQueueRepository
import kotlin.random.Random

/**
 * Completes a set. Writes locally FIRST (never blocked by network), then enqueues a sync item. This
 * is the offline-first guarantee that makes Wet Mode reliable poolside.
 */
class CompleteWorkoutSetUseCase(
    private val sessionRepository: SessionRepository,
    private val syncQueueRepository: SyncQueueRepository
) {
    suspend operator fun invoke(sessionId: String, setId: String): TrainingSession? {
        val updated = sessionRepository.completeSet(sessionId, setId) ?: return null
        syncQueueRepository.enqueue(
            SyncQueueItem(
                id = "sync-${Random.nextInt(100_000, 999_999)}",
                operation = SyncOperation.COMPLETE_SET,
                payload = mapOf("sessionId" to sessionId, "setId" to setId),
                createdAtIso = "" // TODO: wire platform time when persistence lands.
            )
        )
        return updated
    }
}
