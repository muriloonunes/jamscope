package com.mno.jamscope.features.login.webauth.viewmodel

import android.content.Context
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.domain.Resource
import com.mno.jamscope.domain.usecase.login.LoginWebUseCase
import com.mno.jamscope.domain.usecase.user.SaveUserDataUseCase
import com.mno.jamscope.features.login.webauth.state.LoginStateWeb
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebLoginViewModel @Inject constructor(
    private val navigator: Navigator,
    private val loginWebUseCase: LoginWebUseCase,
    private val saveUserDataUseCase: SaveUserDataUseCase,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {
    private val _state = MutableStateFlow(LoginStateWeb())
    val state = _state.asStateFlow()

    init {
        clearWebViewData(context)
    }

    fun handleCallbackUrl(url: String) {
        val uri = url.toUri()
        val token = uri.getQueryParameter("token")
        viewModelScope.launch {
            when (val result = loginWebUseCase(token = token ?: "")) {
                is Resource.Success -> {
                    saveUserDataUseCase(result.data)
                    _state.update { it.copy(isLoading = false) }
                    navigateToApp()
                    clearWebViewData(context)
                }

                is Resource.Error -> {
                    Log.e("WebViewViewModel", "Error during web login: ${result.message}")
                }
            }
        }
    }

    fun navigateToWebView() {
        viewModelScope.launch {
            navigator.navigate(Destination.LastFmWebLoginScreen)
        }
    }

    private fun navigateToApp() {
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

    fun navigateBack() {
        viewModelScope.launch {
            navigator.back()
        }
    }

    private fun clearWebViewData(context: Context) {
        try {
            // 🔹 Limpa cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.removeAllCookies(null)
            cookieManager.flush()

            // 🔹 Limpa cache, histórico e storage do WebView
            WebStorage.getInstance().deleteAllData()

            WebView(context).apply {
                clearCache(true)
                clearHistory()
                clearFormData()
            }
        } catch (e: Exception) {
            Log.e("WebViewCleanup", "Failed to clear WebView data", e)
        }
    }

}