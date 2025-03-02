package com.mno.jamscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.repository.ApiRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.navigator.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val apiRepository: ApiRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading get() = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<Destination?>(null)
    val startDestination get() = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isLoggedIn = userRepository.isUserLoggedIn()
            if (isLoggedIn) {
                val profile = userRepository.getUserProfile()
                if (apiRepository.isStillAuthenticated(profile!!)) {
                    _startDestination.value = Destination.AppRoute
                    _isLoading.value = false
                }
            } else {
                _startDestination.value = Destination.LoginRoute
                _isLoading.value = false
            }
        }
    }
}