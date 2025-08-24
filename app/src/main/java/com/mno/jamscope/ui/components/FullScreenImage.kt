package com.mno.jamscope.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.request.ImageRequest
import coil3.request.crossfade
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage

@Composable
fun FullscreenImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    placeholderUrl: String,
    contentDescription: String,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Black.copy(
                        alpha = 0.7f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            ZoomableAsyncImage(
                modifier = modifier
                    .fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .placeholderMemoryCacheKey(placeholderUrl)
                    .crossfade(1000)
                    .build(),
                contentDescription = contentDescription,
                contentScale = ContentScale.Fit,
                onClick = { onDismissRequest() }
            )
        }
    }
}