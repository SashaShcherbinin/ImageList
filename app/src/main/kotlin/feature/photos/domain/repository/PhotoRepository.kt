package feature.photos.domain.repository

import feature.photos.domain.entity.PhotoPage
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getRecentPhotos(): Flow<Result<PhotoPage>>
    fun searchPhotos(query: String): Flow<Result<PhotoPage>>
    suspend fun resetRecent(query: String): Result<Unit>
    suspend fun fetchRecentMore(): Result<Unit>
    suspend fun resetSearch(query: String): Result<Unit>
    suspend fun fetchSearchMore(query: String): Result<Unit>
}


