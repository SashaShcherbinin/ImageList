package app

import android.app.Application
import base.data.module.networkModule
import base.logger.di.loggerModule
import feature.photos.di.featurePhotosModule
import feature.notification.presentaion.notification.NotificationHelper
import feature.notification.presentaion.service.PollingService
import feature.notification.presentaion.service.PhotoPollingWorkerFactory
import feature.splash.di.featureSplashModule
import androidx.work.Configuration
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.java.KoinAndroidApplication
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        NotificationHelper.createNotificationChannel(this)
        initWorkManager()
        startPollingService()
    }

    private fun initKoin() {
        val koin = KoinAndroidApplication.create(this)
            .androidContext(this)
            .androidLogger(Level.ERROR)
            .modules(
                loggerModule(),
                networkModule(),
                featureSplashModule(),
                featurePhotosModule(),
            )
        startKoin(koin)
    }
    
    private fun initWorkManager() {
        val workerFactory = PhotoPollingWorkerFactory()
        val config = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        androidx.work.WorkManager.initialize(this, config)
    }
    
    private fun startPollingService() {
        val pollingService = GlobalContext.get().get<PollingService>()
        pollingService.start()
    }
}
