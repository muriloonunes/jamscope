package com.murile.nowplaying.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.murile.nowplaying.data.api.ApiRequest
import com.murile.nowplaying.data.api.Resource
import com.murile.nowplaying.data.model.Profile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _userProfile = MutableStateFlow<Profile?>(null)
    val userProfile: StateFlow<Profile?> = _userProfile

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun login(context: Context) {
        _loading.value = true
        viewModelScope.launch {
            when (val result = ApiRequest.autenticar(_username.value, _password.value, "getMobileSession", context)) {
                is Resource.Success -> {
                    _userProfile.value = result.data
                    _loading.value = false
                }
                is Resource.Error -> {
                    _errorMessage.value = result.message
                    _loading.value = false
                }
            }
        }
    }
}