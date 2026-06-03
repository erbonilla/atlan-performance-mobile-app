package com.atlan.performance.shared.presentation

import com.atlan.performance.shared.domain.model.SwapProposal

/**
 * Session Swapper state. Both actions are valid, neither is failure. The UI must not render red
 * warning states; `tag` and `affirmation` carry calm, supportive copy.
 */
data class SessionSwapperState(
    val tag: String,
    val empathyLine: String,
    val originalLabel: String,
    val proposal: SwapProposal
)
