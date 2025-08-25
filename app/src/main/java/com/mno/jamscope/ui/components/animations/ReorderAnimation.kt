package com.mno.jamscope.ui.components.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun AppearingItem(
    itemKey: Any,
    durationMillis: Int = 300,
    content: @Composable () -> Unit,
) {
    var appeared by remember(itemKey) { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (appeared) 1f else 0f,
        animationSpec = tween(durationMillis, easing = FastOutSlowInEasing),
        label = "alphaAnim"
    )

    LaunchedEffect(itemKey) {
        appeared = true
    }

    Box(Modifier.graphicsLayer { this.alpha = alpha }) {
        content()
    }
}

@Composable
fun ReorderAnimatedItem(
    itemKey: Any,
    durationMillis: Int = 250,
    content: @Composable () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember(itemKey) { Animatable(0f) }
    var lastYInRoot by remember(itemKey) { mutableStateOf<Float?>(null) }

    Box(
        Modifier
            .onGloballyPositioned { coords ->
                val y = coords.positionInRoot().y
                val prev = lastYInRoot
                lastYInRoot = y
                if (prev != null) {
                    val dy = prev - y
                    if (abs(dy) > 1f) {
                        scope.launch {
                            offsetY.snapTo(dy)
                            offsetY.animateTo(
                                0f,
                                animationSpec = tween(
                                    durationMillis,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
                    }
                }
            }
            .graphicsLayer { translationY = offsetY.value }
    ) {
        content()
    }

}
