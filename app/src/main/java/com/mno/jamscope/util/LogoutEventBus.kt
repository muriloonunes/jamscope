package com.mno.jamscope.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object LogoutEventBus {
    private val _logoutEvents = MutableSharedFlow<Unit>()
    val logoutEvents = _logoutEvents.asSharedFlow()

    suspend fun sendLogoutEvent() {
        _logoutEvents.emit(Unit)
    }
}