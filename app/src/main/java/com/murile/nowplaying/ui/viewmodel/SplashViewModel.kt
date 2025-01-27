package com.murile.nowplaying.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.session.UserSessionManager
import com.murile.nowplaying.ui.components.APP_ROUTE
import com.murile.nowplaying.ui.components.FRIENDS_SCREEN
import com.murile.nowplaying.ui.components.LOGIN_ROUTE
import com.murile.nowplaying.ui.components.LOGIN_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userSessionManager: UserSessionManager,
    private val apiRequest: ApiRequest
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading get() = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination get() = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000)
            val isLoggedIn = userSessionManager.isUserLoggedIn()

            if (isLoggedIn) {
                checkUser()
                _startDestination.value = APP_ROUTE
                _isLoading.value = false
            } else {
                _isLoading.value = false
                _startDestination.value = LOGIN_ROUTE
            }
        }
    }

    private suspend fun checkUser() {
        val userProfile = userSessionManager.getUserProfile()
        val links: Array<String> = apiRequest.getUserInfo(userProfile!!.username)
        val newProfilePic = links[0]
        if (newProfilePic != userProfile.imageUrl) {
            userProfile.imageUrl = newProfilePic
            userSessionManager.saveUserProfile(userProfile)
        } else {
            return
        }
    }
}