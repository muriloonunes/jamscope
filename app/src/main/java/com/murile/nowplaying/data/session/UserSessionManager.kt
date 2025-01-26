package com.murile.nowplaying.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.murile.nowplaying.data.model.Profile
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import javax.inject.Inject

object PreferencesKeys {
    val PROFILE_JSON = stringPreferencesKey("profile_json")
}

class UserSessionManager @Inject constructor(
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
        }
    }
}