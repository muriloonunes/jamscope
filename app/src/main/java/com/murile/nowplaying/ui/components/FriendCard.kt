package com.murile.nowplaying.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.murile.nowplaying.R
import com.murile.nowplaying.data.model.RecentTracks
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.ui.viewmodel.FriendsViewModel
import com.murile.nowplaying.util.forwardingPainter

@Composable
fun FriendCard(
    friend: User,
    recentTracks: RecentTracks?,
    modifier: Modifier,
    friendsViewModel: FriendsViewModel
) {
    var isExtended by remember { mutableStateOf(false) }
    val backgroundColor =
        friendsViewModel.getSecondaryContainerColor(friend.url, isSystemInDarkTheme())

    if (isExtended) {
        ExtendedFriendCard(
            friend = friend,
            backgroundColor = backgroundColor,
            onDismissRequest = { isExtended = false }
        )
    }

    ElevatedCard(
        modifier = modifier
            .padding(8.dp)
            .clickable { isExtended = !isExtended },
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 4.dp, end = 12.dp)
            ) {
                FriendImage(friend, false)
                friend.realname.ifEmpty { friend.name }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        modifier = Modifier
                            .width(55.dp)
                            .basicMarquee(
                                iterations = 5,
                                velocity = 20.dp
                            )
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                AnimatedContent(
                    targetState = recentTracks?.track?.firstOrNull(),
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { it })).togetherWith(
                            fadeOut(animationSpec = tween(300)) + slideOutVertically(targetOffsetY = { it })
                        )
                    },
                    label = "recentTracksTransition"
                ) { track ->
                    if (track != null) {
                        LoadTrackInfo(
                            track = track,
                            forExtended = false
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.no_recent_tracks),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendImage(friend: User, forExtended: Boolean) {
    val size = if (forExtended) 100.dp else 50.dp
    AsyncImage(
        model = friend.image.firstOrNull { it.size == "large" }?.url ?: "",
        contentDescription = friend.name?.let {
            stringResource(
                R.string.profile_pic_description,
                it
            )
        },
        error = forwardingPainter(
            painter = painterResource(id = R.drawable.baseline_account_circle_24),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        ),
        placeholder = ColorPainter(color = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = Modifier
            .clip(CircleShape)
            .size(size),
    )
}