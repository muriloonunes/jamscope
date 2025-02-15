package com.murile.nowplaying.util

import android.app.SearchManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.ui.unit.dp
import com.murile.nowplaying.R
import com.murile.nowplaying.data.model.Token
import com.murile.nowplaying.data.model.Track

object Stuff {
    val WIDGET_CORNER_RADIUS = 16.dp
    const val LAST_KEY = Token.LAST_FM_API_KEY
    const val LAST_SECRET = Token.LAST_FM_SECRET
    const val REFRESHING_TIME = 150000L // 2.5 minutes

    fun Context.openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
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
}