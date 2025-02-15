package com.murile.nowplaying.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.murile.nowplaying.data.local.dao.FriendsDao
import com.murile.nowplaying.data.local.dao.UserProfileDao

@Database(entities = [FriendEntity::class, RecentTrackEntity::class, UserProfileEntity::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun friendsDao(): FriendsDao
    abstract fun userProfileDao(): UserProfileDao
}