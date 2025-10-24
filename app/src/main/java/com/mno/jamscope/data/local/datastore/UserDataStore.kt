package com.mno.jamscope.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.mno.jamscope.domain.model.User
import com.mno.jamscope.util.Crypto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Base64
import javax.inject.Inject
import javax.inject.Named

class UserDataStore @Inject constructor(
    private val json: Json,
    @param:Named("user") private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val PROFILE_JSON = stringPreferencesKey("profile_json")
    }

    suspend fun saveUserProfile(user: User) {
        val jsonProfile = json.encodeToString(user)
        dataStore.edit { prefs ->
            prefs[Keys.PROFILE_JSON] = jsonProfile
        }

    }

    private fun getUserProfileFlow(): Flow<User?> {
        return dataStore.data.catch {
            emit(emptyPreferences())
        }.map { prefs ->
            val raw = prefs[Keys.PROFILE_JSON] ?: return@map null

            val user = try {
                json.decodeFromString<User>(raw)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

            if (user != null) {
                return@map user
            }

            try {
                val encryptedBytes = Base64.getDecoder().decode(raw)
                val decryptedBytes = Crypto.decrypt(encryptedBytes)
                val oldUser = json.decodeFromString<User>(decryptedBytes.decodeToString())

                migrateUserProfile(oldUser)

                oldUser
            } catch (_: Exception) {
                null
            }
        }
    }
    @Deprecated("Usado apenas para migração do DataStore. Remover na proxima release.")
    // TODO: Remover este método após migração completa e usar dataStore.edit no metodo getUserProfileFlow
    private suspend fun migrateUserProfile(user: User) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[Keys.PROFILE_JSON] = json.encodeToString(user)
            }
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