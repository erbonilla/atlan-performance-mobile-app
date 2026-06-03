package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.domain.model.SessionStatus
import com.atlan.performance.shared.domain.model.TrainingSession

interface SessionRepository {
    suspend fun getTodaySession(): TrainingSession?
    suspend fun getSession(id: String): TrainingSession?

    /** Marks a set completed. Writes locally first (offline-safe). */
    suspend fun completeSet(sessionId: String, setId: String): TrainingSession?

    suspend fun updateStatus(sessionId: String, status: SessionStatus): TrainingSession?

    /** Replaces today's session reference after an accepted swap. */
    suspend fun replaceTodaySession(session: TrainingSession)
}
