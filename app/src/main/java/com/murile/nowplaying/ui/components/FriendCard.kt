package com.murile.nowplaying.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import com.murile.nowplaying.util.dateStringFormatter

@Composable
fun FriendCard(
    friend: User,
    recentTracks: RecentTracks?,
    modifier: Modifier,
    friendsViewModel: FriendsViewModel
) {
    val backgroundColor =
        friendsViewModel.getSecondaryContainerColor(friend.url, isSystemInDarkTheme())

    ElevatedCard(
        modifier = modifier
            .padding(8.dp),
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
            FriendImage(friend)
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
                        Column {
                            Text(
                                text = track.name,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                modifier = Modifier
                                    .width(750.dp)
                                    .basicMarquee()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val albumNameWidth =
                                    if (track.dateInfo?.formattedDate == null) 250.dp else 200.dp

                                Text(
                                    text = track.album.name.ifEmpty { stringResource(R.string.unknown_album) },
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    ),
                                    maxLines = 1,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .width(albumNameWidth)
                                        .basicMarquee()
                                )

                                if (track.dateInfo?.formattedDate == null) {
                                    // O usuário está ouvindo no momento
                                    NowPlayingAnimation()
                                } else {
                                    Text(
                                        text = dateStringFormatter(track.dateInfo.formattedDate, false, null),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontSize = 13.sp
                                        ),
                                        maxLines = 1,
                                        modifier = Modifier.align(Alignment.CenterVertically)
                                    )
                                }
                            }

                            Text(
                                text = track.artist.name,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                maxLines = 1,
                                modifier = Modifier.basicMarquee()
                            )
                        }
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
private fun FriendImage(friend: User) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(start = 4.dp, end = 12.dp)
    ) {
        AsyncImage(
            model = friend.image.firstOrNull { it.size == "large" }?.url ?: "",
            contentDescription = friend.name?.let {
                stringResource(
                    R.string.profile_pic_description,
                    it
                )
            },
            error = rememberVectorPainter(image = Icons.Default.AccountCircle),
            placeholder = ColorPainter(color = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp)
                .fillMaxSize(0.3f)
        )
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
}