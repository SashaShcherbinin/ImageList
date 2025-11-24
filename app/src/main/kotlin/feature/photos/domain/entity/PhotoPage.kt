package feature.photos.domain.entity

data class PhotoPage(
    val photos: List<Photo>,
    val hasMore: Boolean,
)


