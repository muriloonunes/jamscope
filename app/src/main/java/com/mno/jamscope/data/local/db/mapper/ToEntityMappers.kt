package com.mno.jamscope.data.local.db.mapper

import com.mno.jamscope.data.local.db.entity.FriendEntity
import com.mno.jamscope.data.local.db.entity.TrackEntity
import com.mno.jamscope.data.local.db.entity.UserEntity
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.model.User

fun User.toEntity(): UserEntity {
    return UserEntity(
        url = this.profileUrl,
        username = this.username,
        password = this.password,
        sessionKey = this.sessionKey,
        country = this.country,
        realName = realName,
        subscriber = this.subscriber,
        playcount = this.playcount,
        imageLarge = this.largeImageUrl,
        imageExtraLarge = this.extraLargeImageUrl
    )
}

fun Friend.toEntity(): FriendEntity {
    return FriendEntity(
        profileUrl = this.profileUrl,
        name = this.name,
        country = this.country,
        realname = this.realName,
        subscriber = this.subscriber,
        playcount = this.playcount,
        imageLarge = this.largeImageUrl,
        imageExtraLarge = this.extraLargeImageUrl,
    )
}

fun Track.toUserEntity(userUrl: String): TrackEntity {
    return TrackEntity(
        userOwnerUrl = userUrl,
        name = this.name,
        artistName = this.artistName,
        albumName = this.albumName,
        largeImageUrl = this.largeImageUrl,
        extraLargeImageUrl = this.extraLargeImageUrl,
        date = this.date,
        url = this.url,
        friendOwnerUrl = null
    )
}
fun Track.toFriendEntity(friendUrl: String): TrackEntity {
    return TrackEntity(
        friendOwnerUrl = friendUrl,
        name = this.name,
        artistName = this.artistName,
        albumName = this.albumName,
        largeImageUrl = this.largeImageUrl,
        extraLargeImageUrl = this.extraLargeImageUrl,
        date = this.date,
        url = this.url,
        userOwnerUrl = null
    )
}

fun UserEntity.toDomain(recentTracks: List<TrackEntity>): User {
    return User(
        username = this.username,
        password = this.password,
        sessionKey = this.sessionKey,
        profileUrl = this.url,
        country = this.country,
        realName = this.realName,
        subscriber = this.subscriber,
        playcount = this.playcount,
        largeImageUrl = this.imageLarge,
        extraLargeImageUrl = this.imageExtraLarge,
        recentTracks = if (recentTracks.isNotEmpty()) recentTracks.map { it.toDomain() } else emptyList()
    )
}

fun FriendEntity.toDomain(recentTracks: List<TrackEntity>): Friend {
    return Friend(
        name = this.name,
        profileUrl = this.profileUrl,
        country = this.country,
        realName = this.realname,
        subscriber = this.subscriber,
        playcount = this.playcount,
        largeImageUrl = this.imageLarge,
        extraLargeImageUrl = this.imageExtraLarge,
        recentTracks = if (recentTracks.isNotEmpty()) recentTracks.map { it.toDomain() } else emptyList()
    )
}

fun TrackEntity.toDomain(): Track {
    return Track(
        name = this.name,
        artistName = this.artistName,
        albumName = this.albumName,
        largeImageUrl = this.largeImageUrl,
        extraLargeImageUrl = this.extraLargeImageUrl,
        date = this.date,
        url = this.url
    )
}

