package com.mno.jamscope.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mno.jamscope.data.local.db.entity.TrackEntity

@Dao
interface TrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query("SELECT * FROM tracks WHERE userOwnerUrl = :username")
    suspend fun getUserTracks(username: String): List<TrackEntity>

    @Query("SELECT * FROM tracks WHERE friendOwnerUrl = :friendProfileUrl")
    suspend fun getFriendTracks(friendProfileUrl: String): List<TrackEntity>

    @Query("DELETE FROM tracks")
    suspend fun deleteAllRecentTracks()
}
