package com.mno.jamscope.features.settings.state

import com.mno.jamscope.R
import com.mno.jamscope.features.settings.domain.model.SwitchState

data class SettingsUiState(
    val themePreference: Int = 0,
    val switchStates: Map<String, SwitchState> = emptyMap(),
    val selectedTile: Int = R.string.personalization_setting_title,
    val showThemeDialog: Boolean = false,
    val showLogOutDialog: Boolean = false
)