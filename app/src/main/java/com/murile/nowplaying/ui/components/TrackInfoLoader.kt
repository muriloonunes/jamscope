package com.murile.nowplaying.ui.components

import android.content.Context
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.memory.MemoryCache
import com.murile.nowplaying.R
import com.murile.nowplaying.data.model.Track
import com.murile.nowplaying.util.dateStringFormatter
import com.murile.nowplaying.util.forwardingPainter

@Composable
fun LoadTrackInfo(
    track: Track,
    forExtended: Boolean
) {
    Column {
        Text(
            text = track.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            modifier = Modifier
                .basicMarquee()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val albumNameWidth = remember(forExtended, track.dateInfo) {
                if (!forExtended)
                    if (track.dateInfo?.formattedDate == null) 250.dp else 200.dp
                else 190.dp
            }

            Text(
                text = track.album.name.ifEmpty { stringResource(R.string.unknown_album) },
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .width(albumNameWidth)
                    .basicMarquee()
            )

            if (track.dateInfo?.formattedDate == null) {
                // O usuário está ouvindo no momento
                NowPlayingAnimation()
            } else {
                Text(
                    text = dateStringFormatter(
                        track.dateInfo.formattedDate,
                        false,
                        null
                    ),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }

        Text(
            text = track.artist.name,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            maxLines = 1,
            modifier = Modifier.basicMarquee()
        )
    }
}

@Composable
fun TrackImageLoader(imageUrl: String, context: Context) {
    val imageLoader = remember {
        ImageLoader.Builder(context = context)
            .memoryCache {
                MemoryCache.Builder().maxSizePercent(percent = 0.25, context = context).build()
            }.diskCache {
                DiskCache.Builder().directory(context.cacheDir.resolve("image_cache"))
                    .maxSizeBytes(25 * 1024 * 1024).build()
            }.build()
    }
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        error = forwardingPainter(
            painter = painterResource(id = R.drawable.baseline_album_24),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        ),
        placeholder = ColorPainter(color = MaterialTheme.colorScheme.surfaceContainerHigh),
        imageLoader = imageLoader,
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .size(60.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
    )
}
