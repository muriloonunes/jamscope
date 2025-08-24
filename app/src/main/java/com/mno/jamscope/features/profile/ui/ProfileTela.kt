package com.mno.jamscope.features.profile.ui

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.features.profile.state.ProfileState
import com.mno.jamscope.features.profile.ui.components.ProfileHeaderSection
import com.mno.jamscope.features.profile.ui.components.ProfileTracksSection
import com.mno.jamscope.ui.screen.JamPullToRefresh

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTela(
    listState: LazyListState,
    windowSizeClass: WindowSizeClass,
    state: ProfileState,
    onRefresh: () -> Unit,
    onSeeMoreClick: (Context, Profile?) -> Unit
) {
    val context = LocalContext.current
    val windowHeight = windowSizeClass.windowHeightSizeClass

    JamPullToRefresh(
        isRefreshing = state.isRefreshing,
        onRefresh = { onRefresh() }
    ) {
        when (windowHeight) {
            WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.EXPANDED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileHeaderSection(
                        modifier = Modifier
                            .fillMaxWidth(),
                        imagePfp = state.imagePfp,
                        userProfile = state.userProfile,
                        windowSizeClass = windowSizeClass
                    )
                    ProfileTracksSection(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        listState = listState,
                        errorMessage = state.errorMessage,
                        userRecentTracks = state.recentTracks,
                        playingAnimationEnabled = state.playingAnimationEnabled,
                        onSeeMoreClick = {
                            onSeeMoreClick(
                                context,
                                state.userProfile
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
                        imagePfp = state.imagePfp,
                        userProfile = state.userProfile,
                        windowSizeClass = windowSizeClass
                    )
                    ProfileTracksSection(
                        modifier = Modifier
                            .weight(2f)
                            .padding(8.dp),
                        listState = listState,
                        errorMessage = state.errorMessage,
                        userRecentTracks = state.recentTracks,
                        playingAnimationEnabled = state.playingAnimationEnabled,
                        onSeeMoreClick = {
                            onSeeMoreClick(
                                context,
                                state.userProfile
                            )
                        }
                    )
                }
            }
        }
    }
}