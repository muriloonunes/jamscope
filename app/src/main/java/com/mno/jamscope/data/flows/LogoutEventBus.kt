package com.mno.jamscope.data.flows

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutEventBus @Inject constructor() {
    private val _logoutEvents = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val logoutEvents: SharedFlow<Unit> = _logoutEvents.asSharedFlow()

    suspend fun send() {
        _logoutEvents.emit(Unit)
    }

    fun trySend() {
        _logoutEvents.tryEmit(Unit)
    }
}