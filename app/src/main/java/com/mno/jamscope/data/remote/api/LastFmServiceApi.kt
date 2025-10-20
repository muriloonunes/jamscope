package com.mno.jamscope.data.remote.api

import com.mno.jamscope.data.remote.dto.ApiResponseDto
import com.mno.jamscope.data.remote.dto.RecentTracksResponseDto
import com.mno.jamscope.data.remote.dto.SessionResponseDto
import com.mno.jamscope.data.remote.dto.UserFriendsResponseDto

interface LastFmServiceApi {
    suspend fun getMobileSession(username: String, password: String): SessionResponseDto
    suspend fun getWebSession(token: String): SessionResponseDto
    suspend fun isStillAuthenticated(
        username: String,
        password: String,
        currentSessionKey: String,
    ): SessionResponseDto
    suspend fun getRecentTracks(username: String, limit: Int = 50): RecentTracksResponseDto
    suspend fun getUserInfo(username: String): ApiResponseDto
    suspend fun getUserFriends(username: String): UserFriendsResponseDto
}