package com.mno.jamscope.features.friends.ui.components

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.data.model.User
import com.mno.jamscope.ui.components.FullscreenImage
import com.mno.jamscope.ui.components.LastProBadge
import com.mno.jamscope.ui.components.LoadTrackInfo
import com.mno.jamscope.util.Stuff.openUrl
import com.mno.jamscope.util.forwardingPainter
import com.mno.jamscope.util.getCountryFlag
import com.mno.jamscope.util.getLocalizedCountryName
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun FriendImage(friend: User, forExtended: Boolean) {
    val size = if (forExtended) 100.dp else 50.dp
    val isPro = friend.subscriber == 1
    var showFullscreenImage by remember { mutableStateOf(false) }
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
            .size(size)
            .then(
                if (forExtended) {
                    Modifier
                        .clickable { showFullscreenImage = true }
                        .then(
                            if (isPro) {
                                Modifier.border(
                                    width = 4.dp,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape
                                )
                            } else {
                                Modifier
                            }
                        )
                } else {
                    Modifier
                }
            )
            .clip(CircleShape),
    )

    if (showFullscreenImage) {
        FullscreenImage(
            imageUrl = friend.image.firstOrNull { it.size == "extralarge" }?.url ?: "",
            placeholderUrl = friend.image.firstOrNull { it.size == "large" }?.url ?: "",
            contentDescription = friend.realname.ifEmpty { friend.name } ?: "",
            onDismissRequest = { showFullscreenImage = false }
        )
    }
}

@Composable
fun FriendExtendedCardTracksSection(
    modifier: Modifier = Modifier,
    friend: User,
    recentTracks: List<Track>,
    darkerBackgroundColor: Int,
    playingAnimationEnabled: Boolean,
    context: Context,
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(darkerBackgroundColor),
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.recent_tracks),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                )
            )
            if (recentTracks.isNotEmpty()) {
                val scrollState = rememberLazyListState()
                LazyColumnScrollbar(
                    state = scrollState,
                    settings = ScrollbarSettings(
                        thumbUnselectedColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        thumbSelectedColor = MaterialTheme.colorScheme.onSurface,
                        scrollbarPadding = 2.dp
                    ),
                ) {
                    LazyColumn(
                        state = scrollState,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            end = 8.dp,
                            top = 2.dp,
                            bottom = 2.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(recentTracks) { track ->
                            LoadTrackInfo(
                                track = track,
                                clickable = true,
                                playingAnimationEnabled = playingAnimationEnabled
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 5.dp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                    alpha = 0.5f
                                ),
                            )
                        }
                        item {
                            Icon(
                                imageVector = Icons.Filled.MoreHoriz,
                                contentDescription = stringResource(R.string.see_more),
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { context.openUrl("https://www.last.fm/user/${friend.name}/library?page=2") }
                            ) {
                                Text(
                                    text = stringResource(R.string.see_more),
                                    textDecoration = TextDecoration.Underline
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = stringResource(R.string.see_more),
                                    modifier = Modifier
                                        .size(12.dp)
                                        .padding(top = 2.dp)
                                )
                            }
                        }
                    }
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

@Composable
fun FriendExtendedCardHeader(
    modifier: Modifier = Modifier,
    friend: User,
    windowSizeClass: WindowSizeClass,
) {
    val windowHeight = windowSizeClass.windowHeightSizeClass
    when (windowHeight) {
        WindowHeightSizeClass.COMPACT -> {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .padding(8.dp),
            ) {
                FriendImage(friend, true)
                Spacer(modifier = Modifier.height(4.dp))
                if (friend.subscriber == 1) {
                    LastProBadge()
                }
                FriendInfo(
                    friend = friend
                )
            }
        }

        WindowHeightSizeClass.EXPANDED, WindowHeightSizeClass.MEDIUM -> {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .padding(8.dp),
            ) {
                FriendImage(friend, true)
                Spacer(modifier = Modifier.width(4.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FriendInfo(
                        friend = friend
                    )
                    if (friend.subscriber == 1) {
                        LastProBadge()
                    }
                }
            }
        }
    }
}

@Composable
fun FriendInfo(
    friend: User,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (friend.realname.isNotEmpty()) {
            val friendNameWithLink = buildAnnotatedString {
                withLink(LinkAnnotation.Url(url = friend.url)) {
                    append(friend.realname)
                }
            }
            Text(
                text = friendNameWithLink,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                maxLines = 1,
            )
        } else {
            friend.name?.let {
                val friendNameWithLink = buildAnnotatedString {
                    withLink(LinkAnnotation.Url(url = friend.url)) {
                        append(it)
                    }
                }
                Text(
                    text = friendNameWithLink,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    textDecoration = TextDecoration.Underline,
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                )
            }
        }
        if (friend.realname.isNotEmpty() && friend.name != null) {
            Text(
                text = friend.name,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center,
            )
        }
        friend.country?.takeIf { it.isNotEmpty() && it != "None" }?.let { country ->
            Text(
                text = "${getLocalizedCountryName(country)} ${getCountryFlag(country)}",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center,
            )
        }
    }
}