package com.mno.jamscope.data.remote.api

import android.util.Log
import com.mno.jamscope.data.remote.mapper.toUser
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.Resource.Error
import com.mno.jamscope.domain.Resource.Success
import com.mno.jamscope.data.remote.dto.ProfileDto
import com.mno.jamscope.data.remote.dto.SessionDto
import com.mno.jamscope.data.remote.dto.SessionResponseDto
import com.mno.jamscope.domain.model.User
import com.mno.jamscope.util.Stuff
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.channels.UnresolvedAddressException
import java.security.MessageDigest
import javax.crypto.BadPaddingException
import javax.inject.Inject

@Deprecated(
    "This class is deprecated, use LastFmServiceApi instead",
    ReplaceWith("LastFmServiceApi"),
)
class AuthRequest @Inject constructor(
    private val exceptions: Exceptions,
    private val client: HttpClient,
) {
    private fun generateMobileApiSig(
        username: String,
        password: String,
        method: String,
    ): String {
        val apiSignature =
            "api_key${Stuff.LAST_KEY}" + "methodauth.$method" + "password$password" + "username$username" + Stuff.LAST_SECRET

        val md5Digest = MessageDigest.getInstance("MD5")
        val hashBytes = md5Digest.digest(apiSignature.toByteArray(Charsets.UTF_8))

        val hexString = StringBuilder()
        for (byte in hashBytes) {
            val hex = Integer.toHexString(0xFF and byte.toInt())
            hexString.append(hex.padStart(2, '0'))
        }
        return hexString.toString()
    }

    private fun generateWebApiSig(
        token: String,
        method: String,
    ): String {
        val apiSignature =
            "api_key${Stuff.LAST_KEY}" + "methodauth.$method" + "token$token" + Stuff.LAST_SECRET

        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(apiSignature.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private fun buildMobileAuthUrl(username: String, password: String, method: String): String {
        val apiSig = generateMobileApiSig(username, password, method)
        val urlParams =
            "method=auth.$method&api_key=${Stuff.LAST_KEY}&password=$password&username=$username&api_sig=$apiSig"
        return "${Stuff.BASE_URL}${Stuff.FORMAT_JSON}&$urlParams"
    }

    private fun buildWebAuthUrl(token: String, method: String): String {
        val apiSig = generateWebApiSig(token, method)
        val urlParams = "method=auth.$method&api_key=${Stuff.LAST_KEY}&token=$token&api_sig=$apiSig"
        return "${Stuff.BASE_URL}${Stuff.FORMAT_JSON}&$urlParams"
    }

    suspend fun authenticateMobile(
        username: String,
        password: String,
        method: String,
    ): Resource<User> {
        return try {
            val requestUrl = buildMobileAuthUrl(username, password, method)
            val response = withContext(Dispatchers.IO) {
                client.post(requestUrl) {
                    headers {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                }
            }
            if (!response.status.isSuccess()) {
                Log.e("ApiRequest", "autenticar: ${response.bodyAsText()}")
                Error(exceptions.handleError(response.status.value))
            } else {
                val user =
                    withContext(Dispatchers.IO) { createUser(response.bodyAsText(), password) }
                user?.let { Success(it) } ?: Error("Failed to process session response")
            }
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
            Error(exceptions.handleError(666))
        } catch (e: Exception) {
            e.printStackTrace()
            Error(exceptions.handleError(0))
        }
    }

    suspend fun authenticateWeb(
        token: String,
        method: String,
    ) {
        //TODO adicionar retorno
        try {
            val requestUrl = buildWebAuthUrl(token, method)
            val response = withContext(Dispatchers.IO) {
                client.post(requestUrl) {
                    headers {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                }
            }
            if (!response.status.isSuccess()) {
                Log.e("ApiRequest", "autenticar: ${response.bodyAsText()}")
//                Error(exceptions.handleError(response.status.value))
            } else {
                Log.d("AuthRequest", "authenticateWeb: ${response.bodyAsText()}")
            }
        } catch (e: UnresolvedAddressException) {
            e.printStackTrace()
//            Error(exceptions.handleError(666))
        } catch (e: Exception) {
            e.printStackTrace()
//            Error(exceptions.handleError(0))
        }
    }

    suspend fun isStillAuthenticated(user: User, method: String): Boolean {
        return try {
            val username = user.username
            val password = user.password
            val requestUrl = buildMobileAuthUrl(username, password, method)
            val response = withContext(Dispatchers.IO) {
                client.post(requestUrl) {
                    headers {
                        append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    }
                }
            }
            if (!response.status.isSuccess()) {
                Log.e("AuthRequest", "isStillAuthenticated: ${response.bodyAsText()}")
                false
            } else {
                val sessionResponse =
                    Stuff.JSON.decodeFromString<SessionResponseDto>(response.bodyAsText())
                sessionResponse.session.key == user.sessionKey
            }
        } catch (e: BadPaddingException) {
            Log.e("Auth", "Erro de descriptografia: ${e.message}")
            false
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    private fun createUser(responseBodyString: String, password: String): User? {
        return try {
            val sessionResponse =
                Stuff.JSON.decodeFromString<SessionResponseDto>(responseBodyString)

            ProfileDto(
                username = sessionResponse.session.name,
                subscriber = sessionResponse.session.subscriber,
                session = SessionDto(key = sessionResponse.session.key),
                senha = password,
                extraLargeImageUrl = "",
                largeImageUrl = "",
                profileUrl = "",
            ).toUser()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}