package feature.photos.data.service

import feature.photos.data.dto.FlickrResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url

class FlickrService(private val httpClient: HttpClient) {
    
    suspend fun getRecentPhotos(page: Int, perPage: Int): FlickrResponseDto {
        return httpClient.get {
            url(FlickrApi.BASE_URL)
            parameter("method", FlickrApi.METHOD_GET_RECENT)
            parameter("api_key", FlickrApi.API_KEY)
            parameter("extras", FlickrApi.EXTRAS)
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("format", "json")
            parameter("nojsoncallback", 1)
        }.body()
    }
    
    suspend fun searchPhotos(query: String, page: Int, perPage: Int): FlickrResponseDto {
        return httpClient.get {
            url(FlickrApi.BASE_URL)
            parameter("method", FlickrApi.METHOD_SEARCH)
            parameter("api_key", FlickrApi.API_KEY)
            parameter("text", query)
            parameter("extras", FlickrApi.EXTRAS)
            parameter("page", page)
            parameter("per_page", perPage)
            parameter("format", "json")
            parameter("nojsoncallback", 1)
        }.body()
    }
}


