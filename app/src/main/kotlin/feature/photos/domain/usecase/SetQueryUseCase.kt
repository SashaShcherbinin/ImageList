package feature.photos.domain.usecase

import feature.photos.domain.repository.SearchQueryRepository

interface SetQueryUseCase {
    suspend operator fun invoke(query: String)
}

class SetQueryUseCaseImpl(
    private val searchQueryRepository: SearchQueryRepository,
) : SetQueryUseCase {
    override suspend fun invoke(query: String) {
        searchQueryRepository.saveSearchQuery(query)
    }
}
