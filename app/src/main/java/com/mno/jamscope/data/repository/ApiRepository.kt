package com.mno.jamscope.data.repository

import com.mno.jamscope.data.remote.api.AuthRequest
import com.mno.jamscope.data.remote.api.UserRequest
import com.mno.jamscope.data.remote.api.FriendRequest
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.User
import javax.inject.Inject

@Deprecated(
    "This class is deprecated, use LoginRepositoryImpl instead",
    ReplaceWith("LoginRepositoryImpl"),
)
class ApiRepository @Inject constructor(
    private val authRequest: AuthRequest,
    private val userRequest: UserRequest,
    private val friendRequest: FriendRequest,
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
        userRequest.getUserInfo(user)
    }

    suspend fun getProfileRecentTracks(user: User): Resource<Unit> {
        return userRequest.getRecentTracks(user)
    }

    suspend fun getProfileFriends(username: String): Resource<List<Friend>> {
        return userRequest.getUserFriends(username)
    }

    suspend fun getFriendRecentTracks(friend: Friend) {
        friendRequest.getFriendRecentTracks(friend)
    }
}