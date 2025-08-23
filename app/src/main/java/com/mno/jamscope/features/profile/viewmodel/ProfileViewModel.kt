package com.mno.jamscope.features.profile.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.repository.SettingsRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.features.profile.state.ProfileState
import com.mno.jamscope.util.LogoutEventBus
import com.mno.jamscope.util.Stuff
import com.mno.jamscope.util.Stuff.openUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @param:ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    private var lastUpdateTimestamp: Long = 0L

    init {
        viewModelScope.launch {
            LogoutEventBus.logoutEvents.collect {
                resetLastUpdateTimestamp()
            }
        }
        loadCachedProfile()
        if (shouldRefresh()) refreshProfile()
        viewModelScope.launch {
            settingsRepository.getSwitchState("playing_animation_toggle", true)
                .collect { state ->
                    _uiState.update { it.copy(playingAnimationEnabled = state) }
                }
        }
    }

    fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        refreshProfile()
    }

    private fun loadCachedProfile() {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getCachedUserProfile()
                val recentTracks = userProfile.recentTracks?.track ?: emptyList()
                _uiState.update {
                    it.copy(
                        userProfile = userProfile,
                        recentTracks = recentTracks,
                        imagePfp = userProfile.imageUrl ?: R.drawable.baseline_account_circle_24
                    )
                }
            } catch (_: IllegalStateException) {
                _uiState.update { it.copy(errorMessage = context.getString(R.string.error_loading_profile)) }
            }
        }
    }

    private fun refreshProfile() {
        _uiState.update { it.copy(isRefreshing = true, errorMessage = "") }
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile()
            userRepository.getUserInfo(userProfile!!)
            when (val result = userRepository.getRecentTracks(userProfile)) {
                is Resource.Success -> {
                    val recentTracks = userProfile.recentTracks?.track ?: emptyList()
                    _uiState.update {
                        it.copy(
                            userProfile = userProfile,
                            recentTracks = recentTracks,
                            imagePfp = userProfile.imageUrl ?: R.drawable.baseline_account_circle_24,
                            isRefreshing = false
                        )
                    }
                    userRepository.cacheRecentTracks(
                        userProfile.profileUrl!!,
                        recentTracks
                    )
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            userProfile = userProfile,
                            errorMessage = result.message,
                            isRefreshing = false
                        )
                    }
                }
            }
            userRepository.saveUserProfile(userProfile)
            lastUpdateTimestamp = System.currentTimeMillis()
        }
    }

    fun seeMore(
        context: Context,
        userProfile: Profile?
    ) {
        context.openUrl("https://www.last.fm/user/${userProfile!!.username}/library?page=3")
    }

    fun shouldRefresh(): Boolean {
        return System.currentTimeMillis() - lastUpdateTimestamp > Stuff.REFRESHING_TIME
    }

    private fun resetLastUpdateTimestamp() {
        lastUpdateTimestamp = 0L
    }
}