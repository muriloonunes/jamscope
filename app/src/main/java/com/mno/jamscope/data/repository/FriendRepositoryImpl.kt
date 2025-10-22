package com.mno.jamscope.data.repository

import android.content.Context
import com.mno.jamscope.data.local.datastore.SettingsDataStore
import com.mno.jamscope.data.local.db.dao.FriendsDao
import com.mno.jamscope.data.local.db.dao.TrackDao
import com.mno.jamscope.data.local.db.mapper.toDomain
import com.mno.jamscope.data.local.db.mapper.toEntity
import com.mno.jamscope.data.local.db.mapper.toFriendEntity
import com.mno.jamscope.data.remote.api.LastFmServiceApi
import com.mno.jamscope.data.remote.mapper.toTrack
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.Resource.Error
import com.mno.jamscope.domain.handleError
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.repository.FriendRepository
import com.mno.jamscope.features.friends.ui.SortingType
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val serviceApi: LastFmServiceApi,
    private val settingsDataStore: SettingsDataStore,
    private val friendsDao: FriendsDao,
    private val trackDao: TrackDao,
    @param:ApplicationContext private val context: Context,
) : FriendRepository {
    override suspend fun getRecentTracks(username: String): Resource<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = serviceApi.getRecentTracks(username)
                val tracks = response.recenttracks.track.map { it.toTrack() }
                Resource.Success(tracks)
            } catch (e: ConnectTimeoutException) {
                e.printStackTrace()
                Error(context.handleError(525))
            } catch (e: HttpRequestTimeoutException) {
                e.printStackTrace()
                Error(context.handleError(504))
            } catch (e: UnresolvedAddressException) {
                e.printStackTrace()
                Error(context.handleError(666))
            } catch (e: Exception) {
                e.printStackTrace()
                Error(context.handleError(0))
            }
        }
    }

    override suspend fun getFriends(): List<Friend> {
        return withContext(Dispatchers.IO) {
            val friendEntities = friendsDao.getFriends()
            friendEntities.map { friend ->
                val tracks = trackDao.getFriendTracks(friend.profileUrl)
                friend.toDomain(tracks)
            }
        }
    }

    override suspend fun saveFriends(friends: List<Friend>) {
        withContext(Dispatchers.IO) {
            val friendEntities = friends.map { it.toEntity() }
            friendsDao.insertFriends(friendEntities)

            val allTracks = friends.flatMap { friend ->
                friend.recentTracks.map { it.toFriendEntity(friend.profileUrl) }
            }
            trackDao.insertTracks(allTracks)
        }
    }

    override suspend fun saveSortingType(sortingType: SortingType) {
        withContext(Dispatchers.IO) {
            settingsDataStore.saveSortingType(sortingType)
        }
    }

    override suspend fun getSortingType(): SortingType {
        return withContext(Dispatchers.IO) {
            settingsDataStore.getSortingType()
        }
    }

    override suspend fun clearFriends() {
        withContext(Dispatchers.IO) {
            friendsDao.deleteAllFriends()
        }
    }
}