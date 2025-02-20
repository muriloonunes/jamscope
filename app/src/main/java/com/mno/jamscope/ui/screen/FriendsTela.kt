package com.mno.jamscope.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mno.jamscope.R
import com.mno.jamscope.ui.components.FriendCard
import com.mno.jamscope.ui.components.ShowErrorMessage
import com.mno.jamscope.ui.components.SortingBottomSheet
import com.mno.jamscope.ui.viewmodel.FriendsViewModel
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

    LaunchedEffect(sortingType) {
        delay(600)
        listStates.animateScrollToItem(0)
    }

    Column {
        Row(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = {
                friendsViewModel.navigateToSettings()
                }, modifier = Modifier.align(Alignment.CenterVertically )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.open_settings_screen)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                showBottomSheet = true
            }, modifier = Modifier.align(Alignment.CenterVertically)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = stringResource(R.string.sort_button)
                )
            }
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
