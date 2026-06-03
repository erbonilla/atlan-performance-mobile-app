package com.atlan.performance.shared.db

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// Kotlin/Native has no Dispatchers.IO; Default offloads DB work off the main thread.
internal actual val ioDispatcher: CoroutineDispatcher = Dispatchers.Default
