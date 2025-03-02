package com.mno.jamscope.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.repository.FriendsRepository
import com.mno.jamscope.data.repository.SettingsRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.util.Stuff.openUrl
import com.mno.jamscope.util.sendReportMail
import com.mno.jamscope.util.switches
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val navigator: Navigator,
    private val friendsRepository: FriendsRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _switchStates = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val switchStates: StateFlow<Map<String, Boolean>> = _switchStates

    private val _themePreference = MutableStateFlow(0)
    val themePreference: StateFlow<Int> = _themePreference

    init {
        viewModelScope.launch {
            loadSwitchStates()
            _themePreference.value = settingsRepository.getThemePreferenceFlow()
        }
    }

    private fun loadSwitchStates() {
        viewModelScope.launch {
            val states = switches.associate { switch ->
                switch.key to settingsRepository.getSwitchState(switch.key, switch.initialState)
                    .first()
            }
            _switchStates.value = states
        }
    }

    fun toggleSwitch(key: String) {
        val currentState = _switchStates.value[key] ?: return
        val newState = !currentState
        _switchStates.value = _switchStates.value.toMutableMap().apply {
            this[key] = newState
        }
        viewModelScope.launch {
            settingsRepository.saveSwitchState(key, newState)
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            userRepository.clearUserSession()
            friendsRepository.deleteFriends()
            delay(500)
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.LoginRoute,
                navOptions = {
                    popUpTo(Destination.AppRoute) {
                        inclusive = true
                    }
                }
            )
        }
    }

    fun navigateToWebView() {
        viewModelScope.launch {
            navigator.navigate(Destination.WebViewScreen)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            navigator.back()
        }
    }

    fun setThemePreference(theme: Int) {
        viewModelScope.launch {
            settingsRepository.saveThemePref(theme)
            _themePreference.value = theme
        }
    }

    //thx pano scrobbler
    fun sendBugReportMail(context: Context) {
        viewModelScope.launch {
            sendReportMail(context)
        }
    }

    fun openDeleteAccount(context: Context) {
        context.openUrl("https://www.last.fm/settings/account/delete")
    }

    fun openBuyMeACoffee(context: Context) {
        context.openUrl("https://buymeacoffee.com/muriloonunes")
    }

    fun openGithubProject(context: Context) {
        context.openUrl("https://github.com/muriloonunes/jamscope")
    }
}