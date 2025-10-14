package com.mno.jamscope.ui.screen

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JamPullToRefresh(
    modifier: Modifier = Modifier,
    onRefresh: () -> Unit,
    state: PullToRefreshState = rememberPullToRefreshState(),
    isRefreshing: Boolean,
    enabled: Boolean = true,
    padding: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit,
) {
    //https://composables.com/docs/androidx.compose.material3/material3/components/ContainedLoadingIndicator
    //also thanks pano scrobbler
    val scaleFraction = {
        if (isRefreshing) 1f
        else LinearOutSlowInEasing.transform(state.distanceFraction).coerceIn(0f, 1f)
    }
    Box(
        modifier = modifier
            .pullToRefresh(state = state, isRefreshing = isRefreshing, onRefresh = onRefresh, enabled = enabled)
    ) {
        content()
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .padding(padding)
                .graphicsLayer {
                    scaleX = scaleFraction()
                    scaleY = scaleFraction()
                }
        ) {
            PullToRefreshDefaults.LoadingIndicator(state = state, isRefreshing = isRefreshing)
        }
    }
}