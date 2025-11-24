package feature.photos.domain.usecase

import feature.photos.data.repository.PhotoRepositoryImpl
import feature.photos.domain.repository.HashResponseRepository
import feature.photos.domain.repository.PhotoRepository
import feature.photos.domain.repository.SearchQueryRepository
import kotlinx.coroutines.flow.first

interface IsUpdatedUseCase {
    suspend operator fun invoke(): Boolean
}

class IsUpdatedUseCaseImpl(
    private val searchQueryRepository: SearchQueryRepository,
    private val photoRepository: PhotoRepository,
    private val hashResponseRepository: HashResponseRepository,
) : IsUpdatedUseCase {
    override suspend fun invoke(): Boolean {
        val query = searchQueryRepository.getSearchQuery().first()
        if (query.isNotEmpty()) {
            photoRepository.resetSearch(query)
            photoRepository.searchPhotos(query).first()
                .onSuccess {
                    return hashResponseRepository.isUpdated(it.photos.take(20))
                }
        }
        return false
    }
}
