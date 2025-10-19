package com.mno.jamscope.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mno.jamscope.data.local.db.dao.FriendsDao
import com.mno.jamscope.data.local.db.dao.UserProfileDao
import com.mno.jamscope.data.local.db.entity.FriendEntity
import com.mno.jamscope.data.local.db.entity.TrackEntity
import com.mno.jamscope.data.local.db.entity.UserEntity

@Database(entities = [FriendEntity::class, TrackEntity::class, UserEntity::class], version = 12)
abstract class AppDatabase : RoomDatabase() {
    abstract fun friendsDao(): FriendsDao
    abstract fun userProfileDao(): UserProfileDao
}