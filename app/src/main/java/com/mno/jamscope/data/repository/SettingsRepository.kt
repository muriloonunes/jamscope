package com.mno.jamscope.data.repository

import com.mno.jamscope.data.local.datastore.SettingsDataStoreManager
import com.mno.jamscope.features.settings.domain.model.SwitchState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@Deprecated(
    "This class is deprecated, use SettingsRepositoryImpl instead",
    ReplaceWith("SettingsRepositoryImpl"),
)
class SettingsRepository @Inject constructor(
    private val settingsDataStoreManager: SettingsDataStoreManager
) {
    fun themePreferenceFlow(): Flow<Int> = settingsDataStoreManager.themePreferenceFlow

    suspend fun saveThemePref(theme: Int) {
        settingsDataStoreManager.saveThemePref(theme)
    }

    fun getSwitchState(key: String, initialState: SwitchState): Flow<SwitchState> =
        settingsDataStoreManager.getSwitchState(key, initialState)

    suspend fun saveSwitchState(key: String, value: SwitchState) {
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