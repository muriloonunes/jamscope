package com.mno.jamscope.data.repository

import android.content.Context
import com.mno.jamscope.data.local.datastore.UserDataStore
import com.mno.jamscope.data.remote.api.LastFmServiceApi
import com.mno.jamscope.data.remote.dto.ProfileDto
import com.mno.jamscope.data.remote.dto.SessionDto
import com.mno.jamscope.data.remote.mapper.toUser
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.Resource.Error
import com.mno.jamscope.domain.handleError
import com.mno.jamscope.domain.model.User
import com.mno.jamscope.domain.repository.LoginRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.channels.UnresolvedAddressException
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val serviceApi: LastFmServiceApi,
    private val userDataStore: UserDataStore,
    @param:ApplicationContext private val context: Context,
) : LoginRepository {
    override suspend fun login(
        username: String,
        password: String,
    ): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val sessionResponse = serviceApi.getMobileSession(username, password)
                val user = ProfileDto(
                    username = sessionResponse.session.name,
                    subscriber = sessionResponse.session.subscriber,
                    session = SessionDto(key = sessionResponse.session.key),
                    senha = password,
                    largeImageUrl = "",
                    extraLargeImageUrl = "",
                    profileUrl = "",
                    country = "",
                    realname = "",
                    playcount = 0
                ).toUser()
                Resource.Success(user)
            } catch (e: UnresolvedAddressException) {
                e.printStackTrace()
                Error(context.handleError(666))
            } catch (e: Exception) {
                e.printStackTrace()
                Error(context.handleError(0))
            }
        }
    }

    override suspend fun login(token: String): Resource<User> {
        return withContext(Dispatchers.IO) {
            try {
                val sessionResponse = serviceApi.getWebSession(token)
                val username = sessionResponse.session.name
                val user = ProfileDto(
                    username = username,
                    subscriber = sessionResponse.session.subscriber,
                    session = SessionDto(key = sessionResponse.session.key),
                    profileUrl = "https://www.last.fm/user/$username",
                    senha = "",
                    largeImageUrl = "",
                    extraLargeImageUrl = "",
                    country = "",
                    realname = "",
                    playcount = 0
                ).toUser()
                Resource.Success(user)
            } catch (e: ConnectTimeoutException) {
                e.printStackTrace()
                Error(context.handleError(525))
            } catch (e: UnresolvedAddressException) {
                e.printStackTrace()
                Error(context.handleError(666))
            } catch (e: Exception) {
                e.printStackTrace()
                Error(context.handleError(0))
            }
        }
    }

    override suspend fun isStillAuthenticated(
        username: String,
        password: String,
        currentSessionKey: String,
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val sessionResponse =
                    serviceApi.isStillAuthenticated(username, password, currentSessionKey)
                sessionResponse.session.key == currentSessionKey
            } catch (e: Exception) {
                e.printStackTrace()
                true
            }
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            userDataStore.isUserLoggedIn()
        }
    }
}