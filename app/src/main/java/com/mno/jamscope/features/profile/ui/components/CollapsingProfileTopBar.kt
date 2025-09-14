package com.mno.jamscope.features.profile.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp

@Composable
fun CollapsingProfileTopBar(
    modifier: Modifier = Modifier,
    imagePfp: Any?,
    username: String?,
    realName: String?,
    subscriber: Int?,
    profileUrl: String?,
    country: String?,
    playcount: Long?,
    collapseFraction: Float,
) {
    val imageSize = lerp(136.dp, 64.dp, collapseFraction) // Anima o tamanho de 136dp para 64dp
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Crossfade(
                targetState = imagePfp,
                animationSpec = tween(durationMillis = 500)
            ) { currentImage ->
                ProfileImage(
                    currentImage = currentImage,
                    username = username,
                    size = imageSize,
                    shape = RoundedCornerShape(48.dp),
                    isLastPro = subscriber == 1
                )
            }
            if (username != null) {
                ProfileInfo(
                    modifier = Modifier
                        .fillMaxWidth(),
                    username = username,
                    realName = realName,
                    profileUrl = profileUrl,
                    subscriber = subscriber,
                    country = country,
                    playcount = playcount,
                    collapseFraction = collapseFraction,
                )
            }
        }
    }
}