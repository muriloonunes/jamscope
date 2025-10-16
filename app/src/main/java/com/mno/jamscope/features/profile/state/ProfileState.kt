package com.mno.jamscope.features.profile.state

import com.mno.jamscope.R
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.model.User

data class ProfileState(
    val isRefreshing: Boolean = false,
    val userProfile: User? = null,
    val recentTracks: List<Track> = emptyList(),
    val errorMessage: String = "",
    val playingAnimationEnabled: Boolean = true,
    val imagePfp: Any? = R.drawable.baseline_account_circle_24,
)