package com.mno.jamscope.ui.components.animations

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay

fun Modifier.highlightIf(
    condition: Boolean,
    onExtendedHandled: () -> Unit,
    highlightColor: Color = Color.LightGray.copy(alpha = 0.4f),
    durationMillis: Int = 3500,
    animationMillis: Int = 500,
): Modifier = composed {
    var isHighlighted by remember { mutableStateOf(false) }
    val animatedColor by animateColorAsState(
        targetValue = if (isHighlighted) highlightColor else Color.Transparent,
        animationSpec = tween(animationMillis)
    )

    LaunchedEffect(condition) {
        if (condition) {
            isHighlighted = true
            delay(durationMillis.toLong())
            isHighlighted = false
            onExtendedHandled()
        } else {
            if (isHighlighted) isHighlighted = false
        }
    }

    this.background(animatedColor)
}