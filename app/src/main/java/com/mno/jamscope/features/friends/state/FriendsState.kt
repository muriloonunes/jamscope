package com.mno.jamscope.features.friends.state

import com.mno.jamscope.domain.model.Friend
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.features.friends.ui.SortingType

data class FriendsState(
    val isRefreshing: Boolean = false,
    val isBottomSheetShown: Boolean = false,
    val errorMessage: String = "",
    val sortingType: SortingType = SortingType.DEFAULT,
    val friends: List<Friend> = emptyList(),
    val recentTracksMap: Map<String, Track?> = emptyMap(),
    val playingAnimationEnabled: Boolean = true,
    val cardBackgroundColorEnabled: Boolean = true,
)
