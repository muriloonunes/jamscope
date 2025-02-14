package com.murile.nowplaying.data.repository

import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.model.Resource
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.session.UserDataStoreManager
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiRequest: ApiRequest,
    private val userDataStoreManager: UserDataStoreManager,
) {
    suspend fun getUserProfile(): Profile? {
        return userDataStoreManager.getUserProfile()
    }

    suspend fun saveUserProfile(profile: Profile) {
        userDataStoreManager.saveUserProfile(profile)
    }

    suspend fun clearUserSession() {
        userDataStoreManager.clearUserSession()
    }

    suspend fun isUserLoggedIn(): Boolean {
        return userDataStoreManager.isUserLoggedIn()
    }

    suspend fun authenticate(username: String, password: String, method: String): Resource<Profile> {
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
}