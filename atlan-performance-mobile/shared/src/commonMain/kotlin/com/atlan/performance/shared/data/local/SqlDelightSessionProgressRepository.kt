package com.atlan.performance.shared.data.local

import com.atlan.performance.shared.db.AtlanDatabase
import com.atlan.performance.shared.domain.model.SessionProgress
import com.atlan.performance.shared.domain.repository.SessionProgressRepository

/**
 * SQLDelight-backed active-session snapshot — survives process death so a session can be resumed
 * after the app is closed or killed. INTEGER columns map to Long; the domain uses Int.
 *
 * TODO(io): offload to a background dispatcher once kotlinx-coroutines-core is a shared dependency.
 */
class SqlDelightSessionProgressRepository(database: AtlanDatabase) : SessionProgressRepository {

    private val queries = database.sessionProgressQueries

    override suspend fun save(progress: SessionProgress) {
        queries.upsert(
            sessionId = progress.sessionId,
            setIndex = progress.setIndex.toLong(),
            setCount = progress.setCount.toLong(),
            completedCount = progress.completedCount.toLong(),
            restSeconds = progress.restSeconds.toLong(),
            savedAtIso = progress.savedAtIso
        )
    }

    override suspend fun loadActive(): SessionProgress? =
        queries.selectActive(::mapRow).executeAsOneOrNull()

    override suspend fun clear(sessionId: String) {
        queries.deleteBySession(sessionId)
    }

    private fun mapRow(
        sessionId: String,
        setIndex: Long,
        setCount: Long,
        completedCount: Long,
        restSeconds: Long,
        savedAtIso: String
    ): SessionProgress = SessionProgress(
        sessionId = sessionId,
        setIndex = setIndex.toInt(),
        setCount = setCount.toInt(),
        completedCount = completedCount.toInt(),
        restSeconds = restSeconds.toInt(),
        savedAtIso = savedAtIso
    )
}
