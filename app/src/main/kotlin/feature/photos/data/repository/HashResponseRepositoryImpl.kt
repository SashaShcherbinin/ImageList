package feature.photos.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import base.logger.AppLog
import feature.photos.domain.entity.Photo
import feature.photos.domain.repository.HashResponseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val HASH_RESPONSE = stringPreferencesKey("hash_response")

class HashResponseRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
    private val appLog: AppLog,
) : HashResponseRepository {
    override fun getHashResponse(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[HASH_RESPONSE] ?: ""
        }
    }

    override suspend fun saveHashResponse(photos: List<Photo>) {
        dataStore.edit { preferences ->
            preferences[HASH_RESPONSE] = photos.toHashString()
        }
    }

    override suspend fun isUpdated(photos: List<Photo>): Boolean {
        val currentHash = getHashResponse().map { it }.first()
        appLog.d("Current hash: $currentHash")
        val newHash = photos.toHashString()
        appLog.d("New hash: $newHash")
        return currentHash != newHash
    }
}

private fun List<Photo>.toHashString(): String {
    return this.joinToString(separator = ",") { it.id }
}

