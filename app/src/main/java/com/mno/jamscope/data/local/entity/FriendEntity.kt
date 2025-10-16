package com.mno.jamscope.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend")
data class FriendEntity(
    @PrimaryKey val url: String,
    val name: String,
    val country: String,
    val realname: String,
    val subscriber: Boolean,
    val playcount: Long,
    val imageLarge: String,
    val imageExtraLarge: String,
)