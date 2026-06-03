package com.atlan.performance.shared.domain.model

/**
 * An adaptation offered when a session no longer fits the user's day. Neither accepting nor skipping
 * is failure. Copy must avoid "Missed", "behind plan", "streak broken", or red badges, and
 * `weeklyLoadStatus` stays calm ("Still on track.").
 */
data class SwapProposal(
    val id: String,
    val originalSessionId: String,
    val replacementTitle: String,
    val replacementDetail: String,
    val weeklyLoadStatus: String,
    /** Short affirmation, e.g. "Smart call — protect the week." Never shame copy. */
    val affirmation: String,
    val status: SwapProposalStatus
)

enum class SwapProposalStatus {
    PROPOSED,
    ACCEPTED,
    REJECTED,
    EXPIRED
}
