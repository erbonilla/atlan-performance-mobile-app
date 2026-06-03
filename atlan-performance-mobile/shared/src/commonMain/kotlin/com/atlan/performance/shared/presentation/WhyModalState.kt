package com.atlan.performance.shared.presentation

import com.atlan.performance.shared.domain.model.WhyConcept

/**
 * Why modal state. `concept == null` with `requested == true` represents the "content missing /
 * unsupported language fallback" case the UI handles calmly (no error styling).
 */
data class WhyModalState(
    val requested: Boolean = false,
    val concept: WhyConcept? = null,
    val cachedOffline: Boolean = false
)
