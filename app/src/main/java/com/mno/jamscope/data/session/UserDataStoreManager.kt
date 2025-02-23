package com.mno.jamscope.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.util.SortingType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UserDataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun saveUserProfile(profile: Profile) {
        val json = Json.encodeToString(profile)
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PROFILE_JSON] = json
        }
    }

    suspend fun getUserProfile(): Profile? {
        val preferences = dataStore.data.first()
        val profileJson = preferences[PreferencesKeys.PROFILE_JSON]
        return if (profileJson != null) {
            Json.decodeFromString(profileJson)
        } else {
            null
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        val profile = getUserProfile()
        return profile?.session?.key?.isNotEmpty() == true
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.PROFILE_JSON)
            preferences.remove(PreferencesKeys.SORTING_TYPE)
        }
    }

    suspend fun saveSortingType(sortingType: SortingType) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SORTING_TYPE] = sortingType.ordinal
        }
    }

    suspend fun getSortingType(): SortingType {
        return dataStore.data.map { preferences ->
            val ordinal = preferences[PreferencesKeys.SORTING_TYPE] ?: SortingType.DEFAULT.ordinal
            try {
                SortingType.entries[ordinal]
            } catch (e: IndexOutOfBoundsException) {
                SortingType.DEFAULT
            }
        }.first()
    }
}