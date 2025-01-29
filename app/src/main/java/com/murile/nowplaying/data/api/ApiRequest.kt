package com.murile.nowplaying.data.api

import android.util.Log
import com.murile.nowplaying.data.api.Resource.Error
import com.murile.nowplaying.data.api.Resource.Success
import com.murile.nowplaying.data.model.ApiResponse
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.model.RecentTracksResponse
import com.murile.nowplaying.data.model.Session
import com.murile.nowplaying.data.model.SessionResponse
import com.murile.nowplaying.data.model.Token
import com.murile.nowplaying.data.model.User
import com.murile.nowplaying.data.model.UserFriendsResponse
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
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.channels.UnresolvedAddressException
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class ApiRequest @Inject constructor(
    private val exceptions: Exceptions
) {
    companion object {
        private const val BASE_URL = "https://ws.audioscrobbler.com/2.0/?"
        private const val FORMAT_JSON = "format=json"
        private const val DEFAULT_PROFILE_IMAGE = "https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png"
        private val JSON = Json { ignoreUnknownKeys = true }
    }

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

    private suspend fun processSessionResponse(responseBodyString: String, password: String): Profile? {
        return try {
            val sessionResponse = JSON.decodeFromString<SessionResponse>(responseBodyString)
            val profile = Profile(
                username = sessionResponse.session.name,
                subscriber = sessionResponse.session.subscriber,
                session = Session(key = sessionResponse.session.key),
                senha = password,
                imageUrl = "",
                profileUrl = ""
            )
            getUserInfo(profile)
            profile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getUserInfo(
        profile: Profile
    ) {
         withContext(Dispatchers.IO) {
            val urlParametro = "method=user.getinfo&user=${profile.username}&api_key=${Token.LAST_FM_API_KEY}"
            val requestUrl = "$BASE_URL$FORMAT_JSON&$urlParametro"

            try {
                val response: ApiResponse = HttpClientProvider.client.get(requestUrl).body()
                val largeImageUrl = response.user.image.find { it.size == "large" }?.url ?: DEFAULT_PROFILE_IMAGE
                val profileUrl = response.user.url
                val country = response.user.country
                val realname = response.user.realname
                val playcount = response.user.playcount
                val subscriber = response.user.subscriber

                profile.imageUrl = largeImageUrl
                profile.profileUrl = profileUrl
                profile.country = if (country=="None") "" else country
                profile.realname = realname.ifEmpty { "" }
                profile.playcount = playcount
                if (subscriber != null) {
                    profile.subscriber = subscriber
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getUserFriends(username: String): Resource<List<User>> {
        return try {
            val urlParametro =
                "method=user.getfriends&user=$username&api_key=${Token.LAST_FM_API_KEY}"
            val requestUrl = "$BASE_URL$FORMAT_JSON&$urlParametro"
            val response = withContext(Dispatchers.IO) {
                HttpClientProvider.client.post(requestUrl) {
                    headers {
                        append(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    }
                }
            }
            if (!response.status.isSuccess()) {
                Log.i("getUserFriends", "Error: ${response.bodyAsText()}")
                val jsonResponse = JSON.parseToJsonElement(response.bodyAsText()).jsonObject
                val errorValue = jsonResponse["error"]?.jsonPrimitive?.intOrNull
                Error(handleError(errorValue!!))
            } else {
                val userFriendsResponse = JSON.decodeFromString<UserFriendsResponse>(response.bodyAsText())
                val users = userFriendsResponse.friends.user.map { friend ->
                    User(
                        name = friend.name,
                        image = friend.image,
                        url = friend.url,
                        realname = friend.realname,
                        country = friend.country,
                        playcount = friend.playcount,
                        subscriber = friend.subscriber
                    )
                }
                Success(users)
            }
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
            Error(handleError(666))
        } catch (e: Exception) {
            e.printStackTrace()
            Error("Failed to fetch user friends")
        }
    }

    suspend fun getRecentTracks(
        user: User
    ) {
        val urlParametro =
            "method=user.getrecenttracks&user=${user.name}&api_key=${Token.LAST_FM_API_KEY}&limit=5"
        val requestUrl = "$BASE_URL$FORMAT_JSON&$urlParametro"
        try {
            val response = withContext(Dispatchers.IO) {
                HttpClientProvider.client.post(requestUrl) {
                    headers {
                        append(
                            HttpHeaders.ContentType,
                            ContentType.Application.Json.toString()
                        )
                    }
                }
            }
            val recentTracksResponse = JSON.decodeFromString<RecentTracksResponse>(response.bodyAsText())
            recentTracksResponse.recenttracks.track.forEach { track ->
                track.dateInfo?.let { dateInfo ->
                    val localDateTime = LocalDateTime.parse(dateInfo.formattedDate, DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))
                    val zonedDateTime = localDateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault())
                    val isoFormattedDate = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    dateInfo.formattedDate = isoFormattedDate
                }
            }
            user.recentTracks = recentTracksResponse.recenttracks
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}