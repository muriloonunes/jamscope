package com.mno.jamscope.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.model.Resource
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.util.Stuff.openUrl
import com.mno.jamscope.util.sendReportMail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun openCreateAccount(context: Context) {
        context.openUrl("https://www.last.fm/join")
    }

    fun login() {
        _loading.value = true
        viewModelScope.launch {
            when (val result =
                userRepository.authenticate(_username.value, _password.value, "getMobileSession")) {
                is Resource.Success -> {
                    userRepository.saveUserProfile(result.data)
                    _loading.value = false
                    navigate()
                }

                is Resource.Error -> {
                    _errorMessage.value = result.message
                    _loading.value = false
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

    fun openBugReport(context: Context) {
        viewModelScope.launch {
            sendReportMail(context)
        }
    }
}