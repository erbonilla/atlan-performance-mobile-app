package com.atlan.performance.shared.domain.usecase

import com.atlan.performance.shared.domain.model.OfflineStatus
import com.atlan.performance.shared.domain.repository.SessionRepository
import com.atlan.performance.shared.presentation.SessionDetailState

class GetTodaySessionUseCase(
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(
        offlineStatus: OfflineStatus = OfflineStatus.OFFLINE_USABLE
    ): SessionDetailState? {
        val session = sessionRepository.getTodaySession() ?: return null
        return SessionDetailState(
            session = session,
            offlineStatus = offlineStatus,
            whyConceptKey = session.whyConceptKey
        )
    }
}
