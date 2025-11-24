package feature.photos.domain.navigation

import feature.photos.domain.entity.Photo
import kotlinx.serialization.Serializable

@Serializable
data class NavPhotoDetail(
    val photo: Photo,
)
