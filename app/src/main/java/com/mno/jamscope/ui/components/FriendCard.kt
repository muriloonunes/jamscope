package com.mno.jamscope.ui.components

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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mno.jamscope.R
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.User
import com.mno.jamscope.ui.theme.LocalThemePreference

@Composable
fun FriendCard(
    friend: User,
    recentTracks: RecentTracks?,
    modifier: Modifier,
    cardBackgroundToggle: Boolean,
    playingAnimationEnabled: Boolean,
    colorProvider: (String?, Boolean) -> Color,
) {
    val themePreference = LocalThemePreference.current
    val isDarkTheme = when (themePreference) {
        0 -> isSystemInDarkTheme()
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }
    val backgroundColor = if (cardBackgroundToggle)
        colorProvider(
            friend.url,
            isDarkTheme
        ) else MaterialTheme.colorScheme.onSecondary

    var isExtended by remember { mutableStateOf(false) }

    if (isExtended) {
        ExtendedFriendCard(
            friend = friend,
            backgroundColor = backgroundColor,
            playingAnimationEnabled = playingAnimationEnabled,
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
                            forExtended = false,
                            playingAnimationEnabled = playingAnimationEnabled
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