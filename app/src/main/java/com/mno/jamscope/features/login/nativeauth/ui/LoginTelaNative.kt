package com.mno.jamscope.features.login.nativeauth.ui

import android.content.Context
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.features.login.state.LoginStateNative
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@Composable
fun LoginTelaNative(
    state: LoginStateNative,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    openCreateAccount: (Context) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val context = LocalContext.current
    val windowsWidth = LocalWindowSizeClass.current.windowWidthSizeClass
    val windowsHeight = LocalWindowSizeClass.current.windowHeightSizeClass

    val telaHorizontal = windowsWidth == WindowWidthSizeClass.EXPANDED &&
            windowsHeight != WindowHeightSizeClass.MEDIUM

    val topSpacerHeight = if (
        (windowsWidth == WindowWidthSizeClass.EXPANDED && windowsHeight == WindowHeightSizeClass.MEDIUM) ||
        windowsWidth == WindowWidthSizeClass.MEDIUM
    ) 128.dp else 32.dp

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        if (telaHorizontal) {
            LoginHorizontalScreenNative(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(8.dp),
                username = state.username,
                password = state.password,
                loading = state.isLoading,
                errorMessage = state.errorMessage,
                passwordVisibility = state.isPasswordVisible,
                onLoginClick = { onLoginClick() },
                onPasswordChange = { onPasswordChange(it) },
                onUsernameChange = { onUsernameChange(it) },
                openCreateAccount = { openCreateAccount(it) },
                onPasswordVisibilityChange = { onPasswordVisibilityChange() },
                context = context
            )
        } else {
            LoginVerticalScreenNative(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(8.dp),
                topSpacerHeight = topSpacerHeight,
                username = state.username,
                password = state.password,
                loading = state.isLoading,
                errorMessage = state.errorMessage,
                passwordVisibility = state.isPasswordVisible,
                onLoginClick = { onLoginClick() },
                onPasswordChange = { onPasswordChange(it) },
                onUsernameChange = { onUsernameChange(it) },
                openCreateAccount = { openCreateAccount(it) },
                onPasswordVisibilityChange = { onPasswordVisibilityChange() },
                context = context
            )
        }
    }
}