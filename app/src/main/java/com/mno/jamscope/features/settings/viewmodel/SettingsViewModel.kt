package com.mno.jamscope.features.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.domain.repository.SettingsRepository
import com.mno.jamscope.domain.usecase.user.LogoutUseCase
import com.mno.jamscope.features.settings.domain.model.SwitchState
import com.mno.jamscope.features.settings.domain.model.getPersonalizationSwitches
import com.mno.jamscope.features.settings.state.SettingsUiState
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.navigator.ScreenType
import com.mno.jamscope.domain.openUrl
import com.mno.jamscope.domain.sendMail
import com.mno.jamscope.domain.sendReportMail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val navigator: Navigator,
    private val settingsRepository: SettingsRepository,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            loadSwitchStates()
            settingsRepository.getThemePreferenceFlow().collect { pref ->
                _uiState.update { it.copy(themePreference = pref) }
            }
        }
    }

    private fun loadSwitchStates() {
        val personalizationSwitches = getPersonalizationSwitches()
        viewModelScope.launch {
            val states = personalizationSwitches.associate { switch ->
                switch.key to settingsRepository.getSwitchState(switch.key, switch.initialState)
                    .first()
            }
            _uiState.update { it.copy(switchStates = states) }
        }
    }

    fun toggleSwitch(key: String) {
        val current = _uiState.value.switchStates[key] ?: return
        val newState = if (current is SwitchState.On) SwitchState.Off else SwitchState.On
        val updated = _uiState.value.switchStates.toMutableMap().apply { this[key] = newState }
        _uiState.update { it.copy(switchStates = updated) }
        viewModelScope.launch {
            settingsRepository.saveSwitchState(key, newState)
        }
    }

    fun showThemeDialog() {
        _uiState.update { it.copy(showThemeDialog = true) }
    }

    fun hideThemeDialog() {
        _uiState.update { it.copy(showThemeDialog = false) }
    }

    fun showLogOutDialog() {
        _uiState.update { it.copy(showLogOutDialog = true) }
    }

    fun hideLogOutDialog() {
        _uiState.update { it.copy(showLogOutDialog = false) }
    }

    fun showChangelogDialog() {
        _uiState.update { it.copy(showChangelogDialog = true) }
    }

    fun hideChangelogDialog() {
        _uiState.update { it.copy(showChangelogDialog = false) }
    }

    fun logOutUser() {
        viewModelScope.launch {
            logoutUseCase()
//            logoutBus.send()
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
            navigator.navigate(Destination.SuggestFeatureScreen)
        }
    }

    fun navigateToLibrariesLicenseScreen(screenType: ScreenType) {
        viewModelScope.launch {
            navigator.navigate(Destination.LibrariesLicenseScreen(screenType))
        }
    }

    fun navigateToAboutScreen() {
        viewModelScope.launch {
            navigator.navigate(Destination.AboutScreen)
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            navigator.back()
        }
    }

    fun onTileSelect(newTile: Int) {
        _uiState.update { it.copy(selectedTile = newTile) }
    }

    fun setThemePreference(theme: Int) {
        viewModelScope.launch {
            settingsRepository.saveThemePreference(theme)
            _uiState.update { it.copy(themePreference = theme) }
        }
    }

    fun sendBugReportMail(context: Context) {
        viewModelScope.launch {
            context.sendReportMail()
        }
    }

    fun sendMailToDeveloper(context: Context) {
        viewModelScope.launch {
            context.sendMail()
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

    fun openGithubProfile(context: Context) {
        context.openUrl("https://github.com/muriloonunes")
    }

//    fun openPlayStore(context: Context) {
//        context.openPlayStore()
//    }
}