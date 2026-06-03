package com.atlan.performance.shared.db

import app.cash.sqldelight.db.SqlDriver

/**
 * Platform-provided SQLDelight driver. The shared core owns the schema + queries; each platform
 * supplies the concrete driver (Android needs a Context; iOS/JVM do not). See OFFLINE_FIRST.md.
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

/** Builds the local database from a platform driver. Local storage is the source of truth. */
fun createAtlanDatabase(factory: DatabaseDriverFactory): AtlanDatabase =
    AtlanDatabase(factory.createDriver())
