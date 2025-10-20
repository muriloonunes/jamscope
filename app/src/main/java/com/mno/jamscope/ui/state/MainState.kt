package com.mno.jamscope.ui.state

import com.mno.jamscope.ui.navigator.Destination

data class MainState(
    val isLoading: Boolean = true,
    val startDestination: Destination? = null,
    val showChangelog: Boolean = false,
    val themePreference: Int = 0,
)
