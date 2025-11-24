package feature.photos.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import base.domain.extention.factoryCastOf
import base.domain.extention.singleCalsOf
import feature.notification.presentaion.service.PollingService
import feature.notification.presentaion.service.PollingWorkManager
import feature.photos.data.repository.HashResponseRepositoryImpl
import feature.photos.data.repository.PhotoRepositoryImpl
import feature.photos.data.repository.SearchQueryRepositoryImpl
import feature.photos.data.service.FlickrService
import feature.photos.domain.repository.HashResponseRepository
import feature.photos.domain.repository.PhotoRepository
import feature.photos.domain.repository.SearchQueryRepository
import feature.photos.domain.usecase.FetchMoreUseCase
import feature.photos.domain.usecase.FetchMoreUseCaseImpl
import feature.photos.domain.usecase.GetPhotosUseCase
import feature.photos.domain.usecase.GetPhotosUseCaseImpl
import feature.photos.domain.usecase.GetQueryUseCase
import feature.photos.domain.usecase.GetQueryUseCaseImpl
import feature.photos.domain.usecase.IsUpdatedUseCase
import feature.photos.domain.usecase.IsUpdatedUseCaseImpl
import feature.photos.domain.usecase.SetQueryUseCase
import feature.photos.domain.usecase.SetQueryUseCaseImpl
import feature.photos.presentation.PhotosProcessor
import feature.photos.presentation.PhotosPublisher
import feature.photos.presentation.PhotosReducer
import feature.photos.presentation.PhotosViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private val Context.photosDataStore:
        DataStore<Preferences> by preferencesDataStore(name = "photos_preferences")

fun featurePhotosModule(): Module = module {
    single<DataStore<Preferences>> {
        get<Context>().photosDataStore
    }
    viewModelOf(::PhotosViewModel)
    factoryOf(::PhotosProcessor)
    factoryOf(::PhotosPublisher)
    factoryOf(::PhotosReducer)
    factoryOf(::FlickrService)

    factoryCastOf(::FetchMoreUseCaseImpl, FetchMoreUseCase::class)
    factoryCastOf(::GetPhotosUseCaseImpl, GetPhotosUseCase::class)
    factoryCastOf(::GetQueryUseCaseImpl, GetQueryUseCase::class)
    factoryCastOf(::SetQueryUseCaseImpl, SetQueryUseCase::class)
    factoryCastOf(::IsUpdatedUseCaseImpl, IsUpdatedUseCase::class)

    singleCalsOf(::PhotoRepositoryImpl, PhotoRepository::class)
    singleCalsOf(::SearchQueryRepositoryImpl, SearchQueryRepository::class)
    singleCalsOf(::HashResponseRepositoryImpl, HashResponseRepository::class)

    factoryOf(::PollingWorkManager)
    single {
        PollingService(
            searchQueryRepository = get(),
            pollingWorkManager = get(),
            appLog = get()
        )
    }
}

