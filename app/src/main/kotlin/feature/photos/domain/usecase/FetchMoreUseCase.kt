package feature.photos.domain.usecase

import feature.photos.domain.repository.PhotoRepository

interface FetchMoreUseCase {
    suspend operator fun invoke(query: String): Result<Unit>
}

class FetchMoreUseCaseImpl(
    private val photoRepository: PhotoRepository,
) : FetchMoreUseCase {
    override suspend fun invoke(query: String): Result<Unit> {
        return if (query.isNotEmpty()) {
            photoRepository.fetchSearchMore(query)
        } else {
            photoRepository.fetchRecentMore()
        }
    }
}
