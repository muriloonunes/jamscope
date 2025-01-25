import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.murile.nowplaying.data.model.Profile
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_data_store")

object PreferencesKeys {
    val PROFILE_JSON = stringPreferencesKey("profile_json")
}

class UserSessionManager(private val context: Context) {
    suspend fun saveUserProfile(profile: Profile) {
        val json = Json.encodeToString(profile)
        context.userDataStore.edit { preferences ->
            preferences[PreferencesKeys.PROFILE_JSON] = json
        }
    }

    suspend fun getUserProfile(): Profile? {
        val preferences = context.userDataStore.data.first()
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
        context.userDataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.PROFILE_JSON)
        }
    }
}