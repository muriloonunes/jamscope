package com.mno.jamscope.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mno.jamscope.data.local.db.entity.FriendEntity
import com.mno.jamscope.data.local.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendsDao {
    @Query("SELECT * FROM friend")
    fun getAllFriends(): Flow<List<FriendEntity>>
    @Query("SELECT * FROM friend")
    suspend fun getFriends(): List<FriendEntity>
    @Query("SELECT * FROM recent_tracks WHERE ownerUrl = :userUrl")
    fun getRecentTracksForUser(userUrl: String): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<FriendEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentTracks(tracks: List<TrackEntity>)

    @Query("DELETE FROM recent_tracks WHERE ownerUrl = :userUrl")
    suspend fun deleteRecentTracksForUser(userUrl: String)

    @Query("DELETE FROM friend")
    suspend fun deleteAllFriends()
}