package com.atlan.performance.shared.db

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

/** Android driver — SQLite via a persistent on-device file. The schema is created on first open. */
actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(AtlanDatabase.Schema, context, "atlan.db")
}
