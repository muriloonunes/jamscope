package com.mno.jamscope.data.remote.api

import android.util.Log
import com.mno.jamscope.data.remote.dto.ApiResponseDto
import com.mno.jamscope.data.remote.dto.RecentTracksResponseDto
import com.mno.jamscope.data.remote.dto.SessionResponseDto
import com.mno.jamscope.data.remote.dto.UserFriendsResponseDto
import com.mno.jamscope.util.Stuff
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import javax.inject.Inject

class LastFmServiceApiImpl @Inject constructor(
    private val client: HttpClient,
    private val json: Json,
) : LastFmServiceApi {
    override suspend fun getMobileSession(
        username: String,
        password: String,
    ): SessionResponseDto {
        val method = ApiMethods.AUTH_MOBILE
        val apiSig = generateMobileApiSig(username, password, method)
        val response = client.post {
            parameter("method", method)
            parameter("api_key", Stuff.LAST_KEY)
            parameter("password", password)
            parameter("username", username)
            parameter("api_sig", apiSig)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    override suspend fun getWebSession(token: String) {
        //TODO adicionar retorno
        val method = ApiMethods.AUTH_WEB
        val apiSig = generateWebApiSig(token, method)
        val response = client.post {
            parameter("method", method)
            parameter("api_key", Stuff.LAST_KEY)
            parameter("token", token)
            parameter("api_sig", apiSig)
        }
        if (!response.status.isSuccess()) {
            Log.e("ApiRequest", "autenticarWeb: ${response.bodyAsText()}")
        } else {
            Log.d("ApiRequest", "autenticarWebSuccess: ${response.bodyAsText()}")
        }
    }

    override suspend fun isStillAuthenticated(
        username: String,
        password: String,
        currentSessionKey: String,
    ): SessionResponseDto {
        //TODO mudar retorno pra sessionresponse e fazer a verificacao no repository
        val method = ApiMethods.AUTH_MOBILE
        val apiSig = generateMobileApiSig(username, password, method)
        val response = client.post {
            parameter("method", method)
            parameter("api_key", Stuff.LAST_KEY)
            parameter("password", password)
            parameter("username", username)
            parameter("api_sig", apiSig)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    override suspend fun getRecentTracks(
        username: String,
        limit: Int,
    ): RecentTracksResponseDto {
        val response = client.post {
            parameter("method", ApiMethods.GET_TRACKS)
            parameter("user", username)
            parameter("api_key", Stuff.LAST_KEY)
            parameter("limit", limit)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    override suspend fun getUserInfo(username: String): ApiResponseDto {
        val response = client.get {
            parameter("method", ApiMethods.GET_INFO)
            parameter("user", username)
            parameter("api_key", Stuff.LAST_KEY)
        }
        return json.decodeFromString(response.bodyAsText())
    }

    override suspend fun getUserFriends(username: String): UserFriendsResponseDto {
        val response = client.post {
            parameter("method", ApiMethods.GET_FRIENDS)
            parameter("user", username)
            parameter("api_key", Stuff.LAST_KEY)
        }
        return json.decodeFromString(response.bodyAsText())
    }
}
