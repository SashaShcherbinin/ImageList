package feature.photos.presentation

import base.compose.mvi.Effect
import base.compose.mvi.Event
import base.compose.mvi.Intent
import base.compose.mvi.State
import feature.photos.domain.entity.Photo

sealed class ContentState {
    data object Loading : ContentState()
    data object Content : ContentState()
    data class Error(val message: String) : ContentState()
    data object Empty : ContentState()
}

data class PhotosState(
    val contentState: ContentState = ContentState.Loading,
    val photos: List<Photo> = emptyList(),
    val hasMore: Boolean = false,
    val searchQuery: String = "",
    val fetchingMore: Boolean = false,
) : State

sealed class PhotosIntent : Intent {
    data object LoadData : PhotosIntent()
    data object LoadQuery : PhotosIntent()
    data class Search(val query: String) : PhotosIntent()
    data object LoadMore : PhotosIntent()
    data object Retry : PhotosIntent()
}

sealed class PhotosEffect : Effect {
    data class PhotosLoaded(
        val photos: List<Photo>,
        val hasMore: Boolean,
    ) : PhotosEffect()

    data class OnError(val message: String) : PhotosEffect()
    data class OnNewState(val state: ContentState) : PhotosEffect()
    data class NewSearchQuery(val query: String) : PhotosEffect()
    data class OnFetchingMore(val fetching: Boolean) : PhotosEffect()
}

sealed class PhotosEvent : Event

