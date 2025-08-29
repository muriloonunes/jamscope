package com.mno.jamscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.data.repository.ApiRepository
import com.mno.jamscope.data.repository.UserRepository
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.navigator.NavigationAction
import com.mno.jamscope.ui.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val apiRepository: ApiRepository,
    private val navigator: Navigator
//    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _isLoading = MutableStateFlow(true)
    val isLoading get() = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow<Destination?>(null)
    val startDestination get() = _startDestination.asStateFlow()

    private val _navActions = MutableSharedFlow<NavigationAction>()
    val navActions: SharedFlow<NavigationAction> = _navActions

//    private val _appOpenedTimes = MutableStateFlow(0)
//    val appOpenedTimes: StateFlow<Int> = _appOpenedTimes

    init {
        viewModelScope.launch {
            val isLoggedIn = userRepository.isUserLoggedIn()
            if (isLoggedIn) {
//                settingsRepository.incrementAppOpened()
//                _appOpenedTimes.value = settingsRepository.getAppOpenedFlow()
                val profile = userRepository.getUserProfile()
                if (apiRepository.isStillAuthenticated(profile!!)) {
                    _startDestination.value = Destination.AppRoute
                    _isLoading.value = false
                } else {
                    _startDestination.value = Destination.LoginRoute
                    _isLoading.value = false
                }
            } else {
                _startDestination.value = Destination.LoginRoute
                _isLoading.value = false
            }
        }
        viewModelScope.launch {
            navigator.navigationActions.collect { action ->
                _navActions.emit(action)
            }
        }
    }
}