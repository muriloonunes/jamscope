package com.mno.jamscope.domain.repository

import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.User

interface LoginRepository {
    suspend fun login(username: String, password: String): Resource<User>
    suspend fun login(token: String)
    suspend fun isStillAuthenticated(
        username: String,
        password: String,
        currentSessionKey: String,
    ): Boolean
    suspend fun isUserLoggedIn(): Boolean
}