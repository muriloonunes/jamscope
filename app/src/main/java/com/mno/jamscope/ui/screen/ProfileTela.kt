package com.mno.jamscope.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import com.mno.jamscope.R
import com.mno.jamscope.ui.components.ProfileHeaderSection
import com.mno.jamscope.ui.components.ProfileTracksSection
import com.mno.jamscope.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTela(
    listState: LazyListState,
    windowSizeClass: WindowSizeClass
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val context = LocalContext.current
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val refreshing by profileViewModel.isRefreshing.collectAsStateWithLifecycle()
    val userRecentTracks by profileViewModel.recentTracks.collectAsStateWithLifecycle()
    var imagePfp by remember { mutableStateOf<Any?>(R.drawable.baseline_account_circle_24) }
    val errorMessage by profileViewModel.errorMessage.collectAsStateWithLifecycle()
    val playingAnimationEnabled by profileViewModel.playingAnimationToggle.collectAsState()
    val windowHeight = windowSizeClass.windowHeightSizeClass

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
        when (windowHeight) {
            WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.EXPANDED -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ProfileHeaderSection(
                        modifier = Modifier
                            .fillMaxWidth(),
                        imagePfp = imagePfp,
                        userProfile = userProfile,
                        windowSizeClass = windowSizeClass
                    )
                    ProfileTracksSection(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        listState = listState,
                        errorMessage = errorMessage,
                        userRecentTracks = userRecentTracks,
                        playingAnimationEnabled = playingAnimationEnabled,
                        onTrackClick = {
                            profileViewModel.openSong(
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
                        userProfile = userProfile,
                        windowSizeClass = windowSizeClass
                    )
                    ProfileTracksSection(
                        modifier = Modifier
                            .weight(2f)
                            .padding(8.dp),
                        listState = listState,
                        errorMessage = errorMessage,
                        userRecentTracks = userRecentTracks,
                        playingAnimationEnabled = playingAnimationEnabled,
                        onTrackClick = {
                            profileViewModel.openSong(
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