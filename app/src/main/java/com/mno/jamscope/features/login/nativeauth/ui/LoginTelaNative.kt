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
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
    val windowsWidth = LocalWindowSizeClass.current.widthSizeClass
    val windowsHeight = LocalWindowSizeClass.current.heightSizeClass

    val telaHorizontal = windowsWidth == WindowWidthSizeClass.Expanded &&
            windowsHeight != WindowHeightSizeClass.Medium

    val topSpacerHeight = if (
        (windowsWidth == WindowWidthSizeClass.Expanded && windowsHeight == WindowHeightSizeClass.Medium) ||
        windowsWidth == WindowWidthSizeClass.Medium
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