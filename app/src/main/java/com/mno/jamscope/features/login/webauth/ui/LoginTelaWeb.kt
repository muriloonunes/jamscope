package com.mno.jamscope.features.login.webauth.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@Composable
fun LoginTelaWeb(
    onLoginClick: () -> Unit,
) {
    val windowsWidth = LocalWindowSizeClass.current.windowWidthSizeClass
    val windowsHeight = LocalWindowSizeClass.current.windowHeightSizeClass

    val topSpacerHeight = if (
        (windowsWidth == WindowWidthSizeClass.EXPANDED && windowsHeight == WindowHeightSizeClass.MEDIUM) ||
        windowsWidth == WindowWidthSizeClass.MEDIUM
    ) 64.dp else 32.dp

    val telaHorizontal = windowsWidth == WindowWidthSizeClass.EXPANDED &&
            windowsHeight != WindowHeightSizeClass.MEDIUM
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        LoginVerticalScreenWeb(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            topSpacerHeight = topSpacerHeight,
            onLoginClick = onLoginClick
        )
        if (!telaHorizontal) {
        }
    }
}