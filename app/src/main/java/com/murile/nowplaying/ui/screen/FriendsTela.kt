package com.murile.nowplaying.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.murile.nowplaying.R
import com.murile.nowplaying.ui.components.FriendCard
import com.murile.nowplaying.ui.components.ShowErrorMessage
import com.murile.nowplaying.ui.components.SortingBottomSheet
import com.murile.nowplaying.ui.viewmodel.FriendsViewModel
import kotlinx.coroutines.delay

@ExperimentalMaterial3Api
@Composable
fun FriendsTela(
    friendsViewModel: FriendsViewModel,
    listStates: LazyListState
) {
    val sortingType by friendsViewModel.sortingType.collectAsStateWithLifecycle()
    val refreshing by friendsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val errorMessage by friendsViewModel.errorMessage.collectAsStateWithLifecycle()
    val recentTracksMap by friendsViewModel.recentTracksMap.collectAsStateWithLifecycle()
    val friends by friendsViewModel.friends.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (friendsViewModel.shouldRefresh()) {
                    friendsViewModel.onRefresh()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        if (friendsViewModel.shouldRefresh()) {
            friendsViewModel.onRefresh()
        }
    }

    LaunchedEffect(friends) {
        delay(600)
        listStates.animateScrollToItem(0)
    }

    Column {
        IconButton(onClick = {
            showBottomSheet = true
        }, modifier = Modifier.align(Alignment.End)) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = stringResource(R.string.sort_button)
            )
        }
        PullToRefreshBox(
            isRefreshing = refreshing,
            onRefresh = {
                friendsViewModel.onRefresh()
            }
        ) {
            LazyColumn(
                state = listStates,
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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
                item {
                    if (errorMessage.isNotEmpty()) {
                        ShowErrorMessage(errorMessage)
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
