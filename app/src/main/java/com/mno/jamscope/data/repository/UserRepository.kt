package com.mno.jamscope.data.repository

import com.mno.jamscope.data.local.db.dao.UserProfileDao
import com.mno.jamscope.data.local.mapper.toDomain
import com.mno.jamscope.data.local.mapper.toEntity
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.data.local.datastore.UserDataStoreManager
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Deprecated(
    "This class is deprecated, use UserRepositoryImpl instead",
    ReplaceWith("UserRepositoryImpl"),
)
class UserRepository @Inject constructor(
    private val apiRepository: ApiRepository,
    private val userDataStoreManager: UserDataStoreManager,
    private val userProfileDao: UserProfileDao,
) {
    suspend fun getUserProfile(): User? {
        return userDataStoreManager.getUserProfile()
    }

    suspend fun saveUserProfile(user: User) {
        userDataStoreManager.saveUserProfile(user)
        //TODO voltar a cachear
//        cacheUserProfile(profile)
    }

    suspend fun clearUserSession() {
        userDataStoreManager.clearUserSession()
        userProfileDao.deleteAllRecentTracks()
        userProfileDao.deleteUserProfile()
    }

    suspend fun isUserLoggedIn(): Boolean {
        return userDataStoreManager.isUserLoggedIn()
    }

    suspend fun authenticateMobile(
        username: String,
        password: String,
        method: String,
    ): Resource<User> {
        return apiRepository.authenticateMobile(username, password, method)
    }

    suspend fun authenticateWeb(
        token: String,
        method: String,
    ) {
        return apiRepository.authenticateWeb(token, method)
    }

    suspend fun getUserFriends(username: String): Resource<List<Friend>> {
        return apiRepository.getProfileFriends(username)
    }

    suspend fun getUserInfo(user: User) {
        return apiRepository.getUserInfo(user)
    }

    suspend fun getRecentTracks(user: User): Resource<Unit> {
        return apiRepository.getProfileRecentTracks(user)
    }

    private suspend fun cacheUserProfile(user: User) {
        val userProfile = user.toEntity()
        userProfileDao.insertUserProfile(userProfile)
    }

    suspend fun getCachedUserProfile(): User {
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
                return@withContext userProfileEntity.toDomain(recentTracks)
            } else {
                val fallbackProfile = userDataStoreManager.getUserProfile()!!
                if (fallbackProfile.profileUrl != null) {
                    //TODO voltar a cachear
//                    cacheUserProfile(fallbackProfile)
                    return@withContext fallbackProfile
                } else {
                    throw IllegalStateException("No user profile found")
                }
            }
        }
    }

    suspend fun cacheRecentTracks(userUrl: String, tracks: List<Track>) {
        val trackEntities = tracks.map { it.toEntity(userUrl) }
        userProfileDao.deleteRecentTracksForUser(userUrl)
        userProfileDao.insertRecentTracks(trackEntities)
    }

    suspend fun getAppVersion(): Int {
        return userDataStoreManager.getAppVersion()
    }

    suspend fun saveAppVersion(version: Int) {
        userDataStoreManager.saveAppVersion(version)
    }
}