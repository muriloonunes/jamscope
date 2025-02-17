package com.mno.jamscope.ui.components

import androidx.compose.ui.graphics.vector.ImageVector

const val LOGIN_SCREEN = "login_screen"
const val LOGIN_ROUTE = "login"
const val APP_ROUTE = "app"
const val FRIENDS_SCREEN = "friends"
const val PROFILE_SCREEN = "profile"
const val SETTINGS_SCREEN = "settings"

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)
