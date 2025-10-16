package com.mno.jamscope.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mno.jamscope.data.local.dao.FriendsDao
import com.mno.jamscope.data.local.dao.UserProfileDao
import com.mno.jamscope.data.local.entity.FriendEntity
import com.mno.jamscope.data.local.entity.TrackEntity
import com.mno.jamscope.data.local.entity.UserEntity

@Database(entities = [FriendEntity::class, TrackEntity::class, UserEntity::class], version = 11)
abstract class AppDatabase : RoomDatabase() {
    abstract fun friendsDao(): FriendsDao
    abstract fun userProfileDao(): UserProfileDao
}