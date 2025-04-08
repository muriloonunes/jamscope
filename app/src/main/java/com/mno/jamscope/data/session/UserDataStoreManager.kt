package com.mno.jamscope.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.util.Crypto
import com.mno.jamscope.util.SortingType
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.util.Base64
import javax.inject.Inject

class UserDataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun saveUserProfile(profile: Profile) {
        val json = Json.encodeToString(profile)
        val encryptedJson = Crypto.encrypt(json.toByteArray())
        val encryptedBase64 = Base64.getEncoder().encodeToString(encryptedJson)
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PROFILE_JSON] = encryptedBase64
        }
    }

    suspend fun getUserProfile(): Profile? {
        val preferences = dataStore.data.first()
        val encryptedBase64 = preferences[PreferencesKeys.PROFILE_JSON] ?: return null
        return try {
            val encryptedBytes = Base64.getDecoder().decode(encryptedBase64)
            val decryptedBytes = Crypto.decrypt(encryptedBytes)
            Json.decodeFromString(decryptedBytes.decodeToString())
        } catch (e: Exception) {
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