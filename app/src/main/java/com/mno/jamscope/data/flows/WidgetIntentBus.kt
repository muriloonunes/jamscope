package com.mno.jamscope.data.flows

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WidgetIntentBus @Inject constructor() {
    private val _widgetClick = MutableSharedFlow<String?>(
        replay = 1
    )

    val widgetClick = _widgetClick.asSharedFlow()

    suspend fun emit(name: String?) {
        _widgetClick.emit(name)
    }

    suspend fun consume() {
        _widgetClick.emit(null)
    }
}