package com.mno.jamscope.data.repository

import com.mno.jamscope.data.local.dao.FriendsDao
import com.mno.jamscope.data.local.toFriendEntity
import com.mno.jamscope.data.local.toRecentTrackEntity
import com.mno.jamscope.data.local.toTrack
import com.mno.jamscope.data.local.toUser
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.data.model.User
import com.mno.jamscope.data.session.UserDataStoreManager
import com.mno.jamscope.util.SortingType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FriendsRepository @Inject constructor(
    private val userDataStoreManager: UserDataStoreManager,
    private val friendsDao: FriendsDao,
    private val apiRepository: ApiRepository
) {
    suspend fun saveSortingType(sortingType: SortingType) {
        userDataStoreManager.saveSortingType(sortingType)
    }

    suspend fun getSortingType(): SortingType {
        return userDataStoreManager.getSortingType()
    }

    fun getFriendsFromDataStore(): Flow<List<User>> = flow {
        val profile = userDataStoreManager.getUserProfile()
        emit(profile?.friends ?: emptyList())
    }

    fun getCachedFriends(): Flow<List<User>> = friendsDao.getAllUsers().map { friendEntities ->
        friendEntities.map { friendEntity ->
            val recentTracks =
                friendsDao.getRecentTracksForUser(friendEntity.url).firstOrNull() ?: emptyList()
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
        return apiRepository.getUserRecentTracks(user)
    }

    suspend fun deleteFriends() {
        friendsDao.deleteAllFriends()
    }
}
