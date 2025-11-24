package feature.photos.domain.usecase

import feature.photos.domain.repository.SearchQueryRepository
import kotlinx.coroutines.flow.Flow

interface GetQueryUseCase {
    operator fun invoke(): Flow<String>
}

class GetQueryUseCaseImpl(
    private val searchQueryRepository: SearchQueryRepository,
) : GetQueryUseCase {
    override fun invoke(): Flow<String> {
        return searchQueryRepository.getSearchQuery()
    }
}
