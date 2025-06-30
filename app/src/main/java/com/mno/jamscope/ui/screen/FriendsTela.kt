package com.mno.jamscope.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.User
import com.mno.jamscope.ui.components.FriendCard
import com.mno.jamscope.ui.components.FriendScreenTopAppBar
import com.mno.jamscope.ui.components.ShowErrorMessage
import com.mno.jamscope.ui.components.SortingBottomSheet
import com.mno.jamscope.ui.viewmodel.FriendsViewModel
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
        listState.animateScrollToItem(0)
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

@Composable
fun FriendsVerticalScreen(
    listState: LazyListState,
    modifier: Modifier,
    errorMessage: String,
    friends: List<User>,
    recentTracksMap: Map<String, RecentTracks?>,
    friendsViewModel: FriendsViewModel
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            if (errorMessage.isNotEmpty()) {
                ShowErrorMessage(errorMessage)
            }
        }
        items(friends, key = { it.name!! }) { friend ->
            FriendCard(
                friend = friend,
                recentTracks = recentTracksMap[friend.url],
                modifier = Modifier
                    .animateItem(
                        fadeInSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        ),
                        placementSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        ),
                        fadeOutSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    ),
                friendsViewModel = friendsViewModel
            )
        }
    }
}

@Composable
fun FriendsHorizontalScreen(
    gridState: LazyGridState,
    modifier: Modifier,
    errorMessage: String,
    friends: List<User>,
    recentTracksMap: Map<String, RecentTracks?>,
    friendsViewModel: FriendsViewModel
) {
    if (errorMessage.isNotEmpty()) {
        ShowErrorMessage(errorMessage)
    }
    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Adaptive(minSize = 310.dp),
    ) {
        items(friends, key = { it.name!! }) { friend ->
            FriendCard(
                friend = friend,
                recentTracks = recentTracksMap[friend.url],
                modifier = modifier
                    .animateItem(
                        fadeInSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        ),
                        placementSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        ),
                        fadeOutSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    ),
                friendsViewModel = friendsViewModel
            )
        }
    }
}