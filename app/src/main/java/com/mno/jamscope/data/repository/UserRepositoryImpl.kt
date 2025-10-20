package com.mno.jamscope.data.repository

import android.content.Context
import com.mno.jamscope.data.local.datastore.SettingsDataStore
import com.mno.jamscope.data.local.datastore.UserDataStore
import com.mno.jamscope.data.local.db.dao.TrackDao
import com.mno.jamscope.data.local.db.dao.UserDao
import com.mno.jamscope.data.local.db.mapper.toDomain
import com.mno.jamscope.data.local.db.mapper.toEntity
import com.mno.jamscope.data.local.db.mapper.toUserEntity
import com.mno.jamscope.data.remote.api.LastFmServiceApi
import com.mno.jamscope.domain.handleError
import com.mno.jamscope.data.remote.mapper.toFriend
import com.mno.jamscope.data.remote.mapper.toTrack
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.Resource.Error
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.model.User
import com.mno.jamscope.domain.model.mergeWith
import com.mno.jamscope.domain.repository.UserRepository
import com.mno.jamscope.util.Stuff
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val serviceApi: LastFmServiceApi,
    private val userDataStore: UserDataStore,
    private val settingsDataStore: SettingsDataStore,
    private val userDao: UserDao,
    private val trackDao: TrackDao,
    @param:ApplicationContext private val context: Context,
) : UserRepository {
    override suspend fun getUserInfoFromApi(username: String): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = serviceApi.getUserInfo(username)
                val dto = response.user
                val extraLargeImageUrl = dto.image.find { it.size == "extralarge" }?.url
                    ?: Stuff.DEFAULT_PROFILE_IMAGE
                val largeImageUrl = dto.image.find { it.size == "large" }?.url
                    ?: Stuff.DEFAULT_PROFILE_IMAGE
                val profileUrl = dto.url
                val country = dto.country
                val realname = dto.realname
                val playcount = dto.playcount
                val subscriber = dto.subscriber
                val apiUser = User(
                    username = username,
                    realName = realname,
                    country = country ?: "",
                    profileUrl = profileUrl,
                    largeImageUrl = largeImageUrl,
                    extraLargeImageUrl = extraLargeImageUrl,
                    playcount = playcount ?: 0,
                    subscriber = subscriber == 1,
                    sessionKey = "",
                    password = ""
                )

                val currentUser = getUser()
                val mergedUser = currentUser.mergeWith(apiUser)

                Resource.Success(mergedUser)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(context.handleError(0))
            }
        }
    }

    override suspend fun getUserFriends(username: String): Resource<List<Friend>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = serviceApi.getUserFriends(username)
                val friends = response.friends.user.map { it.toFriend() }
                Resource.Success(friends)
            } catch (e: UnresolvedAddressException) {
                e.printStackTrace()
                Error(context.handleError(666))
            } catch (e: Exception) {
                e.printStackTrace()
                Error(context.handleError(999))
            }
        }
    }

    override suspend fun getRecentTracks(username: String): Resource<List<Track>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = serviceApi.getRecentTracks(username, limit = 100)
                val tracks = response.recenttracks.track.map { it.toTrack() }
                Resource.Success(tracks)
            } catch (e: Exception) {
                e.printStackTrace()
                Error(context.handleError(0))
            }
        }
    }

    override suspend fun getUser(): User = withContext(Dispatchers.IO) {
        val user = userDataStore.getUserProfile()
        if (user != null) return@withContext user

        val fallbackProfile = userDao.getUser()
        val tracks = trackDao.getUserTracks(fallbackProfile.url)
        val restored = fallbackProfile.toDomain(tracks)

        saveUser(restored)
        restored
    }

    override suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            userDataStore.saveUserProfile(user)
            userDao.insertUser(user.toEntity())
            val trackEntities = user.recentTracks.map { it.toUserEntity(user.profileUrl) }
            trackDao.insertTracks(trackEntities)
        }
    }

    override suspend fun clearUserSession() {
        withContext(Dispatchers.IO) {
            userDataStore.clearUserSession()
            settingsDataStore.clearUserPrefs()
            userDao.deleteUserProfile()
            trackDao.deleteAllRecentTracks()
        }
    }
}