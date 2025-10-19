package com.mno.jamscope.data.remote.mapper

import com.mno.jamscope.data.remote.dto.FriendDto
import com.mno.jamscope.data.remote.dto.ProfileDto
import com.mno.jamscope.data.remote.dto.TrackDto
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.model.User
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun ProfileDto.toUser(): User {
    return User(
        username = username,
        password = senha,
        sessionKey = session.key,
        subscriber = subscriber == 1,
        largeImageUrl = extraLargeImageUrl ?: "",
        extraLargeImageUrl = extraLargeImageUrl ?: "",
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
        name = name,
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
    val largeUrl = image?.find { it.size == "large" }?.url ?: ""
    val extraLargeUrl = image?.find { it.size == "extralarge" }?.url ?: ""
    val date = dateInfo?.formattedDate?.let {
        val instant = Instant.parse(it)
        val zonedDateTime = instant.atZone(ZoneId.systemDefault())
        zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    return Track(
        name = name,
        artistName = artist.name,
        albumName = album.name,
        largeImageUrl = largeUrl,
        extraLargeImageUrl = extraLargeUrl,
        date = date,
        url = url,
    )
}