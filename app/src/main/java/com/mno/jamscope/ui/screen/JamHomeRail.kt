package com.mno.jamscope.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mno.jamscope.R
import com.mno.jamscope.features.friends.ui.components.BottomNavigationItem
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.util.FriendsScreenCaller
import com.mno.jamscope.ui.util.ProfileScreenCaller
import com.mno.jamscope.ui.util.SettingsScreenCaller
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamHomeRail() {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val windowSizeClass = LocalWindowSizeClass.current
    val coroutineScope = rememberCoroutineScope()
    val itemsBarList = listOf(
        BottomNavigationItem(
            stringResource(R.string.friends),
            Icons.Filled.Group,
            Icons.Outlined.Group,
            Destination.FriendsScreen
        ),
        BottomNavigationItem(
            stringResource(R.string.profile),
            Icons.Filled.Person,
            Icons.Outlined.Person,
            Destination.ProfileScreen
        ),
        BottomNavigationItem(
            title = stringResource(R.string.settings),
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings,
            Destination.SettingsScreen
        )
    )
    val gridState = remember { List(itemsBarList.size) { LazyGridState() } }
    val listState = remember { List(itemsBarList.size) { LazyListState() } }

    Scaffold { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavigationRail(
                windowInsets = WindowInsets.statusBars
            ) {
                itemsBarList.forEachIndexed { index, item ->
                    NavigationRailItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                            selectedItemIndex = index
                            coroutineScope.launch {
                                if (selectedItemIndex == 0) {
                                    gridState[selectedItemIndex].animateScrollToItem(0)
                                } else if (selectedItemIndex == 1) {
                                    listState[selectedItemIndex].animateScrollToItem(0)
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (selectedItemIndex == index) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) },
                    )
                }
            }
            AnimatedContent(
                targetState = selectedItemIndex,
                transitionSpec = {
                    val durationMillis = 180
                    if (targetState > initialState) {
                        slideInVertically(
                            animationSpec = tween(
                                durationMillis = durationMillis,
                                easing = FastOutLinearInEasing
                            ),
                            initialOffsetY = { fullWidth -> fullWidth }
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = durationMillis)
                        ) togetherWith
                                slideOutVertically(
                                    animationSpec = tween(
                                        durationMillis = durationMillis,
                                        easing = FastOutLinearInEasing
                                    ),
                                    targetOffsetY = { fullWidth -> -fullWidth }
                                ) + fadeOut(
                            animationSpec = tween(durationMillis = durationMillis)
                        )
                    } else {
                        slideInVertically(
                            animationSpec = tween(
                                durationMillis = durationMillis,
                                easing = FastOutLinearInEasing
                            ),
                            initialOffsetY = { fullWidth -> -fullWidth }
                        ) + fadeIn(
                            animationSpec = tween(durationMillis = durationMillis)
                        ) togetherWith
                                slideOutVertically(
                                    animationSpec = tween(
                                        durationMillis = durationMillis,
                                        easing = FastOutLinearInEasing
                                    ),
                                    targetOffsetY = { fullWidth -> fullWidth }
                                ) + fadeOut(
                            animationSpec = tween(durationMillis = durationMillis)
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                Box(modifier = Modifier.fillMaxSize()) {
                    when (pageIndex) {
                        0 -> {
                            FriendsScreenCaller(
                                windowSizeClass = windowSizeClass,
                                gridState = gridState[selectedItemIndex],
                            )
                        }

                        1 -> {
                            ProfileScreenCaller(
                                listState = listState[selectedItemIndex],
                            )
                        }

                        2 -> {
                            SettingsScreenCaller()
                        }
                    }
                }
            }
        }
    }
}