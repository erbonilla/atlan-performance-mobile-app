package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.data.seed.SeedTrainingPlan
import com.atlan.performance.shared.data.sync.SyncOperation
import com.atlan.performance.shared.data.sync.SyncQueueItem
import com.atlan.performance.shared.domain.model.SessionStatus
import com.atlan.performance.shared.domain.model.SwapProposalStatus
import com.atlan.performance.shared.domain.repository.SessionRepository
import com.atlan.performance.shared.domain.repository.SwapProposalRepository
import com.atlan.performance.shared.domain.repository.SyncQueueRepository
import kotlin.random.Random

/**
 * Applies an accepted swap to local state (source of truth), then queues a sync write. Updates the
 * dashboard's today-session locally. Skipping is handled by [SkipResult] without any failure state.
 */
class AcceptSessionSwapUseCase(
    private val swapProposalRepository: SwapProposalRepository,
    private val sessionRepository: SessionRepository,
    private val syncQueueRepository: SyncQueueRepository
) {
    /** Accept the proposal: mark old session SWAPPED, install the replacement as today's session. */
    suspend fun accept(proposalId: String, originalSessionId: String) {
        swapProposalRepository.updateStatus(proposalId, SwapProposalStatus.ACCEPTED)
        sessionRepository.updateStatus(originalSessionId, SessionStatus.SWAPPED)
        sessionRepository.replaceTodaySession(SeedTrainingPlan.swapReplacement)
        syncQueueRepository.enqueue(
            SyncQueueItem(
                id = "sync-${Random.nextInt(100_000, 999_999)}",
                operation = SyncOperation.ACCEPT_SWAP,
                payload = mapOf("proposalId" to proposalId, "sessionId" to originalSessionId),
                createdAtIso = "" // TODO: wire platform time when persistence lands.
            )
        )
    }

    /** Skip today without any shame/failure state. Plan updates locally; sync is queued. */
    suspend fun skipToday(sessionId: String) {
        sessionRepository.updateStatus(sessionId, SessionStatus.SKIPPED)
        syncQueueRepository.enqueue(
            SyncQueueItem(
                id = "sync-${Random.nextInt(100_000, 999_999)}",
                operation = SyncOperation.SKIP_SESSION,
                payload = mapOf("sessionId" to sessionId),
                createdAtIso = ""
            )
        )
    }
}
