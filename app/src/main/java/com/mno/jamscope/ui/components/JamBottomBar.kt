package com.mno.jamscope.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mno.jamscope.features.friends.ui.components.BottomNavigationItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JamBottomBar(
    modifier: Modifier = Modifier,
    buttons: List<BottomNavigationItem>,
    selectedItemIndex: Int,
    onItemSelected: (index: Int) -> Unit,
) {
    FlexibleBottomAppBar(
        modifier = modifier,
        expandedHeight = 76.dp
    ) {
        buttons.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItemIndex,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                alwaysShowLabel = true,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JamFloatingBottomBar(
    modifier: Modifier = Modifier,
    buttons: List<BottomNavigationItem>,
    selectedItemIndex: Int,
    onItemSelected: (index: Int) -> Unit,
) {
    HorizontalFloatingToolbar(
        modifier = modifier.height(84.dp),
        expanded = true,
//        colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    ) {
        buttons.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index == selectedItemIndex,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                alwaysShowLabel = true,
            )
        }
    }
}
