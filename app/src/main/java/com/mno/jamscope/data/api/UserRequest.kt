package com.mno.jamscope.data.api

import android.util.Log
import com.mno.jamscope.data.mapper.toTrack
import com.mno.jamscope.data.remote.dto.RecentTracksResponseDto
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.util.Stuff
import com.mno.jamscope.util.Stuff.BASE_URL
import com.mno.jamscope.util.Stuff.FORMAT_JSON
import com.mno.jamscope.util.Stuff.JSON
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class UserRequest @Inject constructor() {
    suspend fun getFriendRecentTracks(friend: Friend) {
        val urlParametro =
            "method=user.getrecenttracks&user=${friend.name}&api_key=${Stuff.LAST_KEY}"
        val requestUrl = "$BASE_URL$FORMAT_JSON&$urlParametro"
        try {
            val response = withContext(Dispatchers.IO) {
                HttpClientProvider.client.post(requestUrl) {
                    headers {
                        append(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    }
                }
            }
            val recentTracksResponse =
                JSON.decodeFromString<RecentTracksResponseDto>(response.bodyAsText())
            recentTracksResponse.recenttracks.track.forEach { track ->
                track.dateInfo?.let { dateInfo ->
                    val localDateTime = LocalDateTime.parse(
                        dateInfo.formattedDate,
                        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
                    )
                    val zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.systemDefault())
                    val isoFormattedDate =
                        zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    dateInfo.formattedDate = isoFormattedDate
                }
            }
            friend.recentTracks = recentTracksResponse.recenttracks.track.map { it.toTrack() }
        } catch (e: Exception) {
            Log.e("UserRequest", "getFriendRecentTracksError: $e")
            e.printStackTrace()
        }
    }
}