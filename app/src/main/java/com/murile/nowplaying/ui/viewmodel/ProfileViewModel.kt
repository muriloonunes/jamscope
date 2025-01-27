package com.murile.nowplaying.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.session.UserSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val userSessionManager: UserSessionManager,
    private val apiRequest: ApiRequest
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing get() = _isRefreshing.asStateFlow()

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile

    private var lastUpdateTimestamp: Long = 0L

    fun onRefresh() {
        _isRefreshing.value = true
        viewModelScope.launch {
            userSessionManager.getUserProfile()?.let { userProfile ->
                val links: Array<String> = apiRequest.getUserInfo(userProfile.username)
                val newProfilePic = links[0]
                if (newProfilePic != userProfile.imageUrl) {
                    val updatedProfile = userProfile.copy(imageUrl = newProfilePic)
                    userSessionManager.saveUserProfile(updatedProfile)
                    _userProfile.value = updatedProfile
                } else {
                    _userProfile.value = userProfile
                }
            }
            _isRefreshing.value = false
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            userSessionManager.clearUserSession()
        }
    }

    fun shouldRefresh(): Boolean {
        return System.currentTimeMillis() - lastUpdateTimestamp > 15000000 // 2 min e meio em milisegundos
    }
}
