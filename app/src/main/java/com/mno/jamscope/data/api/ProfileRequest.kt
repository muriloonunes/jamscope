package com.mno.jamscope.data.api

import android.util.Log
import com.mno.jamscope.data.model.ApiResponse
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.RecentTracksResponse
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.model.Resource.Error
import com.mno.jamscope.data.model.Resource.Success
import com.mno.jamscope.data.model.User
import com.mno.jamscope.data.model.UserFriendsResponse
import com.mno.jamscope.util.Stuff
import com.mno.jamscope.util.Stuff.BASE_URL
import com.mno.jamscope.util.Stuff.FORMAT_JSON
import com.mno.jamscope.util.Stuff.JSON
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
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.channels.UnresolvedAddressException
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class ProfileRequest @Inject constructor(
    private val exceptions: Exceptions
) {
    suspend fun getProfileInfo(profile: Profile) {
        withContext(Dispatchers.IO) {
            val urlParametro = "method=user.getinfo&user=${profile.username}&api_key=${Stuff.LAST_KEY}"
            val requestUrl = "$BASE_URL$FORMAT_JSON&$urlParametro"

            try {
                val response: ApiResponse = HttpClientProvider.client.get(requestUrl).body()
                val largeImageUrl = response.user.image.find { it.size == "large" }?.url
                    ?: Stuff.DEFAULT_PROFILE_IMAGE
                val profileUrl = response.user.url
                val country = response.user.country
                val realname = response.user.realname
                val playcount = response.user.playcount
                val subscriber = response.user.subscriber

                profile.imageUrl = largeImageUrl
                profile.profileUrl = profileUrl
                profile.country = if (country == "None") "" else country
                profile.realname = realname.ifEmpty { "" }
                profile.playcount = playcount
                if (subscriber != null) profile.subscriber = subscriber
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getProfileFriends(username: String): Resource<List<User>> {
        return try {
            val urlParametro =
                "method=user.getfriends&user=$username&api_key=${Stuff.LAST_KEY}"
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
                Log.e("ApiRequest", "getUserFriends: ${response.bodyAsText()}")
                val jsonResponse = JSON.parseToJsonElement(response.bodyAsText()).jsonObject
                val errorValue = jsonResponse["error"]?.jsonPrimitive?.intOrNull
                Error(exceptions.handleError(errorValue ?: 0))
            } else {
                val userFriendsResponse =
                    JSON.decodeFromString<UserFriendsResponse>(response.bodyAsText())
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
            Log.e("ApiRequest", "getUserFriendsError: $e")
            e.printStackTrace()
            Error(exceptions.handleError(666))
        } catch (e: Exception) {
            Log.e("ApiRequest", "getUserFriendsError: $e")
            e.printStackTrace()
            Error(exceptions.handleError(999))
        }
    }

    suspend fun getRecentTracks(profile: Profile): Resource<Unit> {
        val urlParametro =
            "method=user.getrecenttracks&user=${profile.username}&api_key=${Stuff.LAST_KEY}&limit=100"
        val requestUrl = "$BASE_URL$FORMAT_JSON&$urlParametro"
        return try {
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
                Log.e("ApiRequest", "getProfileRecentTracksError: ${response.bodyAsText()}")
                val jsonResponse = JSON.parseToJsonElement(response.bodyAsText()).jsonObject
                val errorValue = jsonResponse["error"]?.jsonPrimitive?.intOrNull
                return Error(exceptions.handleError(errorValue ?: 0))
            }

            val recentTracksResponse =
                JSON.decodeFromString<RecentTracksResponse>(response.bodyAsText())
            recentTracksResponse.recenttracks.track.forEach { track ->
                track.dateInfo?.let { dateInfo ->
                    val localDateTime = LocalDateTime.parse(
                        dateInfo.formattedDate,
                        DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH)
                    )
                    val zonedDateTime = localDateTime.atZone(ZoneId.of("UTC"))
                        .withZoneSameInstant(ZoneId.systemDefault())
                    val isoFormattedDate =
                        zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    dateInfo.formattedDate = isoFormattedDate
                }
            }
            profile.recentTracks = recentTracksResponse.recenttracks
            Success(Unit)
        } catch (e: UnresolvedAddressException) {
            Log.e("ApiRequest", "getProfileRecentTracksError: $e")
            e.printStackTrace()
            Error(exceptions.handleError(666))
        } catch (e: Exception) {
            Log.e("ApiRequest", "getProfileRecentTracksError: $e")
            e.printStackTrace()
            Error(exceptions.handleError(0))
        }
    }
}