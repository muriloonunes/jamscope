package com.murile.nowplaying.widget.singlefriend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentSize
import androidx.glance.layout.wrapContentWidth
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import com.murile.nowplaying.R
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.util.Stuff
import com.murile.nowplaying.util.dateStringFormatter
import com.murile.nowplaying.widget.WidgetDataStoreManager
import com.murile.nowplaying.worker.FriendListeningWidgetWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class FriendListeningWidget : GlanceAppWidget() {
    override val sizeMode = SizeMode.Single
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetDesign(
                context
            )
        }
    }

    @Composable
    private fun WidgetDesign(
        context: Context
    ) {
        val friend = WidgetDataStoreManager.getFriend(currentState())
        val textStyle = TextStyle(
            color = GlanceTheme.colors.onSurface,
        )
        var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
        LaunchedEffect(friend) {
            imageBitmap = friend?.image?.firstOrNull { it.size == "large" }?.url?.let {
                loadBitmap(
                    it, context
                )
            }
        }
        Row(
            modifier = GlanceModifier.fillMaxSize().background(GlanceTheme.colors.surface)
                .padding(8.dp).cornerRadius(Stuff.WIDGET_CORNER_RADIUS),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileImage(friend, imageBitmap)
            FriendInfo(friend, textStyle, context)
        }
    }

    @Composable
    private fun ProfileImage(
        friend: User?, imageBitmap: Bitmap?
    ) {
        val imageDescription = friend?.name?.let {
            LocalContext.current.getString(R.string.profile_pic_description, it)
        } ?: LocalContext.current.getString(R.string.no_friend_selected)
        imageBitmap?.let {
            Image(
                provider = ImageProvider(it),
                contentDescription = imageDescription,
                modifier = GlanceModifier.cornerRadius(50.dp).size(60.dp)
            )
        } ?: run {
            Image(
                provider = ImageProvider(R.drawable.baseline_account_circle_24),
                contentDescription = imageDescription,
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurface)
            )
        }
    }

    @Composable
    private fun FriendInfo(friend: User?, textStyle: TextStyle, context: Context) {
        Column(
            horizontalAlignment = Alignment.Start, modifier = GlanceModifier.padding(start = 8.dp)
        ) {
            when {
                friend == null -> NoFriendSelected(textStyle)
                friend.recentTracks == null -> NoRecentTracks(textStyle, context)
                else -> FriendListeningDetails(friend, textStyle, context)
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
    private fun FriendListeningDetails(friend: User, textStyle: TextStyle, context: Context) {
        val track = friend.recentTracks?.track?.firstOrNull()

        Text(
            text = context.getString(
                R.string.friend_listening_to,
                friend.realname.ifEmpty { friend.name!! }), style = textStyle, maxLines = 1
        )

        track?.let { it ->
            Text(text = it.name, style = textStyle, maxLines = 1)
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = track.album.name,
                    style = textStyle,
                    maxLines = 1,
                    modifier = GlanceModifier.defaultWeight()
                )

                val dateText = track.dateInfo?.formattedDate?.let {
                    dateStringFormatter(it, true, context)
                } ?: context.getString(R.string.now)

                Text(
                    text = dateText, style = TextStyle(
                        color = GlanceTheme.colors.onSurfaceVariant, fontSize = 13.sp
                    ), maxLines = 1, modifier = GlanceModifier.wrapContentWidth()
                )
            }
            Text(text = it.artist.name, style = textStyle, maxLines = 1)
            LastUpdatedManager()
        } ?: NoRecentTracks(textStyle, context)
    }

    @Composable
    private fun LastUpdatedManager() {
        val lastUpdated = WidgetDataStoreManager.getLastUpdated(currentState())
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (lastUpdated.isEmpty()) LocalContext.current.getString(R.string.never_updated) else LocalContext.current.getString(
                    R.string.last_updated_on,
                    lastUpdated
                ),
                style = TextStyle(GlanceTheme.colors.onSurfaceVariant, fontSize = 12.sp),
                maxLines = 1,
                modifier = GlanceModifier.defaultWeight()
            )
            Image(
                provider = ImageProvider(R.drawable.round_refresh_24),
                contentDescription = LocalContext.current.getString(R.string.refresh_button),
                colorFilter = ColorFilter.tint(GlanceTheme.colors.onSurfaceVariant),
                modifier = GlanceModifier.wrapContentSize()
                    .clickable(onClick = actionRunCallback<WidgetRefreshAction>())
            )
        }
    }

    private suspend fun loadBitmap(url: String, context: Context): Bitmap {
        return withContext(Dispatchers.IO) {
            try {
                if (url.isEmpty()) {
                    return@withContext BitmapFactory.decodeResource(
                        context.resources, R.drawable.baseline_account_circle_24
                    )
                }
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context).data(url).build()
                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    return@withContext result.image.toBitmap()
                } else {
                    return@withContext BitmapFactory.decodeResource(
                        context.resources, R.drawable.baseline_account_circle_24
                    )
                }
            } catch (e: Exception) {
                Log.e("loadBitmap", e.toString())
                return@withContext BitmapFactory.decodeResource(
                    context.resources, R.drawable.baseline_account_circle_24
                )
            }
        }
    }
}

val NETWORK_CONSTRAINTS =
    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

fun Context.startListeningUpdateWorker() {
    val oneTimeRequest = OneTimeWorkRequest.Builder(FriendListeningWidgetWorker::class.java)
        .setConstraints(NETWORK_CONSTRAINTS).build()
    WorkManager.getInstance(this).enqueue(oneTimeRequest)

    startPeriodicWorker()
}

fun Context.startPeriodicWorker() {
    val periodicRequest =
        PeriodicWorkRequest.Builder(FriendListeningWidgetWorker::class.java, 15, TimeUnit.MINUTES)
            .setBackoffCriteria(
                androidx.work.BackoffPolicy.EXPONENTIAL, 5000L, TimeUnit.MILLISECONDS
            ).setConstraints(NETWORK_CONSTRAINTS).build()
    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        "FriendListeningWidgetWorker",
        ExistingPeriodicWorkPolicy.UPDATE,
        periodicRequest
    )
}

class WidgetRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        WorkManager.getInstance(context).cancelUniqueWork("FriendListeningWidgetWorker")

        val oneTimeRequest = OneTimeWorkRequest.Builder(FriendListeningWidgetWorker::class.java)
            .setConstraints(NETWORK_CONSTRAINTS).build()
        WorkManager.getInstance(context).enqueue(oneTimeRequest)

        context.startPeriodicWorker()
    }
}
