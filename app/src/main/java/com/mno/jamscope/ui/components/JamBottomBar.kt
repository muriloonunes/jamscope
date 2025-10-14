package com.mno.jamscope.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
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

val bottomBarPadding = 98.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JamFloatingBottomBar(
    modifier: Modifier = Modifier,
    buttons: List<BottomNavigationItem>,
    selectedItemIndex: Int,
    onItemSelected: (index: Int) -> Unit,
) {
    val bottomBarHeight = 84.dp
    HorizontalFloatingToolbar(
        modifier = modifier.height(bottomBarHeight),
        expanded = true,
    ) {
        buttons.forEachIndexed { index, item ->
            //https://stackoverflow.com/a/71969206
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val sizeScale by animateFloatAsState(
                targetValue = if (isPressed) 0.7f else 1f,
                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
            )
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(),
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
                modifier = Modifier
                    .wrapContentSize()
                    .graphicsLayer(
                        scaleX = sizeScale,
                        scaleY = sizeScale
                    ),
                interactionSource = interactionSource
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun JamFloatingBottomBarPreview() {
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val buttons = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            destination = com.mno.jamscope.ui.navigator.Destination.FriendsScreen
        ),
        BottomNavigationItem(
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            destination = com.mno.jamscope.ui.navigator.Destination.ProfileScreen
        )
    )
    JamFloatingBottomBar(
        buttons = buttons,
        selectedItemIndex = selectedItemIndex,
        onItemSelected = { index ->
            selectedItemIndex = index
        }
    )
}
