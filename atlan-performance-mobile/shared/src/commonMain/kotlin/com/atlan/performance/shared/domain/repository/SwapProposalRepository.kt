package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.domain.model.SwapProposal

interface SwapProposalRepository {
    suspend fun save(proposal: SwapProposal)
    suspend fun getForSession(originalSessionId: String): SwapProposal?
    suspend fun updateStatus(proposalId: String, status: com.atlan.performance.shared.domain.model.SwapProposalStatus): SwapProposal?
}
