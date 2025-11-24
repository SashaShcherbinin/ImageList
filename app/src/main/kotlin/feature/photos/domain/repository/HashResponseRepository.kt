package feature.photos.domain.repository

import feature.photos.domain.entity.Photo
import kotlinx.coroutines.flow.Flow

interface HashResponseRepository {
    fun getHashResponse(): Flow<String>
    suspend fun saveHashResponse(photos: List<Photo>)
    suspend fun isUpdated(photos: List<Photo>): Boolean
}

