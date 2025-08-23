package com.mno.jamscope.features.friends.state

import com.mno.jamscope.data.model.RecentTracks
import com.mno.jamscope.data.model.User
import com.mno.jamscope.util.SortingType

data class FriendsState(
    val isRefreshing: Boolean = false,
    val isBottomSheetShown: Boolean = false,
    val errorMessage: String = "",
    val sortingType: SortingType = SortingType.DEFAULT,
    val friends: List<User> = emptyList(),
    val recentTracksMap: Map<String, RecentTracks?> = emptyMap(),
    val playingAnimationEnabled: Boolean = true,
    val cardBackgroundColorEnabled: Boolean = true,
)
