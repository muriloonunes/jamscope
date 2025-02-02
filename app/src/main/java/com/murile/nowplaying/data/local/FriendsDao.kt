package com.murile.nowplaying.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendsDao {
    @Query("SELECT * FROM friend")
    fun getAllUsers(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM recent_tracks WHERE userUrl = :userUrl")
    fun getRecentTracksForUser(userUrl: String): Flow<List<RecentTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<FriendEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentTracks(tracks: List<RecentTrackEntity>)

    @Query("DELETE FROM recent_tracks WHERE userUrl = :userUrl")
    suspend fun deleteRecentTracksForUser(userUrl: String)
}