package feature.notification.presentaion.service

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import base.logger.AppLog
import feature.photos.domain.repository.SearchQueryRepository
import feature.photos.domain.usecase.IsUpdatedUseCase
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PhotoPollingWorkerFactory : WorkerFactory(), KoinComponent {

    private val isUpdatedUseCase: IsUpdatedUseCase by inject()
    private val searchQueryRepository: SearchQueryRepository by inject()
    private val appLog: AppLog by inject()

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            PhotoPollingWorker::class.java.name -> {
                PhotoPollingWorker(
                    appContext,
                    workerParameters,
                    isUpdatedUseCase,
                    searchQueryRepository,
                    appLog
                )
            }

            else -> null
        }
    }
}

