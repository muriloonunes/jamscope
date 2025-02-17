package com.mno.jamscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.components.APP_ROUTE
import com.mno.jamscope.ui.components.LOGIN_ROUTE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading get() = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination get() = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            val isLoggedIn = userRepository.isUserLoggedIn()
            if (isLoggedIn) {
                _startDestination.value = APP_ROUTE
                _isLoading.value = false
            } else {
                _isLoading.value = false
                _startDestination.value = LOGIN_ROUTE
            }
        }
    }
}