package com.mno.jamscope.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String,
    val sessionKey: String,
    val subscriber: Boolean,
    val largeImageUrl: String,
    val extraLargeImageUrl: String,
    val profileUrl: String,
    val country: String,
    val realName: String,
    val playcount: Long,
    var recentTracks: List<Track> = emptyList()
)