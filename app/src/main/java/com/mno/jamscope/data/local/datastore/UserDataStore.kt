package com.mno.jamscope.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mno.jamscope.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named

class UserDataStore @Inject constructor(
    @param:Named("user") private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val PROFILE_JSON = stringPreferencesKey("profile_json")
    }

    suspend fun saveUserProfile(user: User) {
        val jsonProfile = Json.encodeToString(user)
        dataStore.edit { prefs ->
            prefs[Keys.PROFILE_JSON] = jsonProfile
        }

    }

    fun getUserProfileFlow(): Flow<User?> {
        return dataStore.data.catch {
            emit(emptyPreferences())
        }.map { prefs ->
            prefs[Keys.PROFILE_JSON]?.let { Json.decodeFromString<User>(it) }
        }
    }

    suspend fun getUserProfile(): User? =
        getUserProfileFlow().firstOrNull()

    suspend fun isUserLoggedIn(): Boolean =
        getUserProfile()?.sessionKey?.isNotEmpty() == true

    suspend fun clearUserSession() = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs.remove(Keys.PROFILE_JSON)
        }
    }
}