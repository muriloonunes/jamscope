package com.murile.nowplaying.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.murile.nowplaying.R
import com.murile.nowplaying.data.model.RecentTracks
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.ui.components.AnimateListening
import com.murile.nowplaying.ui.theme.LocalThemeAttributes
import com.murile.nowplaying.ui.viewmodel.FriendsViewModel
import com.murile.nowplaying.util.dateFormatter
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@ExperimentalMaterial3Api
@Composable
fun FriendsTela(
    friendsViewModel: FriendsViewModel,
    listStates: LazyListState
) {
    val refreshing by friendsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val errorMessage by friendsViewModel.errorMessage.collectAsStateWithLifecycle()
    val recentTracksMap by friendsViewModel.recentTracksMap.collectAsStateWithLifecycle()
    val friends by friendsViewModel.friends.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (friendsViewModel.shouldRefresh()) {
            friendsViewModel.onRefresh()
        }
    }

    LaunchedEffect(friends) {
        delay(600)
        listStates.animateScrollToItem(0)
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
                        )
                )
            }
            item {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FriendCard(
    friend: User,
    recentTracks: RecentTracks?,
    modifier: Modifier
) {
    var albumNameWidth by remember { mutableStateOf(200.dp) }
    val themeAttributes = LocalThemeAttributes.current
    val index =
        (friend.name.hashCode().absoluteValue) % themeAttributes.allSecondaryContainerLightColors.size
    val backgroundColor = if (isSystemInDarkTheme()) {
        themeAttributes.allSecondaryContainerDarkColors.getOrElse(index) { Color.Unspecified }
    } else {
        themeAttributes.allSecondaryContainerLightColors.getOrElse(index) { Color.Unspecified }
    }

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
            Column(
                horizontalAlignment = Alignment.Start,
            ) {
                recentTracks?.track?.firstOrNull()?.let { track ->
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
                        albumNameWidth =
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
                            //signfica que o usuario esta ouvindo naquele momento
                            AnimateListening()
                        } else {
                            Text(
                                text = dateFormatter(track.dateInfo.formattedDate),
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
                        modifier = Modifier
                            .basicMarquee()
                    )
                } ?: Text(
                    text = stringResource(id = R.string.no_recent_tracks),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}