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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.murile.nowplaying.R
import com.murile.nowplaying.data.model.Album
import com.murile.nowplaying.data.model.Artist
import com.murile.nowplaying.data.model.Image
import com.murile.nowplaying.data.model.RecentTracks
import com.murile.nowplaying.data.model.Track
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.ui.viewmodel.FriendsViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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
    var albumNameWidth by remember { mutableStateOf(200.dp) }
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                            if (track.dateInfo?.formattedDate == null) 230.dp else 200.dp
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
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                contentDescription = stringResource(R.string.now_playing),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        } else {
                            Text(
                                text = formatData(track.dateInfo.formattedDate),
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
                        maxLines = 1
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

@Composable
private fun formatData(dataIso: String): String {
    val dataFormatada = ZonedDateTime.parse(dataIso)
    val currentTime = ZonedDateTime.now()
    val duration = java.time.Duration.between(dataFormatada, currentTime)

    return when {
        //menos de  1hora
        duration.abs().toMinutes() < 60 -> stringResource(
            R.string.minutes_ago,
            duration.abs().toMinutes()
        )
        //entre 1 e 2 horas
        duration.toMinutes() in 60 until 120 -> stringResource(R.string.one_hour_ago)
        // menos de 24h
        duration.toMinutes() in 120 until 1440 -> {
            val hours = duration.toHours()
            stringResource(R.string.hours_ago, hours)
        }
        // entr 1 e 2 dias
        duration.toDays() in 1 until 2 -> stringResource(R.string.one_day_ago)
        // menos de 1 mes
        duration.toDays() in 2 until 30 -> {
            val days = duration.toDays()
            stringResource(R.string.days_ago, days)
        }

        else -> {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .withLocale(Locale.getDefault())
            val formattedDate = dataFormatada.format(formatter)
            formattedDate
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFriendCard() {
    val sampleUser = User(
        name = "Sample User",
        image = listOf(Image(size = "large", url = "")),
        url = "http://sample.url",
        realname = "Sample Realname",
        country = "Sample Country",
        playcount = 12345,
        subscriber = 1,
        recentTracks = RecentTracks(
            track = listOf(
                Track(
                    artist = Artist(name = "Sample Artist"),
                    image = listOf(Image(size = "large", url = "")),
                    album = Album(name = "Sample Album"),
                    name = "Sample Track",
                    dateInfo = null
//                    dateInfo = DateInfo(timestamp = "1616161616", formattedDate = "2025-01-27T21:33:00-03:00")
                )
            )
        )
    )
    FriendCard(friend = sampleUser, recentTracks = sampleUser.recentTracks)
}