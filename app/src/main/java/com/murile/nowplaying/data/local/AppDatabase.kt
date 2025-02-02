package com.murile.nowplaying.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FriendEntity::class, RecentTrackEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun friendsDao(): FriendsDao
}