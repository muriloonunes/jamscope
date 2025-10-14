package com.mno.jamscope.features.friends.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.User
import com.mno.jamscope.features.friends.ui.components.topBarHeight
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
    friendToExtend: String?,
    onExtendedHandled: () -> Unit,
    onRefresh: () -> Unit,
    onSettingIconClick: () -> Unit,
    onSortingTypeChange: (SortingType) -> Unit,
    colorProvider: (String?, Boolean) -> Color,
    listState: LazyListState = LazyListState(),
    gridState: LazyGridState = LazyGridState(),
    windowSizeClass: WindowSizeClass,
) {
    val windowWidth = windowSizeClass.windowWidthSizeClass

    JamPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        padding = topBarHeight
    ) {
        when (windowWidth) {
            WindowWidthSizeClass.COMPACT -> {
                FriendsVerticalScreen(
                    listState = listState,
                    modifier = Modifier
                        .fillMaxSize(),
                    errorMessage = errorMessage,
                    friends = friends,
                    recentTracksMap = recentTracks,
                    friendToExtend = friendToExtend,
                    onExtendedHandled = onExtendedHandled,
                    cardBackgroundToggle = cardBackgroundColorEnabled,
                    playingAnimationEnabled = playingAnimationEnabled,
                    colorProvider = { name, isDark -> colorProvider(name, isDark) },
                    sortingType = sortingType,
                    onSortingTypeChange = { onSortingTypeChange(it) },
                    onSettingIconClick = onSettingIconClick,
                )
            }

            WindowWidthSizeClass.EXPANDED, WindowWidthSizeClass.MEDIUM -> {
                FriendsHorizontalScreen(
                    modifier = Modifier
                        .fillMaxSize(),
                    gridState = gridState,
                    errorMessage = errorMessage,
                    sortingType = sortingType,
                    friends = friends,
                    recentTracksMap = recentTracks,
                    cardBackgroundToggle = cardBackgroundColorEnabled,
                    playingAnimationEnabled = playingAnimationEnabled,
                    friendToExtend = friendToExtend,
                    onExtendedHandled = onExtendedHandled,
                    colorProvider = { name, isDark -> colorProvider(name, isDark) },
                    onSortingTypeChange = { onSortingTypeChange(it) }
                )
            }
        }
    }


//    if (showBottomSheet) {
//        SortingBottomSheet(
//            currentSortingType = sortingType,
//            onSortingTypeChange = {
//                onSortingTypeChange(it)
//                showBottomSheet = false
//            },
//            onDismissRequest = { showBottomSheet = false }
//        )
//    }
}