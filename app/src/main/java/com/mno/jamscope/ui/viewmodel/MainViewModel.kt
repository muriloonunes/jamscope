package com.mno.jamscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.BuildConfig
import com.mno.jamscope.data.flows.WidgetIntentBus
import com.mno.jamscope.domain.repository.SettingsRepository
import com.mno.jamscope.domain.usecase.login.CheckAppVersionUseCase
import com.mno.jamscope.domain.usecase.login.CheckLoginUseCase
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.NavigationAction
import com.mno.jamscope.ui.navigator.Navigator
import com.mno.jamscope.ui.state.MainState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val navigator: Navigator,
    private val widgetIntentBus: WidgetIntentBus,
    private val checkLoginUseCase: CheckLoginUseCase,
    private val checkAppVersionUseCase: CheckAppVersionUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MainState())
    val state = _state.asStateFlow()
    private val _navActions = MutableSharedFlow<NavigationAction>()
    val navActions: SharedFlow<NavigationAction> = _navActions

    init {
        viewModelScope.launch {
            settingsRepository.getThemePreferenceFlow().collect { pref ->
                _state.update { it.copy(themePreference = pref) }
            }
        }
        viewModelScope.launch {
            val isLoggedIn = checkLoginUseCase()
            val startDestination = if (isLoggedIn) Destination.AppRoute else Destination.LoginRoute

            val versionCode = BuildConfig.VERSION_CODE
            val showChangelog = checkAppVersionUseCase(versionCode) && isLoggedIn

            _state.update {
                it.copy(
                    startDestination = startDestination,
                    isLoading = false,
                    showChangelog = showChangelog
                )
            }
            saveAppVersion(versionCode)
        }
        viewModelScope.launch {
            navigator.navigationActions.collect { action ->
                _navActions.emit(action)
            }
        }
    }

    fun handleWidgetIntent(name: String?) {
        viewModelScope.launch {
            widgetIntentBus.emit(name)
        }
    }

    fun onDismissChangelog() {
        _state.update {
            it.copy(
                showChangelog = false
            )
        }
        val versionCode = BuildConfig.VERSION_CODE
        saveAppVersion(versionCode)
    }

    private fun saveAppVersion(version: Int) {
        viewModelScope.launch {
            settingsRepository.saveAppVersion(version)
        }
    }
}