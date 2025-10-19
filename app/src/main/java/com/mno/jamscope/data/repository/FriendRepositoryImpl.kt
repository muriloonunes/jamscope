package com.mno.jamscope.data.repository

import android.content.Context
import com.mno.jamscope.data.local.datastore.SettingsDataStore
import com.mno.jamscope.data.local.datastore.UserDataStore
import com.mno.jamscope.data.local.db.dao.FriendsDao
import com.mno.jamscope.data.local.mapper.toDomain
import com.mno.jamscope.data.mapper.toTrack
import com.mno.jamscope.data.remote.api.LastFmServiceApi
import com.mno.jamscope.data.remote.api.handleError
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.Resource.Error
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.repository.FriendRepository
import com.mno.jamscope.util.SortingType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val serviceApi: LastFmServiceApi,
    private val userDataStore: UserDataStore,
    private val settingsDataStore: SettingsDataStore,
    private val friendsDao: FriendsDao,
    @param:ApplicationContext private val context: Context,
) : FriendRepository {
    override suspend fun getRecentTracks(username: String): Resource<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = serviceApi.getRecentTracks(username)
                val tracks = response.recenttracks.track.map { it.toTrack() }
                Resource.Success(tracks)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(context.handleError(0))
            }
        }
    }

    override suspend fun getFriends(): List<Friend> {
        return withContext(Dispatchers.IO) {
            try {
                val profile = userDataStore.getUserProfile() //talvez eu nao deveria fazer isso aqui?
                profile?.friends ?: throw NoSuchElementException()
            } catch (_: NoSuchElementException) {
                val friends = friendsDao.getFriends().map {
                    val recentTracks =
                        friendsDao.getRecentTracksForUser(it.url)
                    it.toDomain(recentTracks)
                }
                friends
                //TODO achar um metodo de salvar o profile com os amigos atualizados do room novamente mas o metodo que salva o profile esta em outro repository
            }
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