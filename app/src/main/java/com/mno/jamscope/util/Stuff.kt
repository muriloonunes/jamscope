package com.mno.jamscope.util

import android.app.ActivityManager
import android.app.ApplicationExitInfo
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Token
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.ui.navigator.Destination
import kotlinx.serialization.json.Json

object Stuff {
    val WIDGET_CORNER_RADIUS = 16.dp
    const val LAST_KEY = Token.LAST_FM_API_KEY
    const val LAST_SECRET = Token.LAST_FM_SECRET
    const val REFRESHING_TIME = 150000L // 2.5 minutes
    const val EMAIL = "murideveloper@protonmail.com"
    const val BASE_URL = "https://ws.audioscrobbler.com/2.0/?"
    const val FORMAT_JSON = "format=json"
    const val DEFAULT_PROFILE_IMAGE =
        "https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png"
    val JSON = Json { ignoreUnknownKeys = true }


    fun Context.openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun getAppExitReasons(
        afterTime: Long = -1,
        printAll: Boolean = false,
        context: Context
    ): List<ApplicationExitInfo> {
        return try {
            val activityManager =
                ContextCompat.getSystemService(context, ActivityManager::class.java)!!
            val exitReasons = activityManager.getHistoricalProcessExitReasons(null, 0, 30)

            exitReasons.filter {
                it.processName == "${context.packageName}:com.mno.jamscope"
                        && it.reason == ApplicationExitInfo.REASON_CRASH
                        && it.timestamp > afterTime
//                        && it.reason == ApplicationExitInfo.REASON_OTHER
                        && it.timestamp > afterTime
            }.also {
                if (printAll) {
                    it.take(5).forEachIndexed { index, applicationExitInfo ->
                        Log.w("${index + 1}. $applicationExitInfo", "exitReasons")
                    }
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
        // Caused by java.lang.IllegalArgumentException at getHistoricalProcessExitReasons
        // Comparison method violates its general contract!
        // probably a samsung bug
    }

    fun Context.searchMusicIntent(track: Track) {
        val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
            putExtra(SearchManager.QUERY, "${track.artist.name} ${track.name}")
            putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Media.ENTRY_CONTENT_TYPE)
            putExtra(MediaStore.EXTRA_MEDIA_ARTIST, track.artist.name)
            putExtra(MediaStore.EXTRA_MEDIA_TITLE, track.name)
            if (track.album.name.isNotEmpty()) {
                putExtra(MediaStore.EXTRA_MEDIA_ALBUM, track.album.name)
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_music_player), Toast.LENGTH_SHORT).show()
        }
    }

    data class BottomNavigationItem(
        val title: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val destination: Destination
    )

    data class SwitchItem(
        val key: String,
        @StringRes val name: Int,
        val icon: ImageVector,
        @StringRes val iconDesc: Int,
        val initialState: Boolean
    )
}