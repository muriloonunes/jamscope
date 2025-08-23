package com.mno.jamscope.features.settings.state


 data class SettingsUiState(
     val themePreference: Int = 0,
     val switchStates: Map<String, Boolean> = emptyMap(),
     val showThemeDialog: Boolean = false,
     val showLogOutDialog: Boolean = false
 )