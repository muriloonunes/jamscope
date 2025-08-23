package com.mno.jamscope.data.repository

import com.mno.jamscope.data.session.SettingsDataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    private val settingsDataStoreManager: SettingsDataStoreManager
) {
    fun themePreferenceFlow(): Flow<Int> = settingsDataStoreManager.themePreferenceFlow

    suspend fun saveThemePref(theme: Int) {
        settingsDataStoreManager.saveThemePref(theme)
    }

    fun getSwitchState(key: String, initialState: Boolean): Flow<Boolean> =
        settingsDataStoreManager.getSwitchState(key, initialState)

    suspend fun saveSwitchState(key: String, value: Boolean) {
        settingsDataStoreManager.saveSwitchState(key, value)
    }

    suspend fun clearPrefs() {
        settingsDataStoreManager.clearUserPrefs()
    }

//    suspend fun getAppOpenedFlow() = settingsDataStoreManager.appOpenedFlow.first()
//
//    suspend fun incrementAppOpened() {
//        settingsDataStoreManager.incrementAppOpened()
//    }
}