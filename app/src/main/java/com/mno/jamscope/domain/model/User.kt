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