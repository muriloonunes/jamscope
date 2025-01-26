package com.murile.nowplaying.data.api

import com.murile.nowplaying.data.api.Resource.Error
import com.murile.nowplaying.data.api.Resource.Success
import com.murile.nowplaying.data.model.ApiResponse
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.model.Session
import com.murile.nowplaying.data.model.SessionResponse
import com.murile.nowplaying.data.model.Token
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.nio.channels.UnresolvedAddressException
import java.security.MessageDigest
import javax.inject.Inject

class ApiRequest @Inject constructor(
    private val exceptions: Exceptions
) {
    companion object {
        private const val BASE_URL = "https://ws.audioscrobbler.com/2.0/?"
        private const val FORMAT_JSON = "format=json"
    }
    private val json = Json { ignoreUnknownKeys = true }

    private fun generateApiSig(
        username: String, password: String
    ): String {
        val apiSignature =
            "api_key${Token.LAST_FM_API_KEY}" + "methodauth.getMobileSession" + "password$password" + "username$username" + Token.LAST_FM_SECRET

        val md5Digest = MessageDigest.getInstance("MD5")
        val hashBytes = md5Digest.digest(apiSignature.toByteArray(Charsets.UTF_8))

        val hexString = StringBuilder()
        for (byte in hashBytes) {
            val hex = Integer.toHexString(0xFF and byte.toInt())
            hexString.append(hex.padStart(2, '0'))
        }
        return hexString.toString()
    }

    suspend fun autenticar(
        username: String,
        password: String,
        method: String,
    ): Resource<Profile> {
        return try {
            val requestUrl = buildAuthUrl(username, password, method)

            val response = withContext(Dispatchers.IO) {
                HttpClientProvider.client.post(requestUrl) {
                    headers {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                }
            }
            if (!response.status.isSuccess()) {
                val errorMessage = handleError(response.status.value)
                Error(errorMessage)
            } else {
                val responseBodyString = response.bodyAsText()
                val profile = withContext(Dispatchers.IO) {
                    processSessionResponse(responseBodyString, password)
                }
                profile?.let {
                    Success(it)
                } ?: Error("Failed to process session response")
            }
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
            Error(handleError(666))
        } catch (e: Exception) {
            e.printStackTrace()
            Error(handleError(0))
        }
    }

    private fun buildAuthUrl(username: String, password: String, method: String): String {
        val apiSig = generateApiSig(username, password)
        val urlParams =
            "method=auth.$method&api_key=${Token.LAST_FM_API_KEY}&password=$password&username=$username&api_sig=$apiSig"
        return "$BASE_URL$FORMAT_JSON&$urlParams"
    }

    private fun handleError(statusCode: Int): String {
        return exceptions.handleError(statusCode)
    }

    private suspend fun processSessionResponse(
        responseBodyString: String, password: String
    ): Profile? {
        return try {
            withContext(Dispatchers.IO) {
                val sessionResponse = json.decodeFromString<SessionResponse>(responseBodyString)

                val profile = Profile(
                    username = sessionResponse.session.name,
                    subscriber = sessionResponse.session.subscriber,
                    session = Session(key = sessionResponse.session.key),
                    senha = password
                )
                val links = getUserInfo(profile.username)
                profile.imageUrl = links[0]
                profile.profileUrl = links[1]

                profile
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserInfo(
        username: String
    ): Array<String> {
        return withContext(Dispatchers.IO) {
            val urlParametro = "method=user.getinfo&user=$username&api_key=${Token.LAST_FM_API_KEY}"
            val requestUrl = "$BASE_URL$FORMAT_JSON&$urlParametro"

            val links = arrayOf("", "")
            try {
                val response: ApiResponse = HttpClientProvider.client.get(requestUrl).body()
                val largeImageUrl = response.user.image.find { it.size == "large" }?.url
                val profileUrl = response.user.url
                links[0] = largeImageUrl
                    ?: "https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png"
                links[1] = profileUrl

            } catch (e: Exception) {
                e.printStackTrace()
            }
            links
        }
    }
}