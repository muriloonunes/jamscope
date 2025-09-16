package com.mno.jamscope.features.friends.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.User
import com.mno.jamscope.features.friends.ui.components.FriendCard
import com.mno.jamscope.ui.components.ShowErrorMessage
import com.mno.jamscope.ui.components.SortingLazyRow
import com.mno.jamscope.ui.components.animations.highlightIf
import com.mno.jamscope.ui.components.bottomBarPadding
import com.mno.jamscope.util.SortingType

@Composable
fun FriendsVerticalScreen(
    listState: LazyListState,
    modifier: Modifier,
    errorMessage: String,
    friends: List<User>,
    recentTracksMap: Map<String, RecentTracks?>,
    cardBackgroundToggle: Boolean,
    playingAnimationEnabled: Boolean,
    friendToExtend: String?,
    onExtendedHandled: () -> Unit,
    colorProvider: (String?, Boolean) -> Color,
    showSortingRow: Boolean,
    sortingType: SortingType,
    onSortingTypeChange: (SortingType) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = bottomBarPadding)
    ) {
        item {
            //isso aqui impede o bug de scroll "infinito"
            Spacer(Modifier.height(1.dp))
        }
        item {
            if (errorMessage.isNotEmpty()) {
                ShowErrorMessage(errorMessage)
            }
        }
        item {
            if (showSortingRow) {
                SortingLazyRow(
                    currentSortingType = sortingType,
                    onSortingTypeChange = {
                        onSortingTypeChange(it)
                    },
                )
            }
        }
        items(friends, key = { it.name!! }) { friend ->
            FriendCard(
                modifier = Modifier
                    .highlightIf(friend.name == friendToExtend, onExtendedHandled)
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
                friend = friend,
                recentTracks = recentTracksMap[friend.url],
                cardBackgroundToggle = cardBackgroundToggle,
                playingAnimationEnabled = playingAnimationEnabled,
                friendToExtend = friendToExtend,
                onExtendedHandled = onExtendedHandled,
                colorProvider = colorProvider
            )
        }
    }
}