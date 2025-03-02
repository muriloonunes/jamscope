package com.mno.jamscope.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mno.jamscope.R

fun sendReportMail(context: Context) {
    var bgRam = -1
    val manager =
        ContextCompat.getSystemService(context, ActivityManager::class.java)!!
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
            Stuff.getAppExitReasons(printAll = true, context = context).firstOrNull()
                ?.toString()
    }
    val packageName = context.packageName
    val pm = context.packageManager
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

    val emailAddress = Stuff.EMAIL
    val emailIntent =
        Intent(
            Intent.ACTION_SENDTO,
            Uri.fromParts("mailto", emailAddress, null)
        ).apply {
            // https://stackoverflow.com/questions/33098280/how-to-choose-email-app-with-action-sendto-also-support-attachment
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, emailAddress)
            putExtra(
                Intent.EXTRA_SUBJECT,
                "$appName - Bug report"
            )
            putExtra(Intent.EXTRA_TEXT, text)
//                putExtra(Intent.EXTRA_STREAM, logUri)
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

    try {
        context.startActivity(emailIntent)
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(
            context,
            context.getString(R.string.no_email_app_found),
            Toast.LENGTH_LONG
        ).show()
    }
}