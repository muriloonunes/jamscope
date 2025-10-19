package com.mno.jamscope.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String,
    val sessionKey: String,
    var subscriber: Boolean,
    var largeImageUrl: String,
    var extraLargeImageUrl: String,
    var profileUrl: String,
    var country: String,
    var realName: String,
    var playcount: Long,
    var friends: List<Friend> = emptyList(),
    var recentTracks: List<Track> = emptyList()
)

fun User.mergeWith(fullUser: User): User {
    return User(
        username = this.username,
        password = this.password,
        sessionKey = this.sessionKey,
        subscriber = fullUser.subscriber,
        largeImageUrl = fullUser.largeImageUrl,
        extraLargeImageUrl = fullUser.extraLargeImageUrl,
        profileUrl = fullUser.profileUrl,
        country = fullUser.country,
        realName = fullUser.realName,
        playcount = fullUser.playcount,
        friends = fullUser.friends,
        recentTracks = this.recentTracks
    )
}