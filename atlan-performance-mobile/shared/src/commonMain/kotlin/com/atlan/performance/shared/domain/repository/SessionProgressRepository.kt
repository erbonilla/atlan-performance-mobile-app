package com.atlan.performance.shared.domain.repository

import com.atlan.performance.shared.domain.model.SessionProgress

/**
 * Durable store for the active-session snapshot (resume / recovery). At most one active session in
 * this milestone; [loadActive] returns the most recently saved snapshot, or null when none is pending.
 */
interface SessionProgressRepository {
    suspend fun save(progress: SessionProgress)
    suspend fun loadActive(): SessionProgress?
    suspend fun clear(sessionId: String)
}
