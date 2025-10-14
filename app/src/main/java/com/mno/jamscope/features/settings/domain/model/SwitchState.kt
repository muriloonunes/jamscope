package com.mno.jamscope.features.settings.domain.model

sealed class SwitchState(val value: Boolean) {
    object On : SwitchState(true)
    object Off : SwitchState(false)

    companion object {
        fun fromValue(value: Boolean): SwitchState {
            return if (value) On else Off
        }
    }
}