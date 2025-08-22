package com.mno.jamscope.features.friends.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.User
import com.mno.jamscope.features.friends.viewmodel.FriendsViewModel
import com.mno.jamscope.ui.components.FriendCard
import com.mno.jamscope.ui.components.ShowErrorMessage

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