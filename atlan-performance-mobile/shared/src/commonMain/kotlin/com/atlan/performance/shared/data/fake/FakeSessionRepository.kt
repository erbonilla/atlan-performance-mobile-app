package com.atlan.performance.shared.data.fake

import com.atlan.performance.shared.data.seed.SeedTrainingPlan
import com.atlan.performance.shared.domain.model.SessionStatus
import com.atlan.performance.shared.domain.model.TrainingSession
import com.atlan.performance.shared.domain.repository.SessionRepository
import com.atlan.performance.shared.domain.repository.TrainingPlanRepository

/**
 * In-memory session store. Completion and status writes are applied to local state immediately
 * (offline-first); the use case is responsible for enqueuing sync afterwards.
 */
class FakeSessionRepository(
    private val planRepository: TrainingPlanRepository,
    seedToday: TrainingSession = SeedTrainingPlan.todaySession
) : SessionRepository {

    private val sessions = mutableMapOf(seedToday.id to seedToday)
    private var todaySessionId: String = seedToday.id

    override suspend fun getTodaySession(): TrainingSession? = sessions[todaySessionId]

    override suspend fun getSession(id: String): TrainingSession? = sessions[id]

    override suspend fun completeSet(sessionId: String, setId: String): TrainingSession? {
        val session = sessions[sessionId] ?: return null
        val updated = session.copy(
            sets = session.sets.map { if (it.id == setId) it.copy(completed = true) else it }
        )
        sessions[sessionId] = updated
        return updated
    }

    override suspend fun updateStatus(sessionId: String, status: SessionStatus): TrainingSession? {
        val session = sessions[sessionId] ?: return null
        val updated = session.copy(status = status)
        sessions[sessionId] = updated
        return updated
    }

    override suspend fun replaceTodaySession(session: TrainingSession) {
        sessions[session.id] = session
        todaySessionId = session.id
    }
}
