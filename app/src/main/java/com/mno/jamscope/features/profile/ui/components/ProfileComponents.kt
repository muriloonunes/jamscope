package com.mno.jamscope.features.profile.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.ui.components.FullscreenImage
import com.mno.jamscope.ui.components.LastProBadge
import com.mno.jamscope.ui.components.LoadTrackInfo
import com.mno.jamscope.ui.components.ShowErrorMessage
import com.mno.jamscope.ui.components.TrackImageLoader
import com.mno.jamscope.ui.components.animations.shimmerAnimation
import com.mno.jamscope.util.forwardingPainter
import com.mno.jamscope.util.getCountryFlag
import com.mno.jamscope.util.getLocalizedCountryName
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileHeaderSection(
    modifier: Modifier = Modifier,
    imagePfp: Any?,
    username: String?,
    realName: String?,
    subscriber: Int?,
    profileUrl: String?,
    country: String?,
    playcount: Long?,
    windowSizeClass: WindowSizeClass,
) {
    val windowHeight = windowSizeClass.windowHeightSizeClass
    when (windowHeight) {
        WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.EXPANDED -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Crossfade(
                    targetState = imagePfp,
                    animationSpec = tween(durationMillis = 500)
                ) { currentImage ->
                    ProfileImage(
                        currentImage = currentImage,
                        username = username,
                        size = 120.dp,
                        shape = CircleShape,
                        isLastPro = subscriber == 1
                    )
                }
                if (username != null) {
                    ProfileInfo(
                        modifier = Modifier
                            .fillMaxWidth(),
                        username = username,
                        realName = realName,
                        profileUrl = profileUrl,
                        subscriber = subscriber,
                        country = country,
                        playcount = playcount
                    )
                }
            }
        }

        WindowHeightSizeClass.COMPACT -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Crossfade(
                    targetState = imagePfp,
                    animationSpec = tween(durationMillis = 500)
                ) { currentImage ->
                    ProfileImage(
                        currentImage = currentImage,
                        username = username,
                        size = 160.dp,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
                if (username != null) {
                    ProfileInfo(
                        modifier = Modifier
                            .fillMaxWidth(),
                        username = username,
                        realName = realName,
                        profileUrl = profileUrl,
                        subscriber = subscriber,
                        country = country,
                        playcount = playcount,
                        alignment = Alignment.CenterHorizontally,
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileTracksSection(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    errorMessage: String,
    userRecentTracks: List<Track>,
    playingAnimationEnabled: Boolean,
    onSeeMoreClick: () -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        state = listState
    ) {
        item {
            Spacer(Modifier.height(1.dp))
        }
        item {
            if (errorMessage.isNotEmpty()) {
                ShowErrorMessage(errorMessage)
            }
        }
        itemsIndexed(
            items = userRecentTracks,
            key = { index, track -> "$index${track.name}" }) { index, track ->
            val nowPlaying = track.dateInfo?.formattedDate == null
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                    .then(
                        if (nowPlaying) Modifier
                            .padding(2.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .shimmerAnimation()
                        else Modifier
                    )
            ) {
                val imageUrl = track.image?.firstOrNull { it.size == "medium" }?.url ?: ""
                val bigImageUrl = track.image?.firstOrNull { it.size == "extralarge" }?.url ?: ""
                TrackImageLoader(
                    imageUrl = imageUrl,
                    bigImageUrl = bigImageUrl,
                    nowPlaying = nowPlaying
                )
                LoadTrackInfo(
                    track = track,
                    clickable = true,
                    playingAnimationEnabled = playingAnimationEnabled,
                    nowPlaying = nowPlaying,
                    textColor = MaterialTheme.colorScheme.onSurface
                )
            }
            if (index < userRecentTracks.size - 1 && !nowPlaying) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
        if (userRecentTracks.isNotEmpty()) {
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
                    modifier = Modifier.clickable { onSeeMoreClick() }
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
}

@Composable
fun ProfileImage(
    currentImage: Any?,
    username: String?,
    size: Dp,
    shape: Shape,
    isLastPro: Boolean = false,
) {
    var showFullscreenImage by remember { mutableStateOf(false) }
    if (currentImage is String) {
        AsyncImage(
            model = currentImage,
            contentDescription = stringResource(
                R.string.profile_pic_description,
                username ?: "User"
            ),
            error = forwardingPainter(
                painter = painterResource(id = R.drawable.baseline_account_circle_24),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            ),
            placeholder = painterResource(R.drawable.baseline_account_circle_24),
            modifier = Modifier
                .size(size)
                .padding(8.dp)
                .clickable { showFullscreenImage = true }
                .then(
                    if (isLastPro) {
                        Modifier.border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = shape
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(shape)
        )
    } else {
        Image(
            painter = painterResource(currentImage as Int),
            contentDescription = stringResource(
                R.string.profile_pic_description,
                "User"
            ),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            modifier = Modifier
                .size(size)
                .padding(8.dp)
                .then(
                    if (isLastPro) {
                        Modifier.border(
                            width = 3.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = shape
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(shape)
        )
    }

    if (showFullscreenImage) {
        FullscreenImage(
            imageUrl = currentImage as String,
            placeholderUrl = "",
            contentDescription = username!!,
            onDismissRequest = { showFullscreenImage = false }
        )
    }
}

@Composable
fun ProfileInfo(
    modifier: Modifier = Modifier,
    username: String?,
    realName: String?,
    profileUrl: String?,
    subscriber: Int?,
    country: String?,
    playcount: Long?,
    alignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        val text = buildProfileTitleAnnotated(
            realName = realName,
            username = username,
            profileUrl = profileUrl
        )
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        if (subscriber == 1) {
            LastProBadge()
        }
        country?.takeIf { it.isNotEmpty() && it != "None" }
            ?.let { country ->
                Text(
                    text = "${getLocalizedCountryName(country)} ${getCountryFlag(country)}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }
        playcount?.let { userPlaycount ->
            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
            val playcount = numberFormat.format(userPlaycount)
            Text(
                text = stringResource(R.string.scrobbles, playcount),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
fun buildProfileTitleAnnotated(
    realName: String?,
    username: String?,
    profileUrl: String?
): AnnotatedString {
    return if (!realName.isNullOrEmpty()) {
        buildAnnotatedString {
            if (profileUrl != null) {
                withLink(LinkAnnotation.Url(url = profileUrl)) {
                    withStyle(
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        ).toSpanStyle()
                    ) {
                        append(realName)
                    }
                }
                append(" • ")
                withLink(LinkAnnotation.Url(url = profileUrl)) {
                    withStyle(
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.Underline
                        ).toSpanStyle()
                    ) {
                        append(username ?: "")
                    }
                }
            } else {
                append("$realName • ${username ?: ""}")
            }
        }
    } else {
        buildAnnotatedString {
            if (profileUrl != null) {
                withLink(LinkAnnotation.Url(url = profileUrl)) {
                    append(username ?: "User")
                }
            } else {
                append(username ?: "User")
            }
        }
    }
}