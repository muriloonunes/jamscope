package com.mno.jamscope.features.webview.viewmodel

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    private val navigator: Navigator,
    private val userRepository: UserRepository,
    ) : ViewModel() {
    fun handleCallbackUrl(url: String) {
        val uri = url.toUri()
        val token = uri.getQueryParameter("token")
        Log.d("WebViewViewModel", "handleCallbackUrl: $token")
        viewModelScope.launch {
            userRepository.authenticateWeb(token = token?: "", method = "getSession")
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            navigator.back()
        }
    }
}