package com.mno.jamscope.features.friends.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.features.friends.viewmodel.FriendsViewModel
import com.mno.jamscope.ui.components.FriendScreenTopAppBar
import com.mno.jamscope.ui.components.SortingBottomSheet
import kotlinx.coroutines.delay

@ExperimentalMaterial3Api
@Composable
fun FriendsTela(
    friendsViewModel: FriendsViewModel,
    listState: LazyListState = LazyListState(),
    gridState: LazyGridState = LazyGridState(),
    windowSizeClass: WindowSizeClass
) {
    val sortingType by friendsViewModel.sortingType.collectAsStateWithLifecycle()
    val refreshing by friendsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val errorMessage by friendsViewModel.errorMessage.collectAsStateWithLifecycle()
    val recentTracksMap by friendsViewModel.recentTracksMap.collectAsStateWithLifecycle()
    val friends by friendsViewModel.friends.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }
    val windowWidth = windowSizeClass.windowWidthSizeClass

    LaunchedEffect(sortingType) {
        delay(600)
        if (windowWidth == WindowWidthSizeClass.COMPACT) {
            listState.scrollToItem(0)
        } else {
            gridState.scrollToItem(0)
        }
    }

    Column {
        FriendScreenTopAppBar(
            onSettingIconClick = {
                friendsViewModel.navigateToSettings()
            },
            onSortIconClick = {
                showBottomSheet = true
            }
        )
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {
                friendsViewModel.onRefresh()
            }
        ) {
            when (windowWidth) {
                WindowWidthSizeClass.COMPACT -> {
                    FriendsVerticalScreen(
                        listState = listState,
                        modifier = Modifier.fillMaxSize(),
                        errorMessage = errorMessage,
                        friends = friends,
                        recentTracksMap = recentTracksMap,
                        friendsViewModel = friendsViewModel
                    )
                }

                WindowWidthSizeClass.EXPANDED, WindowWidthSizeClass.MEDIUM -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FriendsHorizontalScreen(
                            modifier = Modifier.fillMaxSize(),
                            gridState = gridState,
                            errorMessage = errorMessage,
                            friends = friends,
                            recentTracksMap = recentTracksMap,
                            friendsViewModel = friendsViewModel
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        SortingBottomSheet(
            sortingType = sortingType,
            onSortingTypeChanged = {
                friendsViewModel.onSortingTypeChanged(it)
                showBottomSheet = false
            },
            onDismissRequest = { showBottomSheet = false }
        )
    }
}