package com.murile.nowplaying.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.repository.UserRepository
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

    private var lastUpdateTimestamp: Long = 0L

    fun onRefresh() {
        _isRefreshing.value = true
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile()
            userRepository.getUserInfo(userProfile!!)
            _userProfile.value = userProfile

            _isRefreshing.value = false
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
        return System.currentTimeMillis() - lastUpdateTimestamp > 15000000 // 2 min e meio em milisegundos
    }

    private fun resetLastUpdateTimestamp() {
        lastUpdateTimestamp = 0L
    }
}
