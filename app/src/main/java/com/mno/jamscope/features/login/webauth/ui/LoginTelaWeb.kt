package com.mno.jamscope.features.login.webauth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.mno.jamscope.features.login.webauth.ui.components.LoginWebFab
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@Composable
fun LoginTelaWeb(
    onLoginClick: () -> Unit,
) {
    val windowsWidth = LocalWindowSizeClass.current.widthSizeClass
    val windowsHeight = LocalWindowSizeClass.current.heightSizeClass

    val telaHorizontal = windowsWidth == WindowWidthSizeClass.Expanded &&
            windowsHeight != WindowHeightSizeClass.Medium

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (!telaHorizontal) {
                Surface(
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LoginWebFab { onLoginClick() }
                    }
                }
            }
        }
    ) { innerPadding ->
        if (telaHorizontal) {
            LoginHorizontalScreenWeb(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                onLoginClick = { onLoginClick() }
            )
        } else {
            LoginVerticalScreenWeb(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }
    }
}