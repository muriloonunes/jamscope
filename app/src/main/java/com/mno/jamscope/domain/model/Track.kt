package com.mno.jamscope.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val name: String,
    val url: String,
    val artistName: String,
    val albumName: String,
    val largeImageUrl: String,
    val extraLargeImageUrl: String,
    val date: String?,
)
