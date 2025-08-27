package com.mno.jamscope.features.widgets.theme

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.glance.GlanceTheme
import androidx.glance.material3.ColorProviders
import com.mno.jamscope.ui.theme.darkScheme
import com.mno.jamscope.ui.theme.lightScheme

object JamscopeWidgetTheme {
    val colors = ColorProviders(
        light = lightScheme,
        dark = darkScheme
    )
}

@Composable
fun JamscopeWidgetTheme(
    content: @Composable () -> Unit,
) {
    GlanceTheme(
        colors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            GlanceTheme.colors
        else JamscopeWidgetTheme.colors,
    ) {
        content()
    }
}