package com.mno.jamscope.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.features.friends.ui.components.BottomNavigationItem
import com.mno.jamscope.ui.components.JamFloatingBottomBar
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.util.FriendsScreenCaller
import com.mno.jamscope.ui.util.ProfileScreenCaller
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JamHomeScaffold() {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
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
        )
    )
    val listState = remember { List(itemsBarList.size) { LazyListState() } }
    val coroutineScope = rememberCoroutineScope()
    val windowSizeClass = LocalWindowSizeClass.current

    Scaffold(
        floatingActionButton = {
            JamFloatingBottomBar(
                modifier = Modifier
                    .padding(horizontal = 4.dp),
                buttons = itemsBarList,
                selectedItemIndex = selectedItemIndex,
                onItemSelected = { index ->
                    selectedItemIndex = index
                    coroutineScope.launch {
                        if (index >= 0 && index < listState.size) {
                            listState[index].animateScrollToItem(0)
                        }
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            AnimatedContent(
                targetState = selectedItemIndex,
                transitionSpec = {
                    val durationMillis = 120
                    if (targetState > initialState) {
                        slideInHorizontally(
                            animationSpec = tween(durationMillis),
                            initialOffsetX = { fullWidth -> fullWidth }
                        ) togetherWith
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis),
                                    targetOffsetX = { fullWidth -> -fullWidth }
                                )
                    } else {
                        slideInHorizontally(
                            animationSpec = tween(durationMillis),
                            initialOffsetX = { fullWidth -> -fullWidth }
                        ) togetherWith
                                slideOutHorizontally(
                                    animationSpec = tween(durationMillis),
                                    targetOffsetX = { fullWidth -> fullWidth }
                                )
                    }
                },
                label = "Page transition"
            ) { targetPage ->
                when (targetPage) {
                    0 -> {
                        FriendsScreenCaller(
                            windowSizeClass = windowSizeClass,
                            listState = listState[selectedItemIndex]
                        )
                    }

                    1 -> {
                        ProfileScreenCaller(
                            listState = listState[selectedItemIndex]
                        )
                    }
                }
            }
        }
    }
}
