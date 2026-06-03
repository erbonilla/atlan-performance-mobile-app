package com.atlan.performance.shared.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

/** iOS driver — SQLite via a persistent on-device file. The schema is created on first open. */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(AtlanDatabase.Schema, "atlan.db")
}
