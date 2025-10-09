package com.mno.jamscope.features.profile.ui

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowSizeClass
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.features.profile.ui.components.CollapsingProfileTopBar
import com.mno.jamscope.features.profile.ui.components.ProfileHeaderSection
import com.mno.jamscope.features.profile.ui.components.ProfileTracksSection
import com.mno.jamscope.ui.components.bottomBarPadding
import com.mno.jamscope.ui.screen.JamPullToRefresh
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val windowHeight = windowSizeClass.windowHeightSizeClass
    val coroutineScope = rememberCoroutineScope()

    var imagePfp by remember { mutableStateOf<Any?>(R.drawable.baseline_account_circle_24) }
    LaunchedEffect(userProfile) {
        if (userProfile != null) {
            imagePfp = userProfile.imageUrl
        }
    }

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val minTopBarHeight = 16.dp + statusBarHeight
    val maxTopBarHeight = 124.dp

    val minTopBarHeightPx = with(density) { minTopBarHeight.toPx() }
    val maxTopBarHeightPx = with(density) { maxTopBarHeight.toPx() }

    val topBarHeight = remember { Animatable(maxTopBarHeightPx) }
    var collapseFraction by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(topBarHeight.value) {
        collapseFraction =
            1f - ((topBarHeight.value - minTopBarHeightPx) / (maxTopBarHeightPx - minTopBarHeightPx)).coerceIn(
                0f,
                1f
            )
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val scrollFactor = 0.5f
                val delta = available.y
                val isScrollingDown = delta < 0 //rolando pra baixo, a barra encolhe

                if (!isScrollingDown && (listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0 || isRefreshing)) {
                    return Offset.Zero
                }

                val previousHeight = topBarHeight.value
                val newHeight =
                    (previousHeight + delta * scrollFactor).coerceIn(
                        minTopBarHeightPx,
                        maxTopBarHeightPx
                    )
                val consumed = newHeight - previousHeight

                if (consumed.roundToInt() != 0) {
                    coroutineScope.launch {
                        topBarHeight.snapTo(newHeight)
                    }
                }

                val canConsumeScroll = !(isScrollingDown && newHeight == minTopBarHeightPx)
                return if (canConsumeScroll) Offset(0f, consumed) else Offset.Zero
            }

            override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
                return super.onPostFling(consumed, available)
            }
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val shouldExpand = topBarHeight.value > (minTopBarHeightPx + maxTopBarHeightPx) / 2
            val canExpand =
                listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0

            val targetValue = if (shouldExpand && canExpand) {
                maxTopBarHeightPx
            } else {
                minTopBarHeightPx
            }

            if (topBarHeight.value != targetValue) {
                coroutineScope.launch {
                    topBarHeight.animateTo(targetValue, spring(stiffness = Spring.StiffnessHigh))
                }
            }
        }
    }

    JamPullToRefresh(
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh() },
        enabled = topBarHeight.value == maxTopBarHeightPx
    ) {
        when (windowHeight) {
            WindowHeightSizeClass.MEDIUM, WindowHeightSizeClass.EXPANDED -> {
                //phones/portrait mode
                val currentTopBarHeightDp = with(density) { topBarHeight.value.toDp() }
                Box(
                    modifier = Modifier
                        .nestedScroll(nestedScrollConnection)
                        .fillMaxWidth()
                ) {
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
                        },
                        contentPadding = PaddingValues(
                            top = currentTopBarHeightDp,
                            bottom = bottomBarPadding
                        )
                    )
                    CollapsingProfileTopBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(currentTopBarHeightDp)
                            .background(MaterialTheme.colorScheme.surface),
                        imagePfp = imagePfp,
                        username = userProfile?.username,
                        realName = userProfile?.realname,
                        subscriber = userProfile?.subscriber,
                        profileUrl = userProfile?.profileUrl,
                        country = userProfile?.country,
                        playcount = userProfile?.playcount,
                        collapseFraction = collapseFraction,
                    )
                }
            }

            WindowHeightSizeClass.COMPACT -> {
                //tablets/landscape mode
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