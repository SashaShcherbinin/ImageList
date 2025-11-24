package feature.notification.presentaion.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import base.logger.AppLog
import feature.notification.presentaion.notification.NotificationHelper
import feature.photos.domain.repository.SearchQueryRepository
import feature.photos.domain.usecase.IsUpdatedUseCase
import kotlinx.coroutines.flow.first

class PhotoPollingWorker(
    context: Context,
    params: WorkerParameters,
    private val isUpdatedUseCase: IsUpdatedUseCase,
    private val searchQueryRepository: SearchQueryRepository,
    private val appLog: AppLog,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val isUpdated = isUpdatedUseCase()
            val query = searchQueryRepository.getSearchQuery().first()
            appLog.d("Polling worker checked for updates with query: $query")
            if (isUpdated) {
                appLog.d("New search results detected for query: $query")
                NotificationHelper.showNewResultsNotification(applicationContext, query)
            } else {
                appLog.d("No new search results for query: $query")
            }

            Result.success()
        } catch (e: Exception) {
            appLog.e(e = e)
            Result.retry()
        }
    }
}

