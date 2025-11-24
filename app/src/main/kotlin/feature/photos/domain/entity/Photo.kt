package feature.photos.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class Photo(
    val id: String,
    val title: String,
    val owner: String,
    val dateTaken: String?,
    val thumbnailUrl: String,
    val largeUrl: String,
)
