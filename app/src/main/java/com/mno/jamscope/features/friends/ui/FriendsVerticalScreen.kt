package com.mno.jamscope.features.friends.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.User
import com.mno.jamscope.ui.components.FriendCard
import com.mno.jamscope.ui.components.ShowErrorMessage

@Composable
fun FriendsVerticalScreen(
    listState: LazyListState,
    modifier: Modifier,
    errorMessage: String,
    friends: List<User>,
    recentTracksMap: Map<String, RecentTracks?>,
    cardBackgroundToggle: Boolean,
    playingAnimationEnabled: Boolean,
    colorProvider: (String?, Boolean) -> Color,
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
                cardBackgroundToggle = cardBackgroundToggle,
                playingAnimationEnabled = playingAnimationEnabled,
                colorProvider = colorProvider
            )
        }
    }
}