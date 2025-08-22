package com.mno.jamscope.features.login.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mno.jamscope.ui.components.LoginActions
import com.mno.jamscope.ui.components.LoginForm
import com.mno.jamscope.ui.components.LoginHeader
import com.mno.jamscope.ui.components.ShowErrorMessage
import androidx.compose.ui.tooling.preview.Preview
import com.mno.jamscope.ui.theme.NowPlayingTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginVerticalScreen(
    modifier: Modifier,
    topSpacerHeight: Dp = 0.dp,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    openCreateAccount: (Context) -> Unit,
    onLoginClick: () -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    passwordVisibility: Boolean,
    username: String,
    password: String,
    loading: Boolean,
    errorMessage: String,
    context: Context,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(topSpacerHeight))
        LoginHeader(
            modifier = Modifier
                .fillMaxWidth(),
            alignment = Alignment.CenterHorizontally
        )
        Spacer(modifier = Modifier.height(8.dp))
        LoginForm(
            username = username,
            onUsernameChange = { onUsernameChange(it) },
            password = password,
            onPasswordChange = { onPasswordChange(it) },
            isPasswordField = passwordVisibility,
            onPasswordVisibilityChange = { onPasswordVisibilityChange() }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage.isNotEmpty()) {
            ShowErrorMessage(errorMessage)
        }
        if (loading) {
            CircularWavyProgressIndicator()
        } else {
            LoginActions(
                onCreateAccountClick = { openCreateAccount(context) },
                username = username,
                password = password,
                onLoginButtonClick = { onLoginClick() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun LoginVerticalScreenPreview() {
    NowPlayingTheme(themePreference = 1) {
        LoginVerticalScreen(
            modifier = Modifier.fillMaxSize(),
            topSpacerHeight = 64.dp,
            onUsernameChange = {},
            onPasswordChange = {},
            openCreateAccount = {},
            onLoginClick = {},
            onPasswordVisibilityChange = {},
            passwordVisibility = false,
            username = "username",
            password = "password",
            loading = false,
            errorMessage = "",
            context = LocalContext.current
        )
    }
}





