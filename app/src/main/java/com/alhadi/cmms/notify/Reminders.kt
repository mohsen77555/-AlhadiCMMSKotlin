package com.alhadi.cmms.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.alhadi.cmms.MainActivity
import com.alhadi.cmms.data.CmmsRepository
import com.alhadi.cmms.util.DateStrings
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/** Counts of everything that needs the team's attention right now. */
data class ReminderSummary(
    val duePm: Int = 0,
    val overdueWorkOrders: Int = 0,
    val expiringPermits: Int = 0,
    val lowStock: Int = 0,
    val pendingPurchases: Int = 0
) {
    val total: Int get() = duePm + overdueWorkOrders + expiringPermits + lowStock + pendingPurchases

    fun lines(): List<String> = buildList {
        if (duePm > 0) add("صيانة دورية مستحقة: $duePm")
        if (overdueWorkOrders > 0) add("أوامر عمل متأخرة: $overdueWorkOrders")
        if (expiringPermits > 0) add("تصاريح تنتهي قريباً: $expiringPermits")
        if (lowStock > 0) add("قطع تحت الحد الأدنى: $lowStock")
        if (pendingPurchases > 0) add("طلبات شراء معلّقة: $pendingPurchases")
    }
}

/**
 * Local maintenance reminders. A daily WorkManager job checks the database and posts a single
 * summary notification for anything overdue/expiring/low — no server required.
 */
object Reminders {
    const val CHANNEL_ID = "cmms_reminders"
    private const val PERIODIC_WORK = "cmms_daily_reminders"
    private const val ONESHOT_WORK = "cmms_reminder_now"
    private const val NOTIFICATION_ID = 4201
    private const val EXPIRY_WINDOW_DAYS = 7

    /** Reads the current state and computes what needs attention. */
    suspend fun collect(repository: CmmsRepository, today: String = DateStrings.today()): ReminderSummary {
        val pm = repository.preventiveMaintenance.first()
        val workOrders = repository.workOrders.first()
        val permits = repository.workPermits.first()
        val parts = repository.spareParts.first()
        val purchases = repository.purchaseOrders.first()
        val soon = DateStrings.daysFromToday(EXPIRY_WINDOW_DAYS)
        return ReminderSummary(
            duePm = pm.count { DateStrings.isDueOrOverdue(it.nextDueAt, today) },
            overdueWorkOrders = workOrders.count { it.status != "Closed" && it.dueAt < today },
            expiringPermits = permits.count {
                it.status != "Rejected" && it.status != "Closed" && it.validUntil.isNotBlank() && it.validUntil <= soon
            },
            lowStock = parts.count { it.onHandQty <= it.minQty },
            pendingPurchases = purchases.count { it.status == "Requested" || it.status == "Approved" || it.status == "Ordered" }
        )
    }

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تذكيرات الصيانة",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "تنبيهات الصيانة المستحقة وأوامر العمل المتأخرة" }
            context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    /** Posts (or skips) the summary notification. No-op when nothing is due or notifications are off. */
    fun postSummary(context: Context, summary: ReminderSummary) {
        if (summary.total == 0) return
        ensureChannel(context)
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java)
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP }
        val pending = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentTitle("تذكير الصيانة — ${summary.total} بند بحاجة لمتابعة")
            .setContentText(summary.lines().joinToString(" • "))
            .setStyle(NotificationCompat.BigTextStyle().bigText(summary.lines().joinToString("\n")))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()

        try {
            manager.notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // POST_NOTIFICATIONS not granted yet — silently skip.
        }
    }

    /** Schedules the recurring daily check (kept if already scheduled). */
    fun schedulePeriodic(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS).build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(PERIODIC_WORK, ExistingPeriodicWorkPolicy.KEEP, request)
    }

    /** Runs a one-off check immediately (used by the in-app "check now" button). */
    fun runNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>().build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(ONESHOT_WORK, ExistingWorkPolicy.REPLACE, request)
    }
}
