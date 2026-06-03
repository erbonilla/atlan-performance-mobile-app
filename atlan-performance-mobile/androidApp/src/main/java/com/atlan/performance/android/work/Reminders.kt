package com.atlan.performance.android.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import java.time.Duration
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

/**
 * Local session reminders — fully client-side (no backend). The user opts in from the notification
 * rationale screen (§21), which requests POST_NOTIFICATIONS (Android 13+) and then schedules a calm
 * daily reminder via WorkManager. Honest scope: a local notification only; no server push.
 */
object Reminders {
    const val CHANNEL_ID = "atlan_reminders"
    private const val UNIQUE_WORK = "atlan-session-reminder"
    private const val NOTIFICATION_ID = 4101
    private const val REMINDER_HOUR = 7 // calm morning nudge, matching the seed cadence

    fun ensureChannel(context: Context) {
        val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (mgr.getNotificationChannel(CHANNEL_ID) == null) {
            mgr.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Calm reminders before sessions"
                }
            )
        }
    }

    /** Schedule a daily reminder near [REMINDER_HOUR] local time. Idempotent (KEEP). */
    fun scheduleDaily(context: Context) {
        ensureChannel(context)
        val now = ZonedDateTime.now()
        var next = now.withHour(REMINDER_HOUR).withMinute(0).withSecond(0).withNano(0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val initialDelayMs = Duration.between(now, next).toMillis()
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(initialDelayMs, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(UNIQUE_WORK, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(UNIQUE_WORK)
    }

    fun notificationsAllowed(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
}

/** Posts the calm session reminder. Respects the persisted language; no-ops if permission is absent. */
class ReminderWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        if (!Reminders.notificationsAllowed(applicationContext)) return Result.success()
        val es = applicationContext
            .getSharedPreferences("atlan_prefs", Context.MODE_PRIVATE)
            .getString("language", "en") == "es"
        Reminders.ensureChannel(applicationContext)
        val notification = NotificationCompat.Builder(applicationContext, Reminders.CHANNEL_ID)
            .setContentTitle(if (es) "Tu sesión de hoy" else "Today's session")
            .setContentText(
                if (es) "Una sesión de umbral te espera cuando estés listo."
                else "A Threshold session is ready when you are."
            )
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify(4101, notification)
        return Result.success()
    }
}
