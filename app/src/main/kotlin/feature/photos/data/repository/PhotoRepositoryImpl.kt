package feature.photos.data.repository

import base.storage.common.storage.MemoryPageCacheStorage
import feature.photos.data.mapper.toDomain
import feature.photos.data.service.FlickrService
import feature.photos.domain.entity.PhotoPage
import feature.photos.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

const val PER_PAGE = 20

class PhotoRepositoryImpl(
    private val flickrService: FlickrService,
) : PhotoRepository {

    val memoryPageCacheStorage = MemoryPageCacheStorage(
        maxElements = 50,
        cacheTime = 30.seconds,
        network = { key: String, photoPage: PhotoPage?, page: Int ->
            if (key.isEmpty()) {
                val newSet = flickrService.getRecentPhotos(page, PER_PAGE)
                    .photos.photo.map { it.toDomain() }
                    .toSet()
                PhotoPage(
                    photos = (photoPage?.photos?.plus(newSet) ?: newSet).toList(),
                    hasMore = newSet.size == PER_PAGE
                )
            } else {
                val newSet = flickrService.searchPhotos(key, page, PER_PAGE)
                    .photos.photo.map { it.toDomain() }
                    .toSet()
                PhotoPage(
                    photos = (photoPage?.photos?.plus(newSet) ?: newSet).toList(),
                    hasMore = newSet.size == PER_PAGE
                )
            }
        },
    )

    override fun getRecentPhotos(): Flow<Result<PhotoPage>> {
        return memoryPageCacheStorage.get("")
    }

    override fun searchPhotos(query: String): Flow<Result<PhotoPage>> {
        return memoryPageCacheStorage.get(query)
    }

    override suspend fun resetRecent(query: String): Result<Unit> {
        return memoryPageCacheStorage.refresh(query)
    }

    override suspend fun fetchRecentMore(): Result<Unit> {
        return memoryPageCacheStorage.fetchNext("")
    }

    override suspend fun resetSearch(query: String): Result<Unit> {
        return memoryPageCacheStorage.refresh(query)
    }

    override suspend fun fetchSearchMore(query: String): Result<Unit> {
        return memoryPageCacheStorage.fetchNext(query)
    }
}
