package com.mno.jamscope.util

import android.app.ActivityManager
import android.app.ApplicationExitInfo
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mno.jamscope.domain.model.Token

object Stuff {
    val WIDGET_CORNER_RADIUS = 16.dp
    const val LAST_KEY = Token.LAST_FM_API_KEY
    const val LAST_SECRET = Token.LAST_FM_SECRET
    const val REFRESHING_TIME = 90000L // 1.5 minutes
    const val EMAIL = "murideveloper@protonmail.com"
    const val AUTH_URL = "https://www.last.fm/api/auth"
    const val DEEPLINK_PROTOCOL_NAME = "jamscope"
    const val DEFAULT_PROFILE_IMAGE =
        "https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png"
    const val GITHUB_RELEASES_LINK = "https://github.com/muriloonunes/jamscope/releases/tag/v"
    const val FROM_WIDGET = "FROM_WIDGET"

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
}