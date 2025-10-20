package com.mno.jamscope.features.login.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.usecase.login.LoginUserUseCase
import com.mno.jamscope.domain.usecase.user.SaveUserDataUseCase
import com.mno.jamscope.features.login.state.LoginStateNative
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.domain.openUrl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NativeLoginViewModel @Inject constructor(
    private val navigator: Navigator,
    private val loginUserUseCase: LoginUserUseCase,
    private val saveUserDataUseCase: SaveUserDataUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginStateNative())
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
        _state.update { it.copy(isLoading = true, errorMessage = "") }
        viewModelScope.launch {
            val username = _state.value.username.trim()
            val password = _state.value.password.trim()
            when (val result =
                loginUserUseCase(
                    username = username,
                    password = password,
                )) {
                is Resource.Success -> {
                    saveUserDataUseCase(result.data)
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