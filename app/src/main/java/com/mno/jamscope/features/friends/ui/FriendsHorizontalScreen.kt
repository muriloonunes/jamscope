package com.mno.jamscope.features.friends.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.features.friends.ui.components.FriendCard
import com.mno.jamscope.ui.components.ShowErrorMessage
import com.mno.jamscope.ui.components.SortingLazyRow
import com.mno.jamscope.ui.components.animations.highlightIf

@Composable
fun FriendsHorizontalScreen(
    modifier: Modifier,
    gridState: LazyGridState,
    errorMessage: String,
    sortingType: SortingType,
    friends: List<Friend>,
    recentTracksMap: Map<String, List<Track>?>,
    cardBackgroundToggle: Boolean,
    playingAnimationEnabled: Boolean,
    friendToExtend: String?,
    onExtendedHandled: () -> Unit,
    onSortingTypeChange: (SortingType) -> Unit,
    colorProvider: (String?, Boolean) -> Color,
) {
    LazyVerticalGrid(
        modifier = modifier,
        state = gridState,
        columns = GridCells.Adaptive(minSize = 310.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            SortingLazyRow(
                currentSortingType = sortingType,
                onSortingTypeChange = {
                    onSortingTypeChange(it)
                },
            )
        }
        if (errorMessage.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ShowErrorMessage(errorMessage)
            }
        }
        items(friends, key = { it.name }) { friend ->
            FriendCard(
                friend = friend,
                recentTracks = recentTracksMap[friend.profileUrl],
                modifier = Modifier
                    .highlightIf(
                        condition = friend.name == friendToExtend,
                        onExtendedHandled = onExtendedHandled
                    )
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
                friendToExtend = friendToExtend,
                onExtendedHandled = onExtendedHandled,
                colorProvider = colorProvider
            )
        }
    }
}