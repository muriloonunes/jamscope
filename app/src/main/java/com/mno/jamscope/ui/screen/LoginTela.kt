package com.mno.jamscope.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.window.core.layout.WindowHeightSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.ui.components.LoginActions
import com.mno.jamscope.ui.components.LoginForm
import com.mno.jamscope.ui.components.LoginHeader
import com.mno.jamscope.ui.components.ShowErrorMessage
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val username by loginViewModel.username.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()
    val loading by loginViewModel.loading.collectAsState()
    val context = LocalContext.current

    val windowsWidth = LocalWindowSizeClass.current.windowWidthSizeClass
    val windowsHeight = LocalWindowSizeClass.current.windowHeightSizeClass

    val telaHorizontal = windowsWidth == WindowWidthSizeClass.EXPANDED &&
            windowsHeight != WindowHeightSizeClass.MEDIUM

    val topSpacerHeight = if (
        (windowsWidth == WindowWidthSizeClass.EXPANDED && windowsHeight == WindowHeightSizeClass.MEDIUM) ||
        windowsWidth == WindowWidthSizeClass.MEDIUM
    ) 64.dp else 0.dp

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        if (telaHorizontal) {
            LoginHorizontalScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(8.dp),
                loginViewModel = loginViewModel,
                username = username,
                password = password,
                loading = loading,
                errorMessage = errorMessage,
                context = context
            )
        } else {
            LoginVerticalScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(8.dp),
                topSpacerHeight = topSpacerHeight,
                loginViewModel = loginViewModel,
                username = username,
                password = password,
                loading = loading,
                errorMessage = errorMessage,
                context = context
            )
        }
    }
}

@Composable
fun LoginVerticalScreen(
    modifier: Modifier,
    topSpacerHeight: Dp = 0.dp,
    loginViewModel: LoginViewModel,
    username: String,
    password: String,
    loading: Boolean,
    errorMessage: String,
    context: Context
) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        if (topSpacerHeight > 0.dp) {
            Spacer(modifier = Modifier.height(topSpacerHeight))
        }
        LoginHeader(
            modifier = Modifier
                .fillMaxWidth(),
            alignment = Alignment.CenterHorizontally
        )
        Spacer(modifier = Modifier.height(8.dp))
        LoginForm(
            username = username,
            onUsernameChange = { loginViewModel.onUsernameChange(it) },
            password = password,
            onPasswordChange = { loginViewModel.onPasswordChange(it) },
            isPasswordField = passwordVisibility,
            onPasswordVisibilityChange = { passwordVisibility = !passwordVisibility }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage.isNotEmpty()) {
            ShowErrorMessage(errorMessage)
        }
        if (loading) {
            CircularProgressIndicator()
        } else {
            LoginActions(
                onCreateAccountClick = { loginViewModel.openCreateAccount(context) },
                username = username,
                password = password,
                onLoginButtonClick = { loginViewModel.login() }
            )
        }
    }
}

@Composable
fun LoginHorizontalScreen(
    modifier: Modifier,
    loginViewModel: LoginViewModel,
    username: String,
    password: String,
    loading: Boolean,
    errorMessage: String,
    context: Context
) {
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top,
    ) {
        LoginHeader(
            modifier = Modifier
                .weight(0.5f),
            alignment = Alignment.Start
        )
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginForm(
                username = username,
                onUsernameChange = { loginViewModel.onUsernameChange(it) },
                password = password,
                onPasswordChange = { loginViewModel.onPasswordChange(it) },
                isPasswordField = passwordVisibility,
                onPasswordVisibilityChange = { passwordVisibility = !passwordVisibility }
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage.isNotEmpty()) {
                ShowErrorMessage(errorMessage)
            }
            if (loading) {
                CircularProgressIndicator()
            } else {
                LoginActions(
                    onCreateAccountClick = { loginViewModel.openCreateAccount(context) },
                    username = username,
                    password = password,
                    onLoginButtonClick = { loginViewModel.login() }
                )
            }
        }
    }
}