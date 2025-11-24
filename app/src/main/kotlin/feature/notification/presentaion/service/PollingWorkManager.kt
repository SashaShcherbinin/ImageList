package feature.notification.presentaion.service

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class PollingWorkManager(private val context: Context) {

    companion object {
        private const val WORK_NAME = "photo_polling_work"
        private const val POLLING_INTERVAL_MINUTES = 15L
    }

    fun startPolling() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWork = PeriodicWorkRequestBuilder<PhotoPollingWorker>(
            POLLING_INTERVAL_MINUTES,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(0, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWork
        )
    }

    fun stopPolling() {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
