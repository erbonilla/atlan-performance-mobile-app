package com.atlan.performance.shared.data.fake

import com.atlan.performance.shared.domain.model.SwapProposal
import com.atlan.performance.shared.domain.model.SwapProposalStatus
import com.atlan.performance.shared.domain.repository.SwapProposalRepository

class FakeSwapProposalRepository : SwapProposalRepository {

    private val byId = mutableMapOf<String, SwapProposal>()
    private val byOriginalSession = mutableMapOf<String, String>()

    override suspend fun save(proposal: SwapProposal) {
        byId[proposal.id] = proposal
        byOriginalSession[proposal.originalSessionId] = proposal.id
    }

    override suspend fun getForSession(originalSessionId: String): SwapProposal? =
        byOriginalSession[originalSessionId]?.let { byId[it] }

    override suspend fun updateStatus(proposalId: String, status: SwapProposalStatus): SwapProposal? {
        val proposal = byId[proposalId] ?: return null
        val updated = proposal.copy(status = status)
        byId[proposalId] = updated
        return updated
    }
}
