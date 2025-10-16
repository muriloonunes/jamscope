package com.mno.jamscope.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val url: String,
    val username: String,
    val password: String,
    val sessionKey: String,
    val country: String,
    val realName: String,
    val subscriber: Boolean,
    val playcount: Long,
    val imageLarge: String,
    val imageExtraLarge: String,
)