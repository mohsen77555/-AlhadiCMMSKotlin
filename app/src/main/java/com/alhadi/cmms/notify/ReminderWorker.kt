package com.alhadi.cmms.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alhadi.cmms.AlhadiApplication

/** Background job that checks for due maintenance and posts a summary reminder. */
class ReminderWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val repository = (applicationContext as AlhadiApplication).repository
            val summary = Reminders.collect(repository)
            Reminders.postSummary(applicationContext, summary)
            Result.success()
        } catch (_: Throwable) {
            Result.retry()
        }
    }
}
