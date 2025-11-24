package feature.photos.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import feature.photos.domain.repository.SearchQueryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val SEARCH_QUERY_KEY = stringPreferencesKey("search_query")

class SearchQueryRepositoryImpl(
    private val dataStore: DataStore<Preferences>,
) : SearchQueryRepository {
    override fun getSearchQuery(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[SEARCH_QUERY_KEY] ?: ""
        }
    }

    override suspend fun saveSearchQuery(query: String) {
        dataStore.edit { preferences ->
            preferences[SEARCH_QUERY_KEY] =
                query
        }
    }
}

