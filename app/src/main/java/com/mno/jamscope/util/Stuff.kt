package com.mno.jamscope.util

import android.app.ActivityManager
import android.app.ApplicationExitInfo
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Token
import com.mno.jamscope.data.model.Track
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

object Stuff {
    val WIDGET_CORNER_RADIUS = 16.dp
    const val LAST_KEY = Token.LAST_FM_API_KEY
    const val LAST_SECRET = Token.LAST_FM_SECRET
    const val REFRESHING_TIME = 90000L // 1.5 minutes
    const val EMAIL = "murideveloper@protonmail.com"
    const val BASE_URL = "https://ws.audioscrobbler.com/2.0/?"
    const val FORMAT_JSON = "format=json"
    const val DEFAULT_PROFILE_IMAGE =
        "https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png"

    const val GITHUB_RELEASES_LINK = "https://github.com/muriloonunes/jamscope/releases/tag/v"

    const val FROM_WIDGET = "FROM_WIDGET"
    val JSON = Json { ignoreUnknownKeys = true }

    fun Context.readChangelog(): String {
        return try {
            val resourceId = R.raw.changelog
            if (resourceId == 0) {
                this.getString(R.string.changelog_not_found)
            }
            val inputStream = this.resources.openRawResource(resourceId)
            val reader = BufferedReader(InputStreamReader(inputStream))
            val changelog = StringBuilder()
            var line: String? = reader.readLine()
            while (line != null) {
                changelog.append(line).append("\n")
                line = reader.readLine()
            }
            reader.close()
            changelog.toString().trimEnd()
        } catch (e: Exception) {
            e.printStackTrace()
            this.getString(R.string.changelog_not_found)
        }
    }

    fun Context.openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(intent)
    }

    //fiz essa funcao antes de descobrir que preciso pagar pra colocar o app na play store...
    //entao essa funcao provavelmente nunca vai ser usada
    //vai se fuder google
    fun Context.openPlayStore() {
        val appId = packageName
        val rateIntent = Intent(
            Intent.ACTION_VIEW,
            "market://details?id=$appId".toUri()
        )
        var marketFound = false


        // find all applications able to handle our rateIntent
        val otherApps = packageManager
            .queryIntentActivities(rateIntent, 0)
        for (otherApp in otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                == "com.android.vending"
            ) {
                val otherAppActivity = otherApp.activityInfo
                val componentName = ComponentName(
                    otherAppActivity.applicationInfo.packageName,
                    otherAppActivity.name
                )
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName)
                startActivity(rateIntent)
                marketFound = true
                break
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$appId".toUri()
            )
            startActivity(webIntent)
        }
    }

    //thanks pano-scrobbler
    @RequiresApi(Build.VERSION_CODES.R)
    fun getAppExitReasons(
        afterTime: Long = -1,
        printAll: Boolean = false,
        context: Context,
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
        } catch (_: Exception) {
            emptyList()
        }
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
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.no_music_player), Toast.LENGTH_SHORT).show()
        }
    }

    data class SwitchItem(
        val key: String,
        @param:StringRes val name: Int,
        val icon: ImageVector,
        @param:StringRes val iconDesc: Int,
        val initialState: Boolean,
    )
}