package com.mno.jamscope.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import com.mno.jamscope.features.settings.domain.model.SwitchState
import com.mno.jamscope.ui.theme.AppTheme
import com.mno.jamscope.util.SortingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class SettingsDataStore @Inject constructor(
    @param:Named("settings") private val dataStore: DataStore<Preferences>,
) {
    private object Keys {
        val SORTING_TYPE = intPreferencesKey("sorting_type")
        val THEME = intPreferencesKey("theme")
        val APP_OPENED = intPreferencesKey("app_opened")
        val APP_VERSION = intPreferencesKey("app_version")
        fun switchKey(key: String) = booleanPreferencesKey("switch_state_$key")
    }

    val themeFlow: Flow<Int> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            prefs[Keys.THEME] ?: AppTheme.SYSTEM.ordinal
        }

    suspend fun saveTheme(theme: Int) = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs[Keys.THEME] = theme
        }
    }

    fun getSwitchState(key: String, initial: SwitchState): Flow<SwitchState> =
        dataStore.data.map { prefs ->
            val value = prefs[Keys.switchKey(key)] ?: initial.value
            SwitchState.fromValue(value)
        }

    suspend fun saveSwitchState(key: String, value: SwitchState) = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs[Keys.switchKey(key)] = value.value
        }
    }

    suspend fun saveSortingType(type: SortingType) = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs[Keys.SORTING_TYPE] = type.ordinal
        }
    }

    suspend fun getSortingType(): SortingType =
        dataStore.data
            .catch { emit(emptyPreferences()) }
            .map { prefs ->
                val ordinal = prefs[Keys.SORTING_TYPE] ?: SortingType.DEFAULT.ordinal
                SortingType.entries.getOrNull(ordinal) ?: SortingType.DEFAULT
            }.first()

    suspend fun getAppVersion(): Int {
        return dataStore.data.map { preferences ->
            preferences[Keys.APP_VERSION] ?: 0
        }.first()
    }

    suspend fun saveAppVersion(version: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_VERSION] = version
        }
    }

    suspend fun clearUserPrefs() = withContext(Dispatchers.IO) {
        dataStore.edit { prefs ->
            prefs.remove(Keys.THEME)
            prefs.remove(Keys.SORTING_TYPE)
            prefs.remove(Keys.APP_VERSION)
        }
    }
}