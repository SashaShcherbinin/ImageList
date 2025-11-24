package feature.photos.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchQueryRepository {
    fun getSearchQuery(): Flow<String>
    suspend fun saveSearchQuery(query: String)
}

