package com.murile.nowplaying.ui.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.murile.nowplaying.R
import com.murile.nowplaying.data.model.Track
import com.murile.nowplaying.util.dateStringFormatter

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
                .width(750.dp)
                .basicMarquee()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val albumNameWidth = if (!forExtended)
                (if (track.dateInfo?.formattedDate == null) 250.dp else 200.dp) else 190.dp

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
