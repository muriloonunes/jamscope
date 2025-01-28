package com.murile.nowplaying.data.repository

import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.api.Resource
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.session.DataStoreManager
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiRequest: ApiRequest,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun getUserProfile(): Profile? {
        return dataStoreManager.getUserProfile()
    }

    suspend fun saveUserProfile(profile: Profile) {
        dataStoreManager.saveUserProfile(profile)
    }

    suspend fun clearUserSession() {
        dataStoreManager.clearUserSession()
    }

    suspend fun isUserLoggedIn(): Boolean {
        return dataStoreManager.isUserLoggedIn()
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

    suspend fun getRecentTracks(user: User) {
        return apiRequest.getRecentTracks(user)
    }
}