package com.mno.jamscope.domain.repository

import com.mno.jamscope.features.settings.domain.model.SwitchState
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getThemePreferenceFlow(): Flow<Int>
    suspend fun saveThemePreference(theme: Int)
    fun getSwitchState(key: String, initialState: SwitchState): Flow<SwitchState>
    suspend fun saveSwitchState(key: String, value: SwitchState)
    suspend fun getAppVersion(): Int
    suspend fun saveAppVersion(version: Int)
    suspend fun clearUserData()
}