package com.mno.jamscope.data.local

import com.mno.jamscope.data.model.Album
import com.mno.jamscope.data.model.Artist
import com.mno.jamscope.data.model.DateInfo
import com.mno.jamscope.data.model.Image
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.Session
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.data.model.User
import kotlinx.serialization.json.Json

fun User.toFriendEntity(): FriendEntity {
    return FriendEntity(
        url = this.url,
        name = this.name,
        imageUrl = this.image.find { it.size == "large" }?.url,
        country = this.country,
        realname = this.realname,
        subscriber = this.subscriber,
        playcount = this.playcount
    )
}

fun FriendEntity.toUser(recentTracks: List<RecentTrackEntity>): User {
    return User(
        name = this.name,
        image = listOf(Image(size = "large", url = this.imageUrl ?: "")),
        url = this.url,
        country = this.country,
        realname = this.realname,
        subscriber = this.subscriber,
        playcount = this.playcount,
        recentTracks = if (recentTracks.isNotEmpty()) RecentTracks(recentTracks.map { it.toTrack() }) else null
    )
}

fun Track.toRecentTrackEntity(userUrl: String) = RecentTrackEntity(
    userUrl = userUrl,
    trackName = this.name,
    albumName = this.album.name,
    artistName = this.artist.name,
    timestamp = this.dateInfo?.timestamp ?: "",
    formattedDate = dateInfo?.formattedDate,
    imageUrl = this.image?.firstOrNull { it.size == "medium" }?.url ?: ""
)

fun RecentTrackEntity.toTrack(): Track {
    return Track(
        artist = Artist(this.artistName),
        album = Album(this.albumName),
        name = this.trackName,
        dateInfo = this.formattedDate?.let { DateInfo(timestamp = "", it) },
        image = this.imageUrl.takeIf { it?.isNotEmpty() ?: true }?.let {
        listOf(Image(url = it, size = "medium"))
    } ?: emptyList()
    )
}

fun Profile.toUserProfileEntity(): UserProfileEntity {
    val sessionString = Json.encodeToString(this.session)

    return UserProfileEntity(
        username = this.username,
        senha = this.senha,
        session = sessionString,
        imageUrl = this.imageUrl,
        country = this.country,
        realname = this.realname,
        subscriber = this.subscriber,
        playcount = this.playcount,
        url = this.profileUrl!!
    )
}

fun UserProfileEntity.toProfile(recentTracks: List<RecentTrackEntity>): Profile {
    val sessionObj = Json.decodeFromString<Session>(this.session)
    return Profile(
        username = this.username,
        senha = this.senha,
        session = sessionObj,
        imageUrl = this.imageUrl,
        country = this.country,
        realname = this.realname,
        subscriber = this.subscriber ?: 0,
        playcount = this.playcount,
        profileUrl = this.url,
        recentTracks = if (recentTracks.isNotEmpty()) RecentTracks(recentTracks.map { it.toTrack() }) else null
    )
}