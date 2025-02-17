package com.mno.jamscope.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dotlottie.dlplayer.Mode
import com.lottiefiles.dotlottie.core.compose.ui.DotLottieAnimation
import com.lottiefiles.dotlottie.core.util.DotLottieSource

@Composable
fun NowPlayingAnimation() {
    var animationLink by remember { mutableStateOf("") }
    animationLink = if (isSystemInDarkTheme()) {
        "https://lottie.host/92b7a9c6-c3c0-446c-9cb6-34b09ae76738/wAmR8oO4I6.lottie"
    } else {
        "https://lottie.host/d399d4b2-8a4d-42b2-b607-a97adc69f352/SHKlLek83X.lottie"
    }
    DotLottieAnimation(
        source = DotLottieSource.Url(animationLink),
        autoplay = true,
        loop = true,
        speed = 1.5f,
        useFrameInterpolation = true,
        playMode = Mode.FORWARD,
        modifier = Modifier.background(Color.Unspecified).size(22.dp).wrapContentWidth()
    )
}