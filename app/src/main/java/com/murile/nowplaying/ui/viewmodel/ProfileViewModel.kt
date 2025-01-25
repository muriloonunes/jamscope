package com.murile.nowplaying.ui.viewmodel

import UserSessionManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile

    fun getProfile(context: Context) {
        val userSessionManager = UserSessionManager(context)
        viewModelScope.launch {
            val userProfile = userSessionManager.getUserProfile()
            _userProfile.value = userProfile
        }
    }
}
