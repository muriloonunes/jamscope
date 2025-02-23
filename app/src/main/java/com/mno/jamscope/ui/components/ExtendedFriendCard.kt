package com.mno.jamscope.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.android.material.color.MaterialColors
import com.mno.jamscope.R
import com.mno.jamscope.data.model.User
import com.mno.jamscope.util.Stuff.openUrl
import com.mno.jamscope.util.getCountryFlag
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun ExtendedFriendCard(
    friend: User,
    backgroundColor: Color,
    playingAnimationEnabled: Boolean,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val recentTracks = friend.recentTracks?.track ?: emptyList()
    val darkerBackgroundColor = MaterialColors.harmonize(
        backgroundColor.toArgb(),
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f).toArgb()
    )
    Dialog(onDismissRequest = { onDismissRequest() }) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 16.dp
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                FriendImage(friend, true)
                Spacer(modifier = Modifier.height(4.dp))
                if (friend.subscriber == 1) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        Text(
                            text = stringResource(R.string.pro),
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { context.openUrl(friend.url) },
                    ) {
                        if (friend.realname.isNotEmpty()) {
                            Text(
                                text = friend.realname,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                ),
                                textAlign = TextAlign.Center,
                                textDecoration = TextDecoration.Underline,
                                maxLines = 1,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        } else {
                            friend.name?.let {
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                    ),
                                    textDecoration = TextDecoration.Underline,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = stringResource(R.string.open_user_profile),
                            modifier = Modifier
                                .size(18.dp)
                                .align(Alignment.CenterVertically)
                                .padding(start = 2.dp, top = 4.dp)
                        )
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
                            text = "$country ${getCountryFlag(country)}",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.recent_tracks),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                    )
                )
                if (recentTracks.isNotEmpty()) {
                    val scrollState = rememberLazyListState()
                    LazyColumnScrollbar(
                        modifier = Modifier
                            .background(
                                color = Color(darkerBackgroundColor),
                                shape = RoundedCornerShape(12.dp)
                            ),
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
                                LoadTrackInfo(track = track, forExtended = true, playingAnimationEnabled = playingAnimationEnabled)
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                        alpha = 0.5f
                                    )
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
                                    Text(text = stringResource(R.string.see_more), textDecoration = TextDecoration.Underline)
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
}

//https://stackoverflow.com/a/68056586
@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 8.dp,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 350 else 900

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRect(
                color = color,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha,
            )
        }
    }
}