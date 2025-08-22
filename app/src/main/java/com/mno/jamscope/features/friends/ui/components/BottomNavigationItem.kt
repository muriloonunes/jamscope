package com.mno.jamscope.features.friends.ui.components

import androidx.compose.ui.graphics.vector.ImageVector
import com.mno.jamscope.ui.navigator.Destination

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val destination: Destination
)