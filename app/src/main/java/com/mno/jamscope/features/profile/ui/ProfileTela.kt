package com.mno.jamscope.features.profile.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.features.profile.ui.components.ProfileHeaderSection
import com.mno.jamscope.features.profile.ui.components.ProfileTracksSection
import com.mno.jamscope.ui.screen.JamPullToRefresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTela(
    isRefreshing: Boolean,
    userProfile: Profile?,
    errorMessage: String,
    recentTracks: List<Track>,
    playingAnimationEnabled: Boolean,
    listState: LazyListState = rememberLazyListState(),
    windowSizeClass: WindowSizeClass,
    onRefresh: () -> Unit,
    onSeeMoreClick: (Context, Profile?) -> Unit,
    setTopBar: (@Composable () -> Unit) -> Unit? = {},
) {
    val context = LocalContext.current
    val windowHeight = windowSizeClass.windowHeightSizeClass

    var imagePfp by remember { mutableStateOf<Any?>(R.drawable.baseline_account_circle_24) }
    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            imagePfp = userProfile.imageUrl
        }
    }

    LaunchedEffect(Unit) {
        setTopBar { null }
    }

    JamPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh() }
    ) {
        when (windowHeight) {
            WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.EXPANDED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileHeaderSection(
                        modifier = Modifier
                            .fillMaxWidth(),
                        imagePfp = imagePfp,
                        username = userProfile?.username,
                        realName = userProfile?.realname,
                        subscriber = userProfile?.subscriber,
                        profileUrl = userProfile?.profileUrl,
                        country = userProfile?.country,
                        playcount = userProfile?.playcount,
                        windowSizeClass = windowSizeClass
                    )
                    ProfileTracksSection(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        listState = listState,
                        errorMessage = errorMessage,
                        userRecentTracks = recentTracks,
                        playingAnimationEnabled = playingAnimationEnabled,
                        onSeeMoreClick = {
                            onSeeMoreClick(
                                context,
                                userProfile
                            )
                        }
                    )
                }
            }

            WindowHeightSizeClass.COMPACT -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    ProfileHeaderSection(
                        modifier = Modifier
                            .weight(0.5f),
                        imagePfp = imagePfp,
                        username = userProfile?.username,
                        realName = userProfile?.realname,
                        subscriber = userProfile?.subscriber,
                        profileUrl = userProfile?.profileUrl,
                        country = userProfile?.country,
                        playcount = userProfile?.playcount,
                        windowSizeClass = windowSizeClass
                    )
                    ProfileTracksSection(
                        modifier = Modifier
                            .weight(2f)
                            .padding(8.dp),
                        listState = listState,
                        errorMessage = errorMessage,
                        userRecentTracks = recentTracks,
                        playingAnimationEnabled = playingAnimationEnabled,
                        onSeeMoreClick = {
                            onSeeMoreClick(
                                context,
                                userProfile
                            )
                        }
                    )
                }
            }
        }
    }
}