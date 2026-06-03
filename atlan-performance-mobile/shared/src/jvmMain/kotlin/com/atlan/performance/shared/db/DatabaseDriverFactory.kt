package com.atlan.performance.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

/**
 * JVM driver — an in-memory SQLite database. Used by the shared test suite (and any desktop/CLI
 * harness) so persistence logic is verifiable with only a JDK. The schema is created eagerly.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { AtlanDatabase.Schema.create(it) }
}
