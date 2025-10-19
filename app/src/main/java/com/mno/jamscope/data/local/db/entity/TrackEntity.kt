package com.mno.jamscope.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    foreignKeys = [
        ForeignKey(
            entity = FriendEntity::class,
            parentColumns = ["profileUrl"],
            childColumns = ["friendOwnerUrl"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["url"],
            childColumns = ["userOwnerUrl"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("friendOwnerUrl"), Index("userOwnerUrl")],
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val friendOwnerUrl: String?,
    val userOwnerUrl: String?,
    val name: String,
    val artistName: String,
    val albumName: String,
    val largeImageUrl: String,
    val extraLargeImageUrl: String,
    val date: String?,
    val url: String,
)