package com.mno.jamscope.data.repository

import com.mno.jamscope.data.session.SettingsDataStoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDataStoreManager: SettingsDataStoreManager
) {
    suspend fun getThemePreferenceFlow() = settingsDataStoreManager.themePreferenceFlow.first()

    suspend fun saveThemePref(theme: Int) {
        settingsDataStoreManager.saveThemePref(theme)
    }

    fun getSwitchState(key: String, initialState: Boolean): Flow<Boolean> =
        settingsDataStoreManager.getSwitchState(key, initialState)

    suspend fun saveSwitchState(key: String, value: Boolean) {
        settingsDataStoreManager.saveSwitchState(key, value)
    }
}