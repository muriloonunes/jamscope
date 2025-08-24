package com.mno.jamscope.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.util.Stuff.searchMusicIntent
import com.mno.jamscope.util.dateStringFormatter
import com.mno.jamscope.util.forwardingPainter

@Composable
fun LoadTrackInfo(
    track: Track,
    forExtended: Boolean,
    playingAnimationEnabled: Boolean
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.then(if (forExtended) Modifier.clickable { context.searchMusicIntent(track) } else Modifier)
    ) {
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
            Text(
                text = track.album.name.ifEmpty { stringResource(R.string.unknown_album) },
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f)
                    .basicMarquee()
            )

            if (track.dateInfo?.formattedDate == null) {
                // O usuário está ouvindo no momento
                if (playingAnimationEnabled) NowPlayingAnimation() else
                    Text(
                        text = stringResource(R.string.now_playing),
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1,
                        modifier = Modifier.align(Alignment.CenterVertically).wrapContentWidth()
                    )
            } else {
                Text(
                    text = dateStringFormatter(
                        track.dateInfo.formattedDate,
                        false,
                        null
                    ),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1,
                    modifier = Modifier.align(Alignment.CenterVertically).wrapContentWidth()
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
fun TrackImageLoader(
    imageUrl: String,
    bigImageUrl: String
) {
    var showFullscreenImage by remember { mutableStateOf(false) }
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        error = forwardingPainter(
            painter = painterResource(id = R.drawable.baseline_album_24),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        ),
        placeholder = ColorPainter(color = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .size(60.dp)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(16.dp))
            .clickable { showFullscreenImage = true },
        contentScale = ContentScale.Crop,
    )

    if (showFullscreenImage) {
        FullscreenImage(
            imageUrl = bigImageUrl,
            placeholderUrl = imageUrl,
            contentDescription = "",
            onDismissRequest = { showFullscreenImage = false }
        )
    }
}
