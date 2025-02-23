package com.mno.jamscope.data.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.mno.jamscope.util.AppTheme
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
}