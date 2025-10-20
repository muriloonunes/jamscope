package com.mno.jamscope.features.profile.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.R
import com.mno.jamscope.data.flows.LogoutEventBus
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.model.Track
import com.mno.jamscope.domain.model.User
import com.mno.jamscope.domain.repository.SettingsRepository
import com.mno.jamscope.domain.usecase.user.GetRecentTracksUseCase
import com.mno.jamscope.domain.usecase.user.GetUserFromLocalUseCase
import com.mno.jamscope.domain.usecase.user.GetUserInfoFromApiUseCase
import com.mno.jamscope.domain.usecase.user.SaveUserDataUseCase
import com.mno.jamscope.features.settings.domain.model.SwitchState
import com.mno.jamscope.util.Stuff
import com.mno.jamscope.util.Stuff.openUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val logoutBus: LogoutEventBus,
    private val getUserFromLocalUseCase: GetUserFromLocalUseCase,
    private val getUserInfoFromApiUseCase: GetUserInfoFromApiUseCase,
    private val getRecentTracksUseCase: GetRecentTracksUseCase,
    private val saveUserDataUseCase: SaveUserDataUseCase,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing
        .onStart {
            loadCachedProfile()
            if (shouldRefresh()) refreshProfile()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    private val _recentTracks = MutableStateFlow<List<Track>>(emptyList())
    val recentTracks: StateFlow<List<Track>> = _recentTracks

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _playingAnimationToggle = MutableStateFlow(true)
    val playingAnimationToggle: StateFlow<Boolean> = _playingAnimationToggle

    private var lastUpdateTimestamp: Long = 0L

    init {
        viewModelScope.launch {
            logoutBus.logoutEvents.collect {
                resetLastUpdateTimestamp()
            }
        }
        viewModelScope.launch {
            settingsRepository.getSwitchState("playing_animation_toggle", SwitchState.On)
                .collect { state ->
                    _playingAnimationToggle.value = state.value
                }
        }
    }

    fun onRefresh() {
        if (_isRefreshing.value) return
        refreshProfile()
    }

    private fun loadCachedProfile() {
        viewModelScope.launch {
            try {
                val userProfile = getUserFromLocalUseCase()
                _userProfile.value = userProfile
                _recentTracks.value = userProfile.recentTracks
            } catch (e: IllegalStateException) {
                Log.e("ProfileViewModel", e.toString())
                e.printStackTrace()
                _errorMessage.value = context.getString(R.string.error_loading_profile)
            }
        }
    }

    private fun refreshProfile() {
        _isRefreshing.value = true
        _errorMessage.value = ""
        viewModelScope.launch {
            var userProfile = getUserFromLocalUseCase()
            when (val result = getUserInfoFromApiUseCase(username = userProfile.username)) {
                is Resource.Success -> {
                    userProfile = result.data
                }
                is Resource.Error -> {
                    _errorMessage.value = result.message
                    _isRefreshing.value = false
                }
            }
            when (val result = getRecentTracksUseCase(userProfile.username)) {
                is Resource.Success -> {
                    userProfile.recentTracks = result.data
                    _userProfile.value = userProfile
                    _recentTracks.value = result.data
                    _isRefreshing.value = false

                    saveUserDataUseCase(userProfile)
                }

                is Resource.Error -> {
                    _userProfile.value = userProfile
                    _errorMessage.value = result.message
                    _isRefreshing.value = false
                }
            }
            saveUserDataUseCase(userProfile)
            lastUpdateTimestamp = System.currentTimeMillis()
        }
    }

    fun seeMore(
        context: Context,
        userProfile: User?,
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