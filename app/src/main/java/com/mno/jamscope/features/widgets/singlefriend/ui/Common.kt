package com.mno.jamscope.features.widgets.singlefriend.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.mno.jamscope.R
import com.mno.jamscope.data.model.User
import com.mno.jamscope.features.widgets.WidgetDataStoreManager
import com.mno.jamscope.features.widgets.singlefriend.WidgetRefreshAction
import com.mno.jamscope.util.dateStringFormatter

@Composable
fun ProfileWidgetImage(
    friend: User?, imageBitmap: Bitmap?,
) {
    val imageDescription = friend?.name?.let {
        LocalContext.current.getString(R.string.profile_pic_description, it)
    } ?: LocalContext.current.getString(R.string.no_friend_selected)
    imageBitmap?.let {
        Box(
            modifier = GlanceModifier.padding(8.dp)
        ) {
            Image(
                provider = ImageProvider(it),
                contentDescription = imageDescription,
                modifier = GlanceModifier.cornerRadius(50.dp).size(60.dp)
            )
        }
    } ?: run {
        Image(
            provider = ImageProvider(R.drawable.baseline_account_circle_24),
            contentDescription = imageDescription,
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface),
            modifier = GlanceModifier.padding(14.dp)
        )
    }
}

@Composable
fun FriendWidgetInfo(
    friend: User?,
    textStyle: TextStyle,
    context: Context,
    forSmall: Boolean,
) {
    val horizontalAlignment = if (forSmall) Alignment.Start else Alignment.CenterHorizontally
    val modifier = if (forSmall) {
        GlanceModifier.padding(start = 8.dp, end = 2.dp)
    } else {
        GlanceModifier
    }
    Column(
        horizontalAlignment = horizontalAlignment,
        modifier = modifier
    ) {
        when {
            friend == null -> NoFriendSelected(textStyle)
            friend.recentTracks == null -> NoRecentTracks(textStyle, context)
            else -> FriendListeningDetails(friend, textStyle, context, forSmall)
        }
    }
}

@Composable
private fun NoFriendSelected(textStyle: TextStyle) {
    Text(
        text = LocalContext.current.getString(R.string.no_friend_selected),
        style = textStyle,
        maxLines = 1
    )
}

@Composable
private fun NoRecentTracks(textStyle: TextStyle, context: Context) {
    Text(
        text = context.getString(R.string.no_recent_tracks), style = textStyle, maxLines = 1
    )
}

@Composable
private fun FriendListeningDetails(
    friend: User,
    textStyle: TextStyle,
    context: Context,
    forSmall: Boolean,
) {
    val track = friend.recentTracks?.track?.firstOrNull()

    Text(
        text = context.getString(
            R.string.friend_listening_to,
            friend.realname.ifEmpty { friend.name!! }),
        style = textStyle.copy(
            fontWeight = FontWeight.Bold
        ),
        maxLines = 1,
    )

    track?.let { it ->
        val dateString = track.dateInfo?.formattedDate?.let {
            dateStringFormatter(it, true, context)
        } ?: context.getString(R.string.now)

        if (forSmall) {
//            Text(text = it.name, style = textStyle, maxLines = 1)
            Text(
                text = context.getString(R.string.song_by_artist, it.name, it.artist.name),
                style = textStyle,
                maxLines = 1
            )
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    //·
//                    text = "${it.album.name} — ${it.artist.name}",
                    text = it.album.name,
                    style = textStyle,
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight()
                )

                DateText(dateString)
            }
        } else {
            Text(
                text = context.getString(R.string.song_by_artist, it.name, it.artist.name),
                style = textStyle,
                maxLines = 1
            )
//            Text(text = it.artist.name, style = textStyle, maxLines = 1)
            Text(text = it.album.name, style = textStyle, maxLines = 1)
            DateText(dateString, false)
        }
        LastUpdatedManager(forSmall)
    } ?: NoRecentTracks(textStyle, context)
}

@Composable
private fun DateText(
    dateString: String,
    forSmall: Boolean = true,
) {
    Text(
        text = dateString,
        style = TextStyle(
            color = GlanceTheme.colors.onSurfaceVariant,
            fontSize = if (!forSmall) 16.sp else 13.sp
        ),
        maxLines = 1,
        modifier = GlanceModifier.wrapContentWidth()
    )
}

@Composable
private fun LastUpdatedManager(
    forSmall: Boolean,
) {
    val lastUpdated = WidgetDataStoreManager.getLastUpdated(currentState())
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (lastUpdated.isEmpty()) LocalContext.current.getString(R.string.never_updated)
            else LocalContext.current.getString(
                R.string.last_updated_on,
                lastUpdated
            ),
            style = TextStyle(GlanceTheme.colors.onSurfaceVariant, fontSize = 13.sp),
            maxLines = 1,
            modifier = if (forSmall) GlanceModifier.defaultWeight() else GlanceModifier
        )
        if (!forSmall) {
            Spacer(GlanceModifier.width(12.dp))
        }
        Image(
            provider = ImageProvider(R.drawable.round_refresh_24),
            contentDescription = LocalContext.current.getString(R.string.refresh_button),
            colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
            modifier = GlanceModifier
                .size(20.dp)
                .clickable(onClick = actionRunCallback<WidgetRefreshAction>())
        )
    }
}