package com.mno.jamscope.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend")
data class FriendEntity(
    @PrimaryKey val url: String,
    val name: String?,
    val imageUrl: String?,
    val country: String?,
    val realname: String,
    val subscriber: Int?,
    val playcount: Long?
)

@Entity(tableName = "recent_tracks")
data class RecentTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userUrl: String,
    val trackName: String,
    val albumName: String,
    val artistName: String,
    val timestamp: String,
    val formattedDate: String?,
    val imageUrl: String?
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val username: String,
    val senha: String,
    val url: String,
    val session: String,
    val imageUrl: String?,
    val country: String?,
    val realname: String,
    val subscriber: Int?,
    val playcount: Long?
)