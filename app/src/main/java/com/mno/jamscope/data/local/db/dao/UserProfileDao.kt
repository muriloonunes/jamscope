package com.mno.jamscope.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mno.jamscope.data.local.db.entity.TrackEntity
import com.mno.jamscope.data.local.db.entity.UserEntity

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile")
    fun getUserProfile(): UserEntity

    @Query("SELECT * FROM recent_tracks WHERE ownerUrl = :userUrl")
    fun getRecentTracksForUser(userUrl: String): List<TrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentTracks(tracks: List<TrackEntity>)

    @Query("DELETE FROM recent_tracks WHERE ownerUrl = :userUrl")
    suspend fun deleteRecentTracksForUser(userUrl: String)

    @Query("DELETE FROM recent_tracks")
    suspend fun deleteAllRecentTracks()

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}