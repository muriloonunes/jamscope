package com.murile.nowplaying.data.local

import com.murile.nowplaying.data.model.Album
import com.murile.nowplaying.data.model.Artist
import com.murile.nowplaying.data.model.DateInfo
import com.murile.nowplaying.data.model.Image
import com.murile.nowplaying.data.model.RecentTracks
import com.murile.nowplaying.data.model.Track
import com.murile.nowplaying.data.model.User

fun User.toFriendEntity(): FriendEntity {
    return FriendEntity(
        url = this.url,
        name = this.name,
        imageUrl = this.image.find { it.size == "large" }?.url, // salvar a imagem do tamanho large
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
    formattedDate = dateInfo?.formattedDate
)

fun RecentTrackEntity.toTrack(): Track {
    return Track(
        artist = Artist(this.artistName),
        album = Album(this.albumName),
        name = this.trackName,
        dateInfo = this.formattedDate?.let { DateInfo(timestamp = "", it) },
    )
}