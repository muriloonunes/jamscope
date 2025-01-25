package com.murile.nowplaying.data.model

import androidx.compose.runtime.saveable.Saver
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class SessionResponse(
    val session: SessionDetails
)

@Serializable
data class SessionDetails(
    val name: String,
    val key: String,
    val subscriber: Int
)

@Serializable
data class Profile(
    val username: String,
    val senha: String,
    val session: Session,
    val subscriber: Int,
    var imageUrl: String? = null,
    var profileUrl: String? = null
)

@Serializable
data class Session(
    val key: String,
)

// Custom Saver for Profile
val ProfileSaver = Saver<Profile, String>(
    save = { profile ->
        // Convert Profile to a JSON string
        Json.encodeToString(profile)
    },
    restore = { jsonString ->
        // Convert JSON string back to Profile
        Json.decodeFromString(jsonString)
    }
)


@Serializable
data class ApiResponse(
    val user: User
)

@Serializable
data class User(
    val image: List<Image>,
    val url: String
)

@Serializable
data class Image(
    val size: String,
    @SerialName("#text") val url: String
)