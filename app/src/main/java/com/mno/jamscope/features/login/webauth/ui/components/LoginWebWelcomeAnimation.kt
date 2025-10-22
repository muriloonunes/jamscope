package com.mno.jamscope.features.login.webauth.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.mno.jamscope.R
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@Composable
fun LoginWebWelcomeAnimation(
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val windowWidth = LocalWindowSizeClass.current.widthSizeClass
        val telaLarga = windowWidth == WindowWidthSizeClass.Expanded
        val imageWidth = if (telaLarga) maxWidth * 0.8f else maxWidth
        val lottieSize = if (telaLarga) imageWidth * 0.35f else imageWidth * 0.6f
        val yOffset = if (telaLarga) maxHeight * -0.36f else maxHeight * -0.25f
        val xOffset = if (telaLarga) maxWidth * 0.01f else maxWidth * 0.05f
        val composition by rememberLottieComposition(LottieCompositionSpec.Url("https://lottie.host/1562c008-dfd3-44f4-a84c-0af2e60c2387/mcSus4eY0G.lottie"))
        val dynamicProperties = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    MaterialTheme.colorScheme.primary.toArgb(),
                    BlendModeCompat.SRC_ATOP
                ),
                keyPath = arrayOf("**")
            )
        )
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            speed = 0.8f,
            dynamicProperties = dynamicProperties,
            modifier = Modifier
                .size(lottieSize)
                .offset(x = xOffset, y = yOffset)
        )
        Image(
            painter = painterResource(R.drawable.login_screen_svg),
            contentDescription = "Login Illustration",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.Center)
                .width(imageWidth),
        )
    }
}