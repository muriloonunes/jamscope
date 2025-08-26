package com.mno.jamscope.features.settings.state

import com.mno.jamscope.R


data class SettingsUiState(
    val themePreference: Int = 0,
    val switchStates: Map<String, Boolean> = emptyMap(),
    val selectedTile: Int = R.string.personalization_setting_title,
    val showThemeDialog: Boolean = false,
    val showLogOutDialog: Boolean = false
)