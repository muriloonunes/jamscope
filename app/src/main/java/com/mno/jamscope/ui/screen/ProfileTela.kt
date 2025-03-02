package com.mno.jamscope.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.mno.jamscope.R
import com.mno.jamscope.ui.components.LoadTrackInfo
import com.mno.jamscope.ui.components.ShowErrorMessage
import com.mno.jamscope.ui.components.TrackImageLoader
import com.mno.jamscope.ui.viewmodel.ProfileViewModel
import com.mno.jamscope.util.Stuff.openUrl
import com.mno.jamscope.util.forwardingPainter
import com.mno.jamscope.util.getCountryFlag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTela(
    listState: LazyListState
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val context = LocalContext.current
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val refreshing by profileViewModel.isRefreshing.collectAsStateWithLifecycle()
    val userRecentTracks by profileViewModel.recentTracks.collectAsStateWithLifecycle()
    var imagePfp by remember { mutableStateOf<Any?>(R.drawable.baseline_account_circle_24) }
    val errorMessage by profileViewModel.errorMessage.collectAsStateWithLifecycle()
    val playingAnimationEnabled by profileViewModel.playingAnimationToggle.collectAsState()

    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            imagePfp = userProfile!!.imageUrl
        }
    }

    PullToRefreshBox(
        isRefreshing = refreshing,
        onRefresh = {
            profileViewModel.onRefresh()
        }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Crossfade(
                    targetState = imagePfp,
                    animationSpec = tween(durationMillis = 500)
                ) { currentImage ->
                    if (currentImage is String) {
                        AsyncImage(
                            model = currentImage,
                            contentDescription = stringResource(
                                R.string.profile_pic_description,
                                userProfile?.username ?: "User"
                            ),
                            error = forwardingPainter(
                                painter = painterResource(id = R.drawable.baseline_account_circle_24),
                                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                            ),
                            placeholder = painterResource(R.drawable.baseline_account_circle_24),
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp)
                                .clip(CircleShape)
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
                                .size(120.dp)
                                .padding(8.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                if (userProfile != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { context.openUrl(userProfile!!.profileUrl!!) }
                        ) {
                            val text = if (userProfile!!.realname.isNotEmpty()) {
                                buildAnnotatedString {
                                    withStyle(
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                                            .toSpanStyle()
                                    ) {
                                        append(userProfile!!.realname)
                                    }
                                    append(" • ")
                                    withStyle(
                                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            .toSpanStyle()
                                    ) {
                                        append(userProfile!!.username)
                                    }
                                }
                            } else {
                                buildAnnotatedString {
                                    append(userProfile!!.username)
                                }
                            }
                            Text(
                                text = text,
                                textAlign = TextAlign.Center,
                                textDecoration = TextDecoration.Underline,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = stringResource(R.string.open_user_profile),
                                modifier = Modifier
                                    .size(10.dp)
                            )
                        }
                        if (userProfile?.subscriber == 1) {
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
                        userProfile?.country?.takeIf { it.isNotEmpty() && it != "None" }
                            ?.let { country ->
                                Text(
                                    text = "$country ${getCountryFlag(country)}",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                )
                            }
                        userProfile?.playcount?.let { playcount ->
                            Text(
                                text = stringResource(R.string.scrobbles, playcount),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
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
                    userRecentTracks,
                    key = { index, track -> "$index${track.name}" }) { index, track ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val imageUrl = track.image?.firstOrNull { it.size == "medium" }?.url ?: ""
                        TrackImageLoader(imageUrl = imageUrl)
                        LoadTrackInfo(track = track, forExtended = true, playingAnimationEnabled = playingAnimationEnabled)
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
                            modifier = Modifier.clickable {
                                profileViewModel.openSong(
                                    context,
                                    userProfile
                                )
                            }
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
    }
}
