package feature.photos.domain.usecase

import feature.photos.domain.entity.PhotoPage
import feature.photos.domain.repository.HashResponseRepository
import feature.photos.domain.repository.PhotoRepository
import feature.photos.domain.repository.SearchQueryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach

interface GetPhotosUseCase {
    operator fun invoke(): Flow<Result<PhotoPage>>
}

class GetPhotosUseCaseImpl(
    private val photoRepository: PhotoRepository,
    private val searchQueryRepository: SearchQueryRepository,
    private val hashResponseRepository: HashResponseRepository,
) : GetPhotosUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(): Flow<Result<PhotoPage>> {
        return searchQueryRepository.getSearchQuery()
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isNotEmpty()) {
                    photoRepository.searchPhotos(query)
                        .onEach {
                            it.onSuccess { photoPage ->
                                hashResponseRepository
                                    .saveHashResponse(photoPage.photos.take(20))
                            }
                        }
                } else {
                    photoRepository.getRecentPhotos()
                }
            }
    }
}
