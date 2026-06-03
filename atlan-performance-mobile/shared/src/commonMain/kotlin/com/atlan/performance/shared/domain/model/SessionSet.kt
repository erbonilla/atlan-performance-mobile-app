package com.atlan.performance.shared.domain.model

data class SessionSet(
    val id: String,
    val order: Int,
    val label: String,
    val distanceLabel: String,
    val targetPaceLabel: String?,
    val completed: Boolean
)
