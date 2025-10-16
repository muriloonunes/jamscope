package com.mno.jamscope.features.widgets.singlefriend

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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalSize
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
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
import com.mno.jamscope.R
import com.mno.jamscope.activity.MainActivity
import com.mno.jamscope.features.widgets.WidgetDataStoreManager
import com.mno.jamscope.features.widgets.singlefriend.ui.LargeWidgetDesign
import com.mno.jamscope.features.widgets.singlefriend.ui.SmallWidgetDesign
import com.mno.jamscope.features.widgets.theme.JamscopeWidgetTheme
import com.mno.jamscope.util.Stuff
import com.mno.jamscope.worker.FriendListeningWidgetWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

class FriendListeningWidget : GlanceAppWidget() {
    companion object {
        val SMALL_LAYOUT_SIZE = DpSize(120.dp, 115.dp)
    }

    override val sizeMode = SizeMode.Exact
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            JamscopeWidgetTheme {
                WidgetUi(context)
            }
        }
    }

    @Composable
    fun WidgetUi(
        context: Context,
    ) {
        val size = LocalSize.current
        val isOneCellHeight = size.height < SMALL_LAYOUT_SIZE.height
        val friend = WidgetDataStoreManager.getFriend(currentState())
        val textStyle = TextStyle(
            color = GlanceTheme.colors.onSurface,
            fontSize = if (isOneCellHeight) 15.sp else 17.sp,
        )
        var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
        LaunchedEffect(friend) {
            if (!friend?.largeImageUrl.isNullOrEmpty()) {
                imageBitmap = loadBitmap(friend.largeImageUrl, context)
            }
        }
        val modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(2.dp)
            .cornerRadius(Stuff.WIDGET_CORNER_RADIUS)
            .clickableWidget(friend?.name)

        if (isOneCellHeight) {
            SmallWidgetDesign(
                modifier = modifier,
                context = context,
                friend = friend,
                textStyle = textStyle,
                imageBitmap = imageBitmap
            )
        } else {
            LargeWidgetDesign(
                modifier = modifier,
                context = context,
                friend = friend,
                textStyle = textStyle,
                imageBitmap = imageBitmap
            )
        }
    }
}

suspend fun loadBitmap(url: String, context: Context): Bitmap? {
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
//                return@withContext BitmapFactory.decodeResource(
//                    context.resources, R.drawable.baseline_account_circle_24
//                )
            return@withContext null
        }
    }
}

fun GlanceModifier.clickableWidget(name: String?): GlanceModifier {
    return this.clickable(
        onClick = actionStartActivity<MainActivity>(
            parameters = actionParametersOf(
                ActionParameters.Key<String>(Stuff.FROM_WIDGET) to (name ?: "")
            )
        )
    )
}

fun generateLastUpdatedString(context: Context): String {
    val locale = Locale.getDefault()
    val dateTimeFormatter: DateTimeFormatter
    val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)
    val dateFormat = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, locale)
    val datePattern = (dateFormat as SimpleDateFormat).toPattern()
    dateTimeFormatter = if (is24HourFormat) {
        DateTimeFormatter.ofPattern("$datePattern HH:mm", locale)
    } else {
        DateTimeFormatter.ofPattern("$datePattern hh:mm a", locale)
    }
    return LocalDateTime.now().format(dateTimeFormatter)
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
        parameters: ActionParameters,
    ) {
        WorkManager.getInstance(context).cancelUniqueWork("FriendListeningWidgetWorker")

        val oneTimeRequest = OneTimeWorkRequest.Builder(FriendListeningWidgetWorker::class.java)
            .setConstraints(NETWORK_CONSTRAINTS).build()
        WorkManager.getInstance(context).enqueue(oneTimeRequest)

        context.startPeriodicWorker()
    }
}
