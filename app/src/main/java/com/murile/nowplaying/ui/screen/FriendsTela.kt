package com.murile.nowplaying.ui.screen

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.murile.nowplaying.ui.viewmodel.FriendsViewModel

@ExperimentalMaterial3Api
@Composable
fun FriendsTela(
    friendsViewModel: FriendsViewModel
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

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = {
            friendsViewModel.onRefresh()
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(friends) { friend ->
                FriendCard(
                    friend = friend,
                    recentTracks = recentTracksMap[friend.url]
                )
            }
            item {
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
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
    recentTracks: RecentTracks?
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
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
                horizontalAlignment = Alignment.Start
            ) {
                recentTracks?.track?.firstOrNull()?.let { track ->
                    Text(
                        text = track.name,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        modifier = Modifier
                            .width(750.dp)
                            .basicMarquee()
                    )
                    Row {
                        Text(
                            text = track.album.name.ifEmpty { stringResource(R.string.unknown_album) },
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .width(450.dp)
                                .basicMarquee()
                        )
                        Text(
                            text = track.dateInfo?.formattedDate ?: "Agora",
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Text(
                        text = track.artist.name,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        )
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

//@Preview
//@Composable
//fun PreviewCard() {
//    FriendCard(
//        User(
//            "name",
//            listOf(
//                Image(
//                    "large",
//                    "https://lastfm.freetls.fastly.net/i/u/174s/f68af57cbd36cbbed04438292681b38f.png"
//                )
//            ),
//            "url",
//            "country",
//            "",
//            0,
//            1,
//            RecentTracks(
//                listOf(
//                    Track(
//                        Artist("Taylor Swift"),
//                        listOf(
//                            Image(
//                                "large",
//                                "https://lastfm.freetls.fastly.net/i/u/174s/f68af57cbd36cbbed04438292681b38f.png"
//                            )
//                        ),
//                        Album("Red"),
//                        "All Too Well",
//                    )
//                )
//            )
//        ), friendsViewModel = friends
//    )
//}