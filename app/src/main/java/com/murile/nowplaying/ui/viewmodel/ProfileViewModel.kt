package com.murile.nowplaying.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.model.Resource
import com.murile.nowplaying.data.model.Track
import com.murile.nowplaying.data.repository.UserRepository
import com.murile.nowplaying.util.Stuff
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing get() = _isRefreshing.asStateFlow()

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile

    private val _recentTracks = MutableStateFlow<List<Track>>(emptyList())
    val recentTracks: StateFlow<List<Track>> = _recentTracks

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private var lastUpdateTimestamp: Long = 0L

    fun onRefresh() {
        if (_isRefreshing.value) return
        refreshProfile()
    }

    private fun refreshProfile() {
        _isRefreshing.value = true
        _errorMessage.value = ""
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile()
            userRepository.getUserInfo(userProfile!!)
            when (val result = userRepository.getRecentTracks(userProfile)) {
                is Resource.Success -> {
                    _userProfile.value = userProfile
                    _recentTracks.value = userProfile.recentTracks?.track ?: emptyList()
                    _isRefreshing.value = false
                }
                is Resource.Error -> {
                    _userProfile.value = userProfile
                    _errorMessage.value = result.message
                    _isRefreshing.value = false
                }
            }
            lastUpdateTimestamp = System.currentTimeMillis()
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            userRepository.clearUserSession()
        }
        resetLastUpdateTimestamp()
    }

    fun shouldRefresh(): Boolean {
        return System.currentTimeMillis() - lastUpdateTimestamp > Stuff.REFRESHING_TIME
    }

    private fun resetLastUpdateTimestamp() {
        lastUpdateTimestamp = 0L
    }
}
