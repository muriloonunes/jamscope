package com.mno.jamscope.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mno.jamscope.ui.navigator.NavigationAction
import com.mno.jamscope.ui.navigator.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NavigationViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {
    private val _navActions = MutableSharedFlow<NavigationAction>()
    val navActions: SharedFlow<NavigationAction> = _navActions

    init {
        viewModelScope.launch {
            navigator.navigationActions.collect { action ->
                _navActions.emit(action)
            }
        }
    }
}
