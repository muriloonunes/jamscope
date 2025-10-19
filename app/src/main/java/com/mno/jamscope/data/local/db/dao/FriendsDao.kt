package com.mno.jamscope.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mno.jamscope.data.local.db.entity.FriendEntity

@Dao
interface FriendsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriends(friends: List<FriendEntity>)

    @Query("SELECT * FROM friend")
    suspend fun getFriends(): List<FriendEntity>

    @Query("SELECT * FROM friend WHERE profileUrl = :url LIMIT 1")
    suspend fun getFriendByUrl(url: String): FriendEntity?

    @Query("DELETE FROM friend")
    suspend fun deleteAllFriends()
}