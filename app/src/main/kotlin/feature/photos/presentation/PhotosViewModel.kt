@file:Suppress("OPT_IN_USAGE")

package feature.photos.presentation

import base.compose.mvi.MviViewModel
import base.compose.mvi.Processor
import base.compose.mvi.Publisher
import base.compose.mvi.Reducer
import base.logger.AppLog
import feature.photos.domain.usecase.FetchMoreUseCase
import feature.photos.domain.usecase.GetPhotosUseCase
import feature.photos.domain.usecase.GetQueryUseCase
import feature.photos.domain.usecase.SetQueryUseCase
import feature.photos.presentation.ContentState.Content
import feature.photos.presentation.ContentState.Empty
import feature.photos.presentation.ContentState.Error
import feature.photos.presentation.ContentState.Loading
import feature.photos.presentation.PhotosIntent.LoadData
import feature.photos.presentation.PhotosIntent.LoadMore
import feature.photos.presentation.PhotosIntent.LoadQuery
import feature.photos.presentation.PhotosIntent.Search
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transformLatest

class PhotosViewModel(
    processor: PhotosProcessor,
    reducer: PhotosReducer,
    publisher: PhotosPublisher,
) : MviViewModel<PhotosIntent, PhotosEffect, PhotosEvent, PhotosState>(
    defaultState = PhotosState(),
    processor = processor,
    reducer = reducer,
    publisher = publisher,
) {
    init {
        process(LoadQuery)
        process(LoadData)
    }
}

class PhotosProcessor(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val fetchMoreUseCase: FetchMoreUseCase,
    private val setQueryUseCase: SetQueryUseCase,
    private val getQueryUseCase: GetQueryUseCase,
    private val appLog: AppLog,
) : Processor<PhotosIntent, PhotosEffect, PhotosState> {
    override fun process(intent: PhotosIntent, state: PhotosState): Flow<PhotosEffect> =
        when (intent) {
            is LoadQuery -> {
                flow {
                    val query = getQueryUseCase().first()
                    emit(PhotosEffect.NewSearchQuery(query))
                }
            }

            is LoadData -> {
                getPhotosUseCase()
                    .transformLatest {
                        it.fold(
                            onSuccess = { page ->
                                if (page.photos.isEmpty()) {
                                    emit(PhotosEffect.OnNewState(Empty))
                                } else {
                                    emit(
                                        PhotosEffect.PhotosLoaded(
                                            photos = page.photos,
                                            hasMore = page.hasMore,
                                        )
                                    )
                                    emit(PhotosEffect.OnNewState(Content))
                                }
                            },
                            onFailure = { e ->
                                appLog.e(e = e)
                                emit(
                                    PhotosEffect
                                        .OnError(e.message ?: "Failed to load photos")
                                )
                            }
                        )
                    }
                    .onStart {
                        PhotosEffect.OnNewState(Loading)
                    }
            }

            is Search -> {
                flow {
                    setQueryUseCase(intent.query)
                    emit(PhotosEffect.NewSearchQuery(intent.query))
                }
            }

            is PhotosIntent.Retry -> {
                process(LoadData, state)
            }

            is LoadMore -> {
                flow {
                    if (state.fetchingMore.not() && state.hasMore) {
                        emit(PhotosEffect.OnFetchingMore(true))
                        fetchMoreUseCase(state.searchQuery)
                            .onFailure { e ->
                                appLog.e(e = e)
                            }
                    }
                }.onCompletion {
                    emit(PhotosEffect.OnFetchingMore(false))
                }
            }
        }
}

class PhotosReducer : Reducer<PhotosEffect, PhotosState> {
    override fun reduce(effect: PhotosEffect, state: PhotosState): PhotosState {
        return when (effect) {
            is PhotosEffect.PhotosLoaded -> {
                state.copy(
                    photos = effect.photos,
                    hasMore = effect.hasMore,
                )
            }

            is PhotosEffect.OnError -> {
                state.copy(contentState = Error(effect.message))
            }

            is PhotosEffect.NewSearchQuery -> {
                state.copy(searchQuery = effect.query)
            }

            is PhotosEffect.OnFetchingMore -> {
                state.copy(fetchingMore = effect.fetching)
            }

            is PhotosEffect.OnNewState -> {
                state.copy(contentState = effect.state)
            }
        }
    }
}

class PhotosPublisher : Publisher<PhotosEffect, PhotosEvent> {
    override fun publish(effect: PhotosEffect): PhotosEvent? {
        return when (effect) {
            is PhotosEffect.NewSearchQuery -> null
            is PhotosEffect.OnError -> null
            is PhotosEffect.OnFetchingMore -> null
            is PhotosEffect.OnNewState -> null
            is PhotosEffect.PhotosLoaded -> null
        }
    }
}
