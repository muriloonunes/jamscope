package com.murile.nowplaying.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.murile.nowplaying.data.local.RecentTrackEntity
import com.murile.nowplaying.data.local.UserProfileEntity

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile")
    fun getUserProfile(): UserProfileEntity

    @Query("SELECT * FROM recent_tracks WHERE userUrl = :userUrl")
    fun getRecentTracksForUser(userUrl: String): List<RecentTrackEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentTracks(tracks: List<RecentTrackEntity>)

    @Query("DELETE FROM recent_tracks WHERE userUrl = :userUrl")
    suspend fun deleteRecentTracksForUser(userUrl: String)

    @Query("DELETE FROM recent_tracks")
    suspend fun deleteAllRecentTracks()

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}