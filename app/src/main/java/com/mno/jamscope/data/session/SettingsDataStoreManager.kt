package com.mno.jamscope.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.mno.jamscope.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataStoreManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private fun getKey(key: String) = booleanPreferencesKey("switch_state_$key")
    }

    fun getSwitchState(key: String, initialState: Boolean): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[getKey(key)] ?: initialState
        }
    }

    suspend fun saveSwitchState(key: String, value: Boolean) {
        dataStore.edit { preferences ->
            preferences[getKey(key)] = value
        }
    }

    val themePreferenceFlow : Flow<Int> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME] ?: AppTheme.SYSTEM.ordinal
    }

    suspend fun saveThemePref(theme: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

//    suspend fun incrementAppOpened() = dataStore.edit {
//        preferences -> preferences[PreferencesKeys.APP_OPENED] = (preferences[PreferencesKeys.APP_OPENED] ?: 0) + 1
//    }
//
//    val appOpenedFlow : Flow<Int> = dataStore.data.map { preferences ->
//        preferences[PreferencesKeys.APP_OPENED] ?: 0
//    }

    suspend fun clearUserPrefs() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.THEME)
            preferences.remove(PreferencesKeys.APP_OPENED)
            preferences.remove(getKey("card_background_color_toggle"))
            preferences.remove(getKey("playing_animation_toggle"))
        }
    }
}