package com.mno.jamscope.features.friends.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.window.core.layout.WindowHeightSizeClass
import com.google.android.material.color.MaterialColors
import com.mno.jamscope.data.model.User
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

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
    val windowSizeClass = LocalWindowSizeClass.current
    val windowHeight = windowSizeClass.windowHeightSizeClass
    val cardModifier = when (windowHeight) {
        WindowHeightSizeClass.COMPACT -> Modifier
            .fillMaxHeight(0.9f)
            .fillMaxWidth(0.7f)
        else -> Modifier
            .fillMaxHeight(0.7f)
            .fillMaxWidth(0.9f)
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ElevatedCard(
            modifier = cardModifier,
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 16.dp
            )
        ) {
            when (windowHeight) {
                WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.EXPANDED -> {
                    Column {
                        FriendExtendedCardHeader(
                            modifier = Modifier
                                .padding(8.dp),
                            friend = friend,
                            windowSizeClass = windowSizeClass
                        )
                        FriendExtendedCardTracksSection(
                            modifier = Modifier
                                .padding(4.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(8.dp)
                                .fillMaxWidth(),
                            friend = friend,
                            recentTracks = recentTracks,
                            darkerBackgroundColor = darkerBackgroundColor,
                            playingAnimationEnabled = playingAnimationEnabled,
                            context = context
                        )
                    }
                }

                WindowHeightSizeClass.COMPACT -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        FriendExtendedCardHeader(
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterVertically),
                            friend = friend,
                            windowSizeClass = windowSizeClass
                        )
                        FriendExtendedCardTracksSection(
                            modifier = Modifier
                                .padding(4.dp)
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(8.dp)
                                .fillMaxWidth(),
                            friend = friend,
                            recentTracks = recentTracks,
                            darkerBackgroundColor = darkerBackgroundColor,
                            playingAnimationEnabled = playingAnimationEnabled,
                            context = context
                        )
                    }
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