package feature.photos.data.mapper

import feature.photos.data.dto.PhotoDto
import feature.photos.domain.entity.Photo

fun PhotoDto.toDomain(): Photo = Photo(
    id = id,
    title = title.ifBlank { "Untitled" },
    owner = ownerName,
    dateTaken = dateTaken,
    thumbnailUrl = urlS ?: "",
    largeUrl = urlL ?: urlS ?: "",
)


