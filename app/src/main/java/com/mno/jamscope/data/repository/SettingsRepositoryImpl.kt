package com.mno.jamscope.data.repository

import com.mno.jamscope.data.local.datastore.SettingsDataStore
import com.mno.jamscope.domain.repository.SettingsRepository
import com.mno.jamscope.features.settings.domain.model.SwitchState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore,
) : SettingsRepository {
    override fun getThemePreferenceFlow(): Flow<Int> {
        return settingsDataStore.themeFlow
    }

    override suspend fun saveThemePreference(theme: Int) {
        withContext(Dispatchers.IO) {
            settingsDataStore.saveTheme(theme)
        }
    }

    override fun getSwitchState(
        key: String,
        initialState: SwitchState,
    ): Flow<SwitchState> {
        return settingsDataStore.getSwitchState(key, initialState)
    }

    override suspend fun saveSwitchState(
        key: String,
        value: SwitchState,
    ) {
        settingsDataStore.saveSwitchState(key, value)
    }

    override suspend fun getAppVersion(): Int {
        return withContext(Dispatchers.IO) {
            settingsDataStore.getAppVersion()
        }
    }

    override suspend fun saveAppVersion(version: Int) {
        withContext(Dispatchers.IO) {
            settingsDataStore.saveAppVersion(version)
        }
    }

    override suspend fun clearUserData() {
        withContext(Dispatchers.IO) {
            settingsDataStore.clearUserPrefs()
        }
    }
}