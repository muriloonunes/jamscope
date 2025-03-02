package com.mno.jamscope.data.api

import android.util.Log
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.model.Resource.Error
import com.mno.jamscope.util.Stuff
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import com.mno.jamscope.data.model.Resource.Success
import com.mno.jamscope.data.model.Session
import com.mno.jamscope.data.model.SessionResponse
import com.mno.jamscope.util.Crypto
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.channels.UnresolvedAddressException
import java.security.MessageDigest
import javax.inject.Inject

class AuthRequest @Inject constructor(
    private val exceptions: Exceptions
) {
    private fun generateApiSig(username: String, password: String): String {
        val apiSignature =
            "api_key${Stuff.LAST_KEY}" + "methodauth.getMobileSession" + "password$password" + "username$username" + Stuff.LAST_SECRET

        val md5Digest = MessageDigest.getInstance("MD5")
        val hashBytes = md5Digest.digest(apiSignature.toByteArray(Charsets.UTF_8))

        val hexString = StringBuilder()
        for (byte in hashBytes) {
            val hex = Integer.toHexString(0xFF and byte.toInt())
            hexString.append(hex.padStart(2, '0'))
        }
        return hexString.toString()
    }

    private fun buildAuthUrl(username: String, password: String, method: String): String {
        val apiSig = generateApiSig(username, password)
        println("apisig: $apiSig")
        val urlParams =
            "method=auth.$method&api_key=${Stuff.LAST_KEY}&password=$password&username=$username&api_sig=$apiSig"
        return "${Stuff.BASE_URL}${Stuff.FORMAT_JSON}&$urlParams"
    }

    suspend fun authenticate(username: String, password: String, method: String): Resource<Profile> {
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
                Log.e("ApiRequest", "autenticar: ${response.bodyAsText()}")
                Error(exceptions.handleError(response.status.value))
            } else {
                val profile = withContext(Dispatchers.IO) { createProfile(response.bodyAsText(), password) }
                profile?.let { Success(it) } ?: Error("Failed to process session response")
            }
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
            Error(exceptions.handleError(666))
        } catch (e: Exception) {
            e.printStackTrace()
            Error(exceptions.handleError(0))
        }
    }

    suspend fun isStillAuthenticated(profile: Profile, method: String): Boolean {
        return try {
            val username = profile.username
            val password = Crypto.decrypt(profile.senha)
            val requestUrl = buildAuthUrl(username, password, method)
            val response = withContext(Dispatchers.IO) {
                HttpClientProvider.client.post(requestUrl) {
                    headers {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                }
            }
            if (!response.status.isSuccess()) {
                Log.e("ApiRequest", "isStillAuthenticated: ${response.bodyAsText()}")
                false
            } else {
                val sessionResponse = Stuff.JSON.decodeFromString<SessionResponse>(response.bodyAsText())
                sessionResponse.session.key == profile.session.key
            }
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    private fun createProfile(responseBodyString: String, password: String): Profile? {
        return try {
            val sessionResponse = Stuff.JSON.decodeFromString<SessionResponse>(responseBodyString)
            Profile(
                username = sessionResponse.session.name,
                subscriber = sessionResponse.session.subscriber,
                session = Session(key = sessionResponse.session.key),
                senha = Crypto.encrypt(password),
                imageUrl = "",
                profileUrl = "",
            )
        } catch (e: Exception) {
            null
        }
    }
}