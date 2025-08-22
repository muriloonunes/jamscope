package com.mno.jamscope.features.login.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.features.login.state.LoginState
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.util.Stuff.openUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val navigator: Navigator,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _state.update { it.copy(username = newUsername) }
    }

    fun onPasswordChange(newPassword: String) {
        _state.update { it.copy(password = newPassword) }
    }

    fun openCreateAccount(context: Context) {
        context.openUrl("https://www.last.fm/join")
    }

    fun onPasswordVisibilityChange() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun login() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val username = _state.value.username.trim()
            val password = _state.value.password.trim()
            when (val result =
                userRepository.authenticate(
                    username = username,
                    password = password,
                    method = "getMobileSession"
                )) {
                is Resource.Success -> {
                    userRepository.saveUserProfile(result.data)
                    _state.update { it.copy(isLoading = false) }
                    navigate()
                }

                is Resource.Error -> {
                    _state.update { it.copy(errorMessage = result.message, isLoading = false) }
                }
            }
        }
    }

    private fun navigate() {
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.AppRoute,
                navOptions = {
                    popUpTo(Destination.LoginRoute) {
                        inclusive = true
                    }
                }
            )
        }
    }
}