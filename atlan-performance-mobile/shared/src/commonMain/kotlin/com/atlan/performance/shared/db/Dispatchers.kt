package com.atlan.performance.shared.db

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Dispatcher for local-database IO, so SQLDelight calls never run on the UI thread. JVM/Android use
 * `Dispatchers.IO` (tuned for blocking IO); Kotlin/Native has no IO dispatcher, so iOS uses
 * `Dispatchers.Default`. The repositories wrap their queries in `withContext(ioDispatcher)`.
 */
internal expect val ioDispatcher: CoroutineDispatcher
