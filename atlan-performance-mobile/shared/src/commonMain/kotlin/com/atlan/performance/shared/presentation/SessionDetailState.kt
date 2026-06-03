package com.atlan.performance.shared.presentation

import com.atlan.performance.shared.domain.model.OfflineStatus
import com.atlan.performance.shared.domain.model.TrainingSession

data class SessionDetailState(
    val session: TrainingSession,
    val offlineStatus: OfflineStatus,
    val whyConceptKey: String?
)
