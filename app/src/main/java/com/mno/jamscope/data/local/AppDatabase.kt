package com.mno.jamscope.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mno.jamscope.data.local.dao.FriendsDao
import com.mno.jamscope.data.local.dao.UserProfileDao

@Database(entities = [FriendEntity::class, RecentTrackEntity::class, UserProfileEntity::class], version = 8)
abstract class AppDatabase : RoomDatabase() {
    abstract fun friendsDao(): FriendsDao
    abstract fun userProfileDao(): UserProfileDao
}