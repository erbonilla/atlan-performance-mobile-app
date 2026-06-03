package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.data.seed.SeedTrainingPlan
import com.atlan.performance.shared.domain.model.SwapProposal
import com.atlan.performance.shared.domain.model.SwapProposalStatus
import com.atlan.performance.shared.domain.repository.SessionRepository
import com.atlan.performance.shared.domain.repository.SwapProposalRepository
import com.atlan.performance.shared.presentation.SessionSwapperState
import kotlin.random.Random

/**
 * Proposes an adaptation when a session no longer fits the day. The proposal is never framed as
 * failure: weekly load stays "Still on track." and copy is supportive. No "missed", "behind", or
 * "streak broken" language is ever produced here.
 */
class ProposeSessionSwapUseCase(
    private val sessionRepository: SessionRepository,
    private val swapProposalRepository: SwapProposalRepository
) {
    suspend operator fun invoke(originalSessionId: String): SessionSwapperState? {
        val original = sessionRepository.getSession(originalSessionId) ?: return null
        val replacement = SeedTrainingPlan.swapReplacement

        val proposal = SwapProposal(
            id = "swap-${Random.nextInt(100_000, 999_999)}",
            originalSessionId = original.id,
            replacementTitle = "45-min Vasa erg",
            replacementDetail = "threshold equivalent",
            weeklyLoadStatus = "Still on track.",
            affirmation = "Smart call — protect the week.",
            status = SwapProposalStatus.PROPOSED
        )
        swapProposalRepository.save(proposal)

        return SessionSwapperState(
            tag = "Session Adjusted",
            empathyLine = "Life happens.",
            originalLabel = "90-min pool · threshold",
            proposal = proposal
        )
    }
}
