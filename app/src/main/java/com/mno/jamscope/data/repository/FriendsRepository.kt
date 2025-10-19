package com.mno.jamscope.data.repository

import com.mno.jamscope.data.local.datastore.UserDataStoreManager
import com.mno.jamscope.data.local.db.dao.FriendsDao
import com.mno.jamscope.data.local.mapper.toDomain
import com.mno.jamscope.data.local.mapper.toEntity
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.util.SortingType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Deprecated(
    "This class is deprecated, use FriendRepositoryImpl instead",
    ReplaceWith("FriendRepositoryImpl"),
)
class FriendsRepository @Inject constructor(
    private val userDataStoreManager: UserDataStoreManager,
    private val friendsDao: FriendsDao,
    private val apiRepository: ApiRepository,
) {
    suspend fun saveSortingType(sortingType: SortingType) {
        userDataStoreManager.saveSortingType(sortingType)
    }

    suspend fun getSortingType(): SortingType {
        return userDataStoreManager.getSortingType()
    }

    fun getFriendsFromDataStore(): Flow<List<Friend>> = flow {
        val profile = userDataStoreManager.getUserProfile()
        emit(profile?.friends ?: emptyList())
    }

    fun getCachedFriends(): Flow<List<Friend>> = friendsDao.getAllFriends().map { friendEntities ->
        friendEntities.map { friendEntity ->
            val recentTracks =
                friendsDao.getRecentTracksForUser(friendEntity.url)
            friendEntity.toDomain(recentTracks)
        }
    }

    fun getCachedRecentTracks(userUrl: String): List<Track> {
        return friendsDao.getRecentTracksForUser(userUrl).map { it.toDomain() }
    }

    suspend fun cacheFriends(users: List<Friend>) {
        val friends = users.map { it.toEntity() }
        friendsDao.insertUsers(friends)
    }

    suspend fun cacheRecentTracks(userUrl: String, tracks: List<Track>) {
        val trackEntities = tracks.map { it.toEntity(userUrl) }
        friendsDao.deleteRecentTracksForUser(userUrl)
        friendsDao.insertRecentTracks(trackEntities)
    }

    suspend fun getRecentTracks(friend: Friend) {
        return apiRepository.getFriendRecentTracks(friend)
    }

    suspend fun deleteFriends() {
        friendsDao.deleteAllFriends()
    }
}
