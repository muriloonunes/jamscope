package com.mno.jamscope.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.R
import com.mno.jamscope.data.model.Profile
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.model.Track
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.util.Stuff
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
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing
        .onStart {
            loadCachedProfile()
            if (shouldRefresh()) refreshProfile() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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

    private fun loadCachedProfile() {
        viewModelScope.launch {
            try {
                val userProfile = userRepository.getCachedUserProfile()
                _userProfile.value = userProfile
                _recentTracks.value = userProfile.recentTracks?.track ?: emptyList()
            } catch (e: IllegalStateException) {
                _errorMessage.value = context.getString(R.string.error_loading_profile)
            }
        }
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
                    userRepository.cacheRecentTracks(userProfile.profileUrl!!, userProfile.recentTracks?.track ?: emptyList())
                    _isRefreshing.value = false
                }
                is Resource.Error -> {
                    _userProfile.value = userProfile
                    _errorMessage.value = result.message
                    _isRefreshing.value = false
                }
            }
            userRepository.saveUserProfile(userProfile)
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
