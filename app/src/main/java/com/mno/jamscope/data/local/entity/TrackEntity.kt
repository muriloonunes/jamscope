package com.mno.jamscope.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent_tracks",
    foreignKeys = [
        ForeignKey(
            entity = FriendEntity::class,
            parentColumns = ["url"],
            childColumns = ["ownerUrl"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["url"],
            childColumns = ["ownerUrl"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("ownerUrl")]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerUrl: String,
    val name: String,
    val artistName: String,
    val albumName: String,
    val largeImageUrl: String,
    val extraLargeImageUrl: String,
    val date: String,
    val isNowPlaying: Boolean,
)