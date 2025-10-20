package com.mno.jamscope.domain.repository

import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.features.friends.ui.SortingType

interface FriendRepository {
    suspend fun getRecentTracks(username: String): Resource<List<Track>>
    suspend fun getFriends(): List<Friend>
    suspend fun saveFriends(friends: List<Friend>)
    suspend fun saveSortingType(sortingType: SortingType)
    suspend fun getSortingType(): SortingType
    suspend fun clearFriends()
}