package com.mno.jamscope.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.util.forwardingPainter
import com.mno.jamscope.util.getCountryFlag
import com.mno.jamscope.util.getLocalizedCountryName
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ProfileHeaderSection(
    modifier: Modifier = Modifier,
    imagePfp: Any?,
    userProfile: Profile?,
    windowSizeClass: WindowSizeClass
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
                        username = userProfile?.username,
                        size = 120.dp,
                        shape = CircleShape,
                        isLastPro = userProfile?.subscriber == 1
                    )
                }
                if (userProfile != null) {
                    ProfileInfo(
                        modifier = Modifier
                            .fillMaxWidth(),
                        userProfile = userProfile
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
                        username = userProfile?.username,
                        size = 160.dp,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
                if (userProfile != null) {
                    ProfileInfo(
                        modifier = Modifier
                            .fillMaxWidth(),
                        userProfile = userProfile,
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
            if (errorMessage.isNotEmpty()) {
                ShowErrorMessage(errorMessage)
            }
        }
        itemsIndexed(
            items = userRecentTracks,
            key = { index, track -> "$index${track.name}" }) { index, track ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .animateItem()
            ) {
                val imageUrl = track.image?.firstOrNull { it.size == "medium" }?.url ?: ""
                TrackImageLoader(imageUrl = imageUrl)
                LoadTrackInfo(
                    track = track,
                    forExtended = true,
                    playingAnimationEnabled = playingAnimationEnabled
                )
            }
            if (index < userRecentTracks.size - 1) {
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
    isLastPro: Boolean = false
) {
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
}

@Composable
fun ProfileInfo(
    modifier: Modifier = Modifier,
    userProfile: Profile,
    alignment: Alignment.Horizontal = Alignment.Start,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        val text = if (userProfile.realname.isNotEmpty()) {
            buildAnnotatedString {
                withLink(LinkAnnotation.Url(url = userProfile.profileUrl!!)) {
                    withStyle(
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            textDecoration = TextDecoration.Underline
                        )
                            .toSpanStyle()
                    ) {
                        append(userProfile.realname)
                    }
                }
                append(" • ")
                withLink(LinkAnnotation.Url(url = userProfile.profileUrl!!)) {
                    withStyle(
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.Underline
                        )
                            .toSpanStyle()
                    ) {
                        append(userProfile.username)
                    }
                }
            }
        } else {
            buildAnnotatedString {
                withLink(LinkAnnotation.Url(url = userProfile.profileUrl!!)) {
                    append(userProfile.username)
                }
            }
        }
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
        if (userProfile.subscriber == 1) {
            LastProBadge()
        }
        userProfile.country?.takeIf { it.isNotEmpty() && it != "None" }
            ?.let { country ->
                Text(
                    text = "${getLocalizedCountryName(country)} ${getCountryFlag(country)}",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                )
            }
        userProfile.playcount?.let { userPlaycount ->
            val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
            val playcount = numberFormat.format(userPlaycount)
            Text(
                text = stringResource(R.string.scrobbles, playcount),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}