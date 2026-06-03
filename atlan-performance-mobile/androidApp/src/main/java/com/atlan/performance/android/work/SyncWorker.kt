package com.atlan.performance.android.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.atlan.performance.shared.AtlanShared
import com.atlan.performance.shared.db.DatabaseDriverFactory

/**
 * Background drain of the local sync queue (offline-first §data flow: "platform worker drains the
 * queue later"). Opens the same persistent database the app writes to and runs the shared drain
 * engine. The "upload" is simulated this milestone (no backend) — see SyncUploader.
 *
 * Scheduled with a CONNECTED constraint so it only runs once connectivity is available, which is the
 * point at which a real backend sync would succeed.
 */
class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val shared = AtlanShared(DatabaseDriverFactory(applicationContext))
        shared.drainSyncQueue()
        return Result.success()
    }

    companion object {
        private const val UNIQUE_WORK = "atlan-sync-drain"

        /** Enqueue a one-shot drain that waits for connectivity. Safe to call repeatedly (KEEP). */
        fun schedule(context: Context) {
            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.KEEP, request)
        }
    }
}
