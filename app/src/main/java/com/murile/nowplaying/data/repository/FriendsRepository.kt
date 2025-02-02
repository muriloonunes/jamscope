package com.murile.nowplaying.data.repository

import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.local.FriendsDao
import com.murile.nowplaying.data.local.toFriendEntity
import com.murile.nowplaying.data.local.toRecentTrackEntity
import com.murile.nowplaying.data.local.toTrack
import com.murile.nowplaying.data.local.toUser
import com.murile.nowplaying.data.model.Track
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.session.DataStoreManager
import com.murile.nowplaying.util.SortingType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FriendsRepository @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val friendsDao: FriendsDao,
    private val apiRequest: ApiRequest
) {
    suspend fun saveSortingType(sortingType: SortingType) {
        dataStoreManager.saveSortingType(sortingType)
    }

    suspend fun getSortingType(): SortingType {
        return dataStoreManager.getSortingType()
    }

    fun getCachedFriends(): Flow<List<User>> = friendsDao.getAllUsers().map { friendEntities ->
        friendEntities.map { friendEntity ->
            val recentTracks = friendsDao.getRecentTracksForUser(friendEntity.url).firstOrNull() ?: emptyList()
            friendEntity.toUser(recentTracks)
        }
    }

    fun getCachedRecentTracks(userUrl: String): Flow<List<Track>> {
        return friendsDao.getRecentTracksForUser(userUrl).map { list -> list.map { it.toTrack() } }
    }

    suspend fun cacheFriends(users: List<User>) {
        val friends = users.map { it.toFriendEntity() }
        friendsDao.insertUsers(friends)
    }

    suspend fun cacheRecentTracks(userUrl: String, tracks: List<Track>) {
        val trackEntities = tracks.map { it.toRecentTrackEntity(userUrl) }
        friendsDao.deleteRecentTracksForUser(userUrl)
        friendsDao.insertRecentTracks(trackEntities)
    }

    suspend fun getRecentTracks(user: User) {
        return apiRequest.getRecentTracks(user)
    }
}