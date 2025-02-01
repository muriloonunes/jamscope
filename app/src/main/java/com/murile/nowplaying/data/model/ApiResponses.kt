package com.murile.nowplaying.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
data class SessionResponse(
    val session: SessionDetails
)

@Serializable
data class SessionDetails(
    val name: String,
    val key: String,
    val subscriber: Int
)

@Serializable
data class Profile(
    val username: String,
    val senha: String,
    val session: Session,
    var subscriber: Int,
    var imageUrl: String? = null,
    var profileUrl: String? = null,
    var friends: List<User>? = null,
    var country: String? = null,
    var realname: String = "",
    var playcount: Long? = null
)

@Serializable
data class Session(
    val key: String,
)

@Serializable
data class ApiResponse(
    val user: User
)

@Serializable
data class UserFriendsResponse(
    val friends: Friends
)

@Serializable
data class Friends(
    val user: List<User>
)

@Serializable
data class User(
    val name: String? = null,
    val image: List<Image>,
    val url: String,
    val country: String? = null,
    val realname: String = "",
    val subscriber: Int? = null,
    val playcount: Long? = null,
    var recentTracks: RecentTracks? = null,
)

@Serializable
data class Image(
    val size: String,
    @SerialName("#text") val url: String
)

@Serializable
data class RecentTracksResponse(
    val recenttracks: RecentTracks
)

@Serializable
data class RecentTracks(
    val track: List<Track>
)

@Serializable
data class Track(
    val artist: Artist,
    val image: List<Image>,
    val album: Album,
    val name: String,
    @SerialName("date") val dateInfo: DateInfo? = null,
    @SerialName("@attr") val nowPlayingAttr: NowPlayingAttr? = null
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class Artist(
    @SerialName("#text") val name: String,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class Album(
    @SerialName("#text") val name: String,
)

@Serializable
data class DateInfo(
    @SerialName("uts") val timestamp: String,
    @SerialName("#text") var formattedDate: String
)

@Serializable
data class NowPlayingAttr(
    val nowplaying: String
)