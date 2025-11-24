package feature.notification.presentaion.service

import base.logger.AppLog
import feature.photos.domain.repository.SearchQueryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class PollingService(
    private val searchQueryRepository: SearchQueryRepository,
    private val pollingWorkManager: PollingWorkManager,
    private val appLog: AppLog,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isActivityActive = true
    private var currentQuery: String = ""

    fun start() {
        searchQueryRepository.getSearchQuery()
            .distinctUntilChanged()
            .onEach { query ->
                currentQuery = query
                updatePollingState()
            }
            .launchIn(scope)
    }

    fun onActivityStarted() {
        isActivityActive = true
        appLog.d("Activity started - stopping polling")
        pollingWorkManager.stopPolling()
    }

    fun onActivityStopped() {
        isActivityActive = false
        appLog.d("Activity stopped - checking if polling should start")
        updatePollingState()
    }

    private fun updatePollingState() {
        if (currentQuery.isNotEmpty() && !isActivityActive) {
            appLog.d("Starting polling for query: $currentQuery")
            pollingWorkManager.startPolling()
        } else if (currentQuery.isEmpty()) {
            appLog.d("Stopping polling - no active search query")
            pollingWorkManager.stopPolling()
        }
    }
}

