package com.murile.nowplaying.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.unit.dp
import com.murile.nowplaying.data.model.Token

object Stuff {
    val WIDGET_CORNER_RADIUS = 16.dp
    const val LAST_KEY = Token.LAST_FM_API_KEY
    const val LAST_SECRET = Token.LAST_FM_SECRET
    const val REFRESHING_TIME = 150000L // 2.5 minutes

    fun Context.openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}