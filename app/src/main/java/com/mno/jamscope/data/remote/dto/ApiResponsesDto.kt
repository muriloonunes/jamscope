package com.mno.jamscope.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SessionResponseDto(
    val session: SessionDetailsDto
)

@Serializable
data class SessionDetailsDto(
    val name: String,
    val key: String,
    val subscriber: Int
)

//vira User no domain
@Serializable
data class ProfileDto(
    val username: String,
    val senha: String,
    val session: SessionDto,
    var subscriber: Int,
    var imageUrl: String? = null,
    var profileUrl: String? = null,
    var friends: List<FriendDto>? = null, // Na API: "user" serve como amigo também
    var country: String? = null,
    var realname: String = "",
    var playcount: Long? = null,
    var recentTracks: RecentTracksDto? = null,
)

@Serializable
data class SessionDto(
    val key: String,
)

@Serializable
data class ApiResponseDto(
    val user: FriendDto
)

@Serializable
data class UserFriendsResponseDto(
    val friends: FriendsDto
)

@Serializable
data class FriendsDto(
    val user: List<FriendDto>
)

/**
 * IMPORTANTE: Na API, "user" é usado tanto para usuário quanto para amigo.
 * No domain, faremos a distinção explicitamente:
 * - UserDto (API) -> Friend (Domain)
 * - ProfileDto (API) -> User (Domain)
 */
@Serializable
data class FriendDto(
    val name: String? = null,
    val image: List<ImageDto>,
    val url: String,
    val country: String? = null,
    val realname: String = "",
    val subscriber: Int? = null,
    val playcount: Long? = null,
    var recentTracks: RecentTracksDto? = null,
)

@Serializable
data class ImageDto(
    val size: String,
    @SerialName("#text") val url: String
)

@Serializable
data class RecentTracksResponseDto(
    val recenttracks: RecentTracksDto
)

@Serializable
data class RecentTracksDto(
    val track: List<TrackDto>
)

@Serializable
data class TrackDto(
    val artist: ArtistDto,
    val image: List<ImageDto>? = null,
    val album: AlbumDto,
    val name: String,
    @SerialName("date") val dateInfo: DateInfoDto? = null,
    @SerialName("@attr") val nowPlayingAttr: NowPlayingAttrDto? = null
)

@Serializable
data class ArtistDto(
    @SerialName("#text") val name: String,
)

@Serializable
data class AlbumDto(
    @SerialName("#text") val name: String,
)

@Serializable
data class DateInfoDto(
    @SerialName("uts") val timestamp: String,
    @SerialName("#text") var formattedDate: String
)

@Serializable
data class NowPlayingAttrDto(
    val nowplaying: String // "true" ou "false"
)