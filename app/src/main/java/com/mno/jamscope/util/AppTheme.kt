package com.mno.jamscope.util

import androidx.compose.runtime.compositionLocalOf

enum class AppTheme {
    SYSTEM, LIGHT, DARK
}
val LocalThemePreference = compositionLocalOf { 0 }
