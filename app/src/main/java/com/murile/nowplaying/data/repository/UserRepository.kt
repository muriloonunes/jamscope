package com.murile.nowplaying.data.repository

import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.local.dao.UserProfileDao
import com.murile.nowplaying.data.local.toProfile
import com.murile.nowplaying.data.local.toRecentTrackEntity
import com.murile.nowplaying.data.local.toUserProfileEntity
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.model.Resource
import com.murile.nowplaying.data.model.Track
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.session.UserDataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiRequest: ApiRequest,
    private val userDataStoreManager: UserDataStoreManager,
    private val userProfileDao: UserProfileDao
) {
    suspend fun getUserProfile(): Profile? {
        return userDataStoreManager.getUserProfile()
    }

    suspend fun saveUserProfile(profile: Profile) {
        userDataStoreManager.saveUserProfile(profile)
        cacheUserProfile(profile)
    }

    suspend fun clearUserSession() {
        userDataStoreManager.clearUserSession()
        userProfileDao.deleteAllRecentTracks()
        userProfileDao.deleteUserProfile()
    }

    suspend fun isUserLoggedIn(): Boolean {
        return userDataStoreManager.isUserLoggedIn()
    }

    suspend fun authenticate(
        username: String,
        password: String,
        method: String
    ): Resource<Profile> {
        return apiRequest.autenticar(username, password, method)
    }

    suspend fun getUserFriends(username: String): Resource<List<User>> {
        return apiRequest.getUserFriends(username)
    }

    suspend fun getUserInfo(profile: Profile) {
        return apiRequest.getUserInfo(profile)
    }

    suspend fun getRecentTracks(profile: Profile): Resource<Unit> {
        return apiRequest.getRecentTracks(profile)
    }

    suspend fun cacheUserProfile(profile: Profile) {
        val userProfile = profile.toUserProfileEntity()
        userProfileDao.insertUserProfile(userProfile)
    }

    suspend fun getCachedUserProfile(): Profile {
        return withContext(Dispatchers.IO) {
            val userProfile = userProfileDao.getUserProfile()
            val recentTracks = userProfileDao.getRecentTracksForUser(userProfile.url)
            return@withContext userProfileDao.getUserProfile().toProfile(recentTracks)
        }
    }

    suspend fun cacheRecentTracks(userUrl: String, tracks: List<Track>) {
        val trackEntities = tracks.map { it.toRecentTrackEntity(userUrl) }
        userProfileDao.deleteRecentTracksForUser(userUrl)
        userProfileDao.insertRecentTracks(trackEntities)
    }
}