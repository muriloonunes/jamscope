package com.murile.nowplaying.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.api.Resource
import com.murile.nowplaying.data.model.Profile
import com.murile.nowplaying.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing get() = _isRefreshing.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile

    private var lastUpdateTimestamp: Long = 0L

    fun onRefresh() {
        _isRefreshing.value = true
        _errorMessage.value = ""
        viewModelScope.launch {
            val userProfile = userRepository.getUserProfile()
            when (val result = userRepository.getUserFriends(userProfile!!.username)) {
                is Resource.Success -> {
                    userProfile.friends = result.data
                    userRepository.saveUserProfile(userProfile)
                    _userProfile.value = userProfile
                }

                is Resource.Error -> {
                    _errorMessage.value = result.message
                }
            }
            lastUpdateTimestamp = System.currentTimeMillis()
            _isRefreshing.value = false
        }
    }

    fun shouldRefresh(): Boolean {
        return System.currentTimeMillis() - lastUpdateTimestamp > 15000000 // 2 min e meio em milisegundos
    }

    fun resetLastUpdateTimestamp() {
        lastUpdateTimestamp = 0L
    }
}