package com.mno.jamscope.features.login.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mno.jamscope.features.login.ui.components.LoginActions
import com.mno.jamscope.features.login.ui.components.LoginForm
import com.mno.jamscope.features.login.ui.components.LoginHeader
import com.mno.jamscope.ui.components.ShowErrorMessage

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginHorizontalScreen(
    modifier: Modifier,
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
    context: Context
) {
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
                onUsernameChange = { onUsernameChange(it) },
                password = password,
                onPasswordChange = { onPasswordChange(it) },
                isPasswordField = passwordVisibility,
                onPasswordVisibilityChange = { onPasswordVisibilityChange() },
                onLoginButtonClick = { onLoginClick() }
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
}