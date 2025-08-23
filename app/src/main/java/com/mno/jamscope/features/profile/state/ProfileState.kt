package com.mno.jamscope.features.profile.state

import com.mno.jamscope.R
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Track

data class ProfileState(
    val isRefreshing: Boolean = false,
    val userProfile: Profile? = null,
    val recentTracks: List<Track> = emptyList(),
    val errorMessage: String = "",
    val playingAnimationEnabled: Boolean = true,
    val imagePfp: Any? = R.drawable.baseline_account_circle_24,
)