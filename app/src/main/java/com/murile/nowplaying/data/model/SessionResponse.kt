package com.murile.nowplaying.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    var profileUrl: String? = null,
    var friends: List<User>? = null
)

@Serializable
data class Session(
    val key: String,
)

@Serializable
data class ApiResponse(
    val user: User
)

@Serializable
data class UserFriendsResponse(
    val friends: Friends
)

@Serializable
data class Friends(
    val user: List<User>
)


@Serializable
data class User(
    val name: String? = null,
    val image: List<Image>,
    val url: String
)

@Serializable
data class Image(
    val size: String,
    @SerialName("#text") val url: String
)