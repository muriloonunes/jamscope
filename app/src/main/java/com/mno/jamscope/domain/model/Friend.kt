package com.mno.jamscope.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    val name: String,
    val largeImageUrl: String,
    val extraLargeImageUrl: String,
    val profileUrl: String,
    val country: String,
    val realName: String,
    val playcount: Long,
    val subscriber: Boolean,
    var recentTracks: List<Track> = emptyList()
)
