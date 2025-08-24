package com.mno.jamscope.features.friends.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.features.friends.state.FriendsState
import com.mno.jamscope.features.friends.ui.components.FriendScreenTopAppBar
import com.mno.jamscope.ui.components.SortingBottomSheet
import com.mno.jamscope.ui.screen.JamPullToRefresh
import com.mno.jamscope.util.SortingType
import kotlinx.coroutines.delay

@ExperimentalMaterial3Api
@Composable
fun FriendsTela(
    state: FriendsState,
    onRefresh: () -> Unit,
    onSettingIconClick: () -> Unit,
    onSortIconClick: () -> Unit,
    onSortingTypeChange: (SortingType) -> Unit,
    onHideSortingSheet: () -> Unit,
    colorProvider: (String?, Boolean) -> Color,
    listState: LazyListState = LazyListState(),
    gridState: LazyGridState = LazyGridState(),
    windowSizeClass: WindowSizeClass,
    setTopBar: (@Composable () -> Unit) -> Unit?,
) {
    val windowWidth = windowSizeClass.windowWidthSizeClass
    val topAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    LaunchedEffect(state.sortingType) {
        delay(600)
        if (windowWidth == WindowWidthSizeClass.COMPACT) {
            listState.scrollToItem(0)
        } else {
            gridState.scrollToItem(0)
        }
    }

    setTopBar {
        FriendScreenTopAppBar(
            onSettingIconClick = onSettingIconClick,
            onSortIconClick = onSortIconClick,
            scrollBehavior = topAppBarScrollBehavior
        )
    }

    Column(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) {
        JamPullToRefresh(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh
        ) {
            when (windowWidth) {
                WindowWidthSizeClass.COMPACT -> {
                    FriendsVerticalScreen(
                        listState = listState,
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = state.errorMessage,
                        friends = state.friends,
                        recentTracksMap = state.recentTracksMap,
                        cardBackgroundToggle = state.cardBackgroundColorEnabled,
                        playingAnimationEnabled = state.playingAnimationEnabled,
                        colorProvider = { name, isDark -> colorProvider(name, isDark) }
                    )
                }

                WindowWidthSizeClass.EXPANDED, WindowWidthSizeClass.MEDIUM -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FriendsHorizontalScreen(
                            modifier = Modifier.fillMaxSize(),
                            gridState = gridState,
                            errorMessage = state.errorMessage,
                            friends = state.friends,
                            recentTracksMap = state.recentTracksMap,
                            cardBackgroundToggle = state.cardBackgroundColorEnabled,
                            playingAnimationEnabled = state.playingAnimationEnabled,
                            colorProvider = { name, isDark -> colorProvider(name, isDark) }
                        )
                    }
                }
            }
        }
    }

    if (state.isBottomSheetShown) {
        SortingBottomSheet(
            sortingType = state.sortingType,
            onSortingTypeChange = { onSortingTypeChange(it) },
            onDismissRequest = { onHideSortingSheet() }
        )
    }
}