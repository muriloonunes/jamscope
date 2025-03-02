package com.mno.jamscope.data.repository

import com.mno.jamscope.data.api.AuthRequest
import com.mno.jamscope.data.api.ProfileRequest
import com.mno.jamscope.data.api.UserRequest
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.model.User
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val authRequest: AuthRequest,
    private val profileRequest: ProfileRequest,
    private val userRequest: UserRequest
) {
    suspend fun authenticate(username: String, password: String, method: String): Resource<Profile> {
        return authRequest.authenticate(username = username, password = password, method = method)
    }

    suspend fun isStillAuthenticated(profile: Profile): Boolean {
        return authRequest.isStillAuthenticated(profile, "auth.getMobileSession")
    }

    suspend fun getProfileInfo(profile: Profile) {
        profileRequest.getProfileInfo(profile)
    }

    suspend fun getProfileRecentTracks(profile: Profile): Resource<Unit> {
        return profileRequest.getRecentTracks(profile)
    }

    suspend fun getProfileFriends(username: String): Resource<List<User>> {
        return profileRequest.getProfileFriends(username)
    }

    suspend fun getUserRecentTracks(user: User) {
        userRequest.getUserRecentTracks(user)
    }
}