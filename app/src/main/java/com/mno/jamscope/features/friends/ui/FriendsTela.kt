package com.mno.jamscope.features.friends.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.User
import com.mno.jamscope.features.friends.ui.components.FriendScreenTopAppBar
import com.mno.jamscope.ui.components.SortingBottomSheet
import com.mno.jamscope.ui.screen.JamPullToRefresh
import com.mno.jamscope.util.SortingType

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterial3Api
@Composable
fun FriendsTela(
    sortingType: SortingType,
    isRefreshing: Boolean,
    errorMessage: String,
    recentTracks: Map<String, RecentTracks?>,
    friends: List<User>,
    cardBackgroundColorEnabled: Boolean,
    playingAnimationEnabled: Boolean,
    onRefresh: () -> Unit,
    onSettingIconClick: () -> Unit,
    onSortingTypeChange: (SortingType) -> Unit,
    colorProvider: (String?, Boolean) -> Color,
    listState: LazyListState = LazyListState(),
    gridState: LazyGridState = LazyGridState(),
    windowSizeClass: WindowSizeClass,
    setTopBar: (@Composable () -> Unit) -> Unit?,
) {
    val windowWidth = windowSizeClass.windowWidthSizeClass
    val topAppBarScrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(sortingType) {
        if (windowWidth == WindowWidthSizeClass.COMPACT) {
            listState.animateScrollToItem(0)
        } else {
            gridState.scrollToItem(0)
        }
    }

    setTopBar {
        FriendScreenTopAppBar(
            onSettingIconClick = onSettingIconClick,
            onSortIconClick = { showBottomSheet = true },
            scrollBehavior = topAppBarScrollBehavior
        )
    }

    JamPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        when (windowWidth) {
            WindowWidthSizeClass.COMPACT -> {
                Column {
                    FriendsVerticalScreen(
                        listState = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        errorMessage = errorMessage,
                        friends = friends,
                        recentTracksMap = recentTracks,
                        cardBackgroundToggle = cardBackgroundColorEnabled,
                        playingAnimationEnabled = playingAnimationEnabled,
                        colorProvider = { name, isDark -> colorProvider(name, isDark) },
                    )
                }
            }

            WindowWidthSizeClass.EXPANDED, WindowWidthSizeClass.MEDIUM -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FriendsHorizontalScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        gridState = gridState,
                        errorMessage = errorMessage,
                        friends = friends,
                        recentTracksMap = recentTracks,
                        cardBackgroundToggle = cardBackgroundColorEnabled,
                        playingAnimationEnabled = playingAnimationEnabled,
                        colorProvider = { name, isDark -> colorProvider(name, isDark) }
                    )
                }
            }
        }
    }


    if (showBottomSheet) {
        SortingBottomSheet(
            sortingType = sortingType,
            onSortingTypeChange = {
                onSortingTypeChange(it)
                showBottomSheet = false
            },
            onDismissRequest = { showBottomSheet = false }
        )
    }
}