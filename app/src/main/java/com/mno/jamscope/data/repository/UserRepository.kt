package com.mno.jamscope.data.repository

import com.mno.jamscope.data.api.ApiRequest
import com.mno.jamscope.data.local.dao.UserProfileDao
import com.mno.jamscope.data.local.toProfile
import com.mno.jamscope.data.local.toRecentTrackEntity
import com.mno.jamscope.data.local.toUserProfileEntity
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.data.model.User
import com.mno.jamscope.data.session.UserDataStoreManager
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
            val userProfileEntity = userProfileDao.getUserProfile()
            @Suppress("SENSELESS_COMPARISON")
            /**
             * this will be null if the user opens the app offline after an app update that changed the database version
             * since the database version upgrade is destructive, user profile entity will not exist
             * therefore pointing to null and causing a crash
             */
            if (userProfileEntity != null) {
                val userUrl = userProfileEntity.url
                val recentTracks = userProfileDao.getRecentTracksForUser(userUrl)
                return@withContext userProfileEntity.toProfile(recentTracks)
            } else {
                val fallbackProfile = userDataStoreManager.getUserProfile()!!
                if (fallbackProfile.profileUrl != null) {
                    cacheUserProfile(fallbackProfile)
                    return@withContext fallbackProfile
                }
                else {
                    throw IllegalStateException("No user profile found")
                }
            }
        }
    }

    suspend fun cacheRecentTracks(userUrl: String, tracks: List<Track>) {
        val trackEntities = tracks.map { it.toRecentTrackEntity(userUrl) }
        userProfileDao.deleteRecentTracksForUser(userUrl)
        userProfileDao.insertRecentTracks(trackEntities)
    }
}