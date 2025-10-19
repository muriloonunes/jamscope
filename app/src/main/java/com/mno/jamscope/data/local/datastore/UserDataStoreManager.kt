package com.mno.jamscope.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mno.jamscope.domain.model.User
import com.mno.jamscope.util.Crypto
import com.mno.jamscope.util.SortingType
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Base64
import javax.inject.Inject

@Deprecated(
    "This class is deprecated, use UserDataStore instead",
    ReplaceWith("UserDataStore"),
)
class UserDataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun saveUserProfile(user: User) = withContext(Dispatchers.IO) {
        val json = Json.encodeToString(user)
        val encryptedJson = Crypto.encrypt(json.toByteArray())
        val encryptedBase64 = Base64.getEncoder().encodeToString(encryptedJson)
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PROFILE_JSON] = encryptedBase64
        }
    }

    suspend fun getUserProfile(): User? = withContext(Dispatchers.IO) {
        val preferences = dataStore.data.first()
        val encryptedBase64 = preferences[PreferencesKeys.PROFILE_JSON] ?: return@withContext null
        return@withContext try {
            val encryptedBytes = Base64.getDecoder().decode(encryptedBase64)
            val decryptedBytes = Crypto.decrypt(encryptedBytes)
            Json.decodeFromString(decryptedBytes.decodeToString())
        } catch (_: Exception) {
            null
        }
    }

    suspend fun isUserLoggedIn(): Boolean {
        val user = getUserProfile()
        return user?.sessionKey?.isNotEmpty() == true
    }

    suspend fun clearUserSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.PROFILE_JSON)
            preferences.remove(PreferencesKeys.SORTING_TYPE)
            preferences.remove(PreferencesKeys.APP_VERSION)
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
            } catch (_: IndexOutOfBoundsException) {
                SortingType.DEFAULT
            }
        }.first()
    }

    suspend fun getAppVersion(): Int {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.APP_VERSION] ?: 0
        }.first()
    }

    suspend fun saveAppVersion(version: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.APP_VERSION] = version
        }
    }
}