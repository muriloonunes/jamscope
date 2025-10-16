package com.mno.jamscope.data.repository

import com.mno.jamscope.data.api.AuthRequest
import com.mno.jamscope.data.api.ProfileRequest
import com.mno.jamscope.data.api.UserRequest
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.User
import javax.inject.Inject

class ApiRepository @Inject constructor(
    private val authRequest: AuthRequest,
    private val profileRequest: ProfileRequest,
    private val userRequest: UserRequest,
) {
    suspend fun authenticateMobile(
        username: String,
        password: String,
        method: String,
    ): Resource<User> {
        return authRequest.authenticateMobile(
            username = username,
            password = password,
            method = method
        )
    }

    suspend fun authenticateWeb(token: String, method: String) {
        return authRequest.authenticateWeb(token, method)
    }

    suspend fun isStillAuthenticated(user: User): Boolean {
        return authRequest.isStillAuthenticated(user, "getMobileSession")
    }

    suspend fun getUserInfo(user: User) {
        profileRequest.getUserInfo(user)
    }

    suspend fun getProfileRecentTracks(user: User): Resource<Unit> {
        return profileRequest.getRecentTracks(user)
    }

    suspend fun getProfileFriends(username: String): Resource<List<Friend>> {
        return profileRequest.getProfileFriends(username)
    }

    suspend fun getFriendRecentTracks(friend: Friend) {
        userRequest.getFriendRecentTracks(friend)
    }
}