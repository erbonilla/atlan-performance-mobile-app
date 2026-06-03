package com.atlan.performance.shared.domain.model

/**
 * Science "depth on demand" content. Precise and sourced — no motivational hype, no generic
 * "studies show". Every concept carries a real reference. Tide (not Coral) marks Why affordances.
 */
data class WhyConcept(
    val id: String,
    val conceptKey: String,
    val language: Language,
    val eyebrow: String,
    val title: String,
    val body: String,
    val mechanisms: List<String>,
    val reference: String
)
