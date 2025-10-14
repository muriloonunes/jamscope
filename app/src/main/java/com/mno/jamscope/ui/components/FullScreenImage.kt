package com.mno.jamscope.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mno.jamscope.R
import com.mno.jamscope.ui.theme.JamscopePreviewTheme
import com.mno.jamscope.util.saveImageFromCoilCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenImage(
    modifier: Modifier = Modifier,
    imageUrl: String,
    placeholderUrl: String,
    contentDescription: String,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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
            //os elementos declarados por último são desenhados por cima dos anteriores,
            //então assim a foto nao vai impedir que a top bar seja clicável
            TopAppBar(
                title = { },
                modifier = Modifier.align(Alignment.TopCenter),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.3f),
                    actionIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { onDismissRequest() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch(Dispatchers.IO) {
                            val success = saveImageFromCoilCache(context, imageUrl)
                            withContext(Dispatchers.Main) {
                                val message =
                                    if (success) R.string.image_saved else R.string.image_not_saved
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = stringResource(R.string.save_image_button)
                        )
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun FullscreenImagePreview() {
    JamscopePreviewTheme(true) {
        FullscreenImage(
            imageUrl = "https://example.com/image.jpg",
            placeholderUrl = "https://example.com/placeholder.jpg",
            contentDescription = "Sample Image",
            onDismissRequest = {}
        )
    }
}
