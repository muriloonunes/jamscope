package com.mno.jamscope.ui.components.animations

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.mno.jamscope.ui.theme.LocalThemePreference

@Composable
fun NowPlayingAnimation(
    nowPlaying: Boolean = false,
) {
    val themePreference = LocalThemePreference.current
    val isDarkTheme = when (themePreference) {
        1 -> false
        2 -> true
        else -> isSystemInDarkTheme()
    }
    var animationLink by remember { mutableStateOf("") }
    animationLink = if (isDarkTheme) {
        "https://lottie.host/42f20bd0-773a-4124-8ffa-e477b2f4092c/PzWxhKJYcH.json"
    } else {
        "https://lottie.host/ed0b7d19-44f1-41e3-999a-2f5fc099484f/7MYcgDJxA6.json"
    }
    val composition by rememberLottieComposition(LottieCompositionSpec.Url(animationLink))
    MaterialTheme.colorScheme.onPrimaryContainer
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                MaterialTheme.colorScheme.onPrimaryContainer.toArgb(),
                BlendModeCompat.SRC_ATOP
            ),
            keyPath = arrayOf("**")
        )
    )
    LottieAnimation(
        modifier = Modifier
            .background(Color.Unspecified)
            .size(22.dp)
            .wrapContentWidth()
            .then(if (nowPlaying) Modifier.padding(end = 2.dp) else Modifier),
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1.5f,
        dynamicProperties = if (nowPlaying) dynamicProperties else null,
    )
}