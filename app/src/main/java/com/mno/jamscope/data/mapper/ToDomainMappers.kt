package com.mno.jamscope.data.mapper

import android.util.Log
import com.mno.jamscope.data.remote.dto.FriendDto
import com.mno.jamscope.data.remote.dto.ProfileDto
import com.mno.jamscope.data.remote.dto.TrackDto
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.model.User
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun ProfileDto.toUser(): User {
    return User(
        username = username,
        password = senha,
        sessionKey = session.key,
        subscriber = subscriber == 1,
        largeImageUrl = imageUrl ?: "",
        extraLargeImageUrl = imageUrl ?: "",
        profileUrl = profileUrl ?: "",
        country = country ?: "",
        realName = realname,
        playcount = playcount ?: 0,
        friends = friends?.map { it.toFriend() } ?: emptyList(),
        recentTracks = recentTracks?.track?.map { it.toTrack() } ?: emptyList()
    )
}

fun FriendDto.toFriend(): Friend {
    val largeUrl = image.find { it.size == "large" }?.url ?: ""
    val extraLargeUrl = image.find { it.size == "extralarge" }?.url ?: ""

    return Friend(
        name = name ?: "",
        largeImageUrl = largeUrl,
        extraLargeImageUrl = extraLargeUrl,
        profileUrl = url,
        country = country ?: "",
        realName = realname,
        playcount = playcount ?: 0,
        subscriber = subscriber == 1,
        recentTracks = recentTracks?.track?.map { it.toTrack() } ?: emptyList()
    )
}

fun TrackDto.toTrack(): Track {
    Log.d("TrackDto", "Converting TrackDto: $this")
    val largeUrl = image?.find { it.size == "large" }?.url ?: ""
    val extraLargeUrl = image?.find { it.size == "extralarge" }?.url ?: ""

    val formattedDate = dateInfo?.let {
        try {
            val localDateTime = LocalDateTime.parse(
                it.formattedDate,
                DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
            )
            val zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"))
                .withZoneSameInstant(ZoneId.systemDefault())
            zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        } catch (_: Exception) {
            ""
        }
    } ?: ""

    return Track(
        name = name,
        artistName = artist.name,
        albumName = album.name,
        largeImageUrl = largeUrl,
        extraLargeImageUrl = extraLargeUrl,
        date = formattedDate,
        isNowPlaying = nowPlayingAttr?.nowplaying == "true"
    )
}