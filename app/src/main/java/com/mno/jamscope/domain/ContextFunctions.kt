package com.mno.jamscope.domain

import android.app.ActivityManager
import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.mno.jamscope.R
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.util.Stuff
import java.io.BufferedReader
import java.io.InputStreamReader

fun Context.handleError(code: Int): String {
    val errorMessage = when (code) {
        6 -> getString(R.string.no_friends)
        17 -> getString(R.string.login_required_error)
        403 -> getString(R.string.invalid_user_password)
        429 -> getString(R.string.too_many_requests)
        500 -> getString(R.string.server_error)
        666 -> getString(R.string.connection_error)
        999 -> getString(R.string.fail_fetch_friend)
        else -> getString(R.string.unknown_error)
    }
    return errorMessage
}

fun Context.readRawFile(
    @RawRes fileRes: Int,
    expectedFileName: String,
): String {
    return try {
        if (fileRes == 0) {
            this.getString(R.string.not_found, expectedFileName)
        }
        val inputStream = this.resources.openRawResource(fileRes)
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
        this.getString(R.string.not_found, expectedFileName)
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

fun Context.sendReportMail() {
    var bgRam = -1
    val manager =
        ContextCompat.getSystemService(this, ActivityManager::class.java)!!
    for (proc in manager.runningAppProcesses) {
        if (proc?.processName?.contains("com.mno.jamscope") == true) {
            // https://stackoverflow.com/questions/2298208/how-do-i-discover-memory-usage-of-my-application-in-android
            val memInfo = manager.getProcessMemoryInfo(intArrayOf(proc.pid)).first()
            bgRam = memInfo.totalPss / 1024
            break
        }
    }

    var lastExitInfo: String? = null
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        lastExitInfo =
            Stuff.getAppExitReasons(printAll = true, context = this).firstOrNull()
                ?.toString()
    }
    val packageName = packageName
    val pm = packageManager
    val appInfo = pm.getApplicationInfo(packageName, 0)
    val appName = pm.getApplicationLabel(appInfo).toString()
    val versionName = pm.getPackageInfo(packageName, 0).versionName
    var text = ""
    text += "$appName v$versionName\n"
    text += "Android " + Build.VERSION.RELEASE + "\n"
    text += "Device: " + Build.BRAND + " " + Build.MODEL + " / " + Build.DEVICE + "\n"
    val mi = ActivityManager.MemoryInfo()
    manager.getMemoryInfo(mi)
    text += "Background RAM usage: " + bgRam + "M \n"
    if (lastExitInfo != null)
        text += "Last exit reason: $lastExitInfo\n"
    text += "----------------------------------"
    text += "\n\n[Describe the issue]\n"
    Log.d("SettingsViewModel", "text: $text")

    val subject = "$appName - Bug report"
    sendMail(
        subject = subject,
        text = text
    )
}

fun Context.sendMail(
    subject: String? = null,
    text: String? = null,
) {
    val emailAddress = Stuff.EMAIL
    val uri = "mailto:".toUri() // this filters email-only apps
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = uri
        putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
        subject?.let {
            putExtra(Intent.EXTRA_SUBJECT, it)
        }
        text?.let {
            putExtra(Intent.EXTRA_TEXT, it)
        }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        startActivity(emailIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            this,
            getString(R.string.no_email_app_found),
            Toast.LENGTH_LONG
        ).show()
    }
}

fun Context.searchMusicIntent(track: Track) {
    val intent = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH).apply {
        putExtra(SearchManager.QUERY, "${track.artistName} ${track.name}")
        putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Media.ENTRY_CONTENT_TYPE)
        putExtra(MediaStore.EXTRA_MEDIA_ARTIST, track.artistName)
        putExtra(MediaStore.EXTRA_MEDIA_TITLE, track.name)
        if (track.albumName.isNotEmpty()) {
            putExtra(MediaStore.EXTRA_MEDIA_ALBUM, track.albumName)
        }
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, getString(R.string.no_music_player), Toast.LENGTH_SHORT).show()
    }
}