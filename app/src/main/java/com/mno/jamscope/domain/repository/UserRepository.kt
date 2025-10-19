package com.mno.jamscope.domain.repository

import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.model.User

interface UserRepository {
    suspend fun getUserInfoFromApi(username: String): Resource<User>
    suspend fun getUserFriends(username: String): Resource<List<Friend>>
    suspend fun getRecentTracks(username: String): Resource<List<Track>>
    suspend fun getUser(): User
    suspend fun saveUser(user: User)
    suspend fun clearUserSession()
}