package feature.photos.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlickrResponseDto(
    @SerialName("photos")
    val photos: PhotosDto,
    @SerialName("stat")
    val stat: String,
)

@Serializable
data class PhotosDto(
    @SerialName("page")
    val page: Int,
    @SerialName("pages")
    val pages: Int,
    @SerialName("perpage")
    val perPage: Int,
    @SerialName("total")
    val total: Int,
    @SerialName("photo")
    val photo: List<PhotoDto>,
)

@Serializable
data class PhotoDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("ownername")
    val ownerName: String,
    @SerialName("datetaken")
    val dateTaken: String? = null,
    @SerialName("url_s")
    val urlS: String? = null,
    @SerialName("url_l")
    val urlL: String? = null,
)


