package com.murile.nowplaying.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.murile.nowplaying.R
import com.murile.nowplaying.data.repository.UserRepository
import com.murile.nowplaying.ui.components.APP_ROUTE
import com.murile.nowplaying.ui.components.LOGIN_ROUTE
import com.murile.nowplaying.ui.components.ShowErrorMessage
import com.murile.nowplaying.ui.viewmodel.LoginViewModel
import com.murile.nowplaying.util.autoFillRequestHandler
import com.murile.nowplaying.util.connectNode
import com.murile.nowplaying.util.defaultFocusChangeAutoFill
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel(),
    userRepository: UserRepository
) {
    val username by loginViewModel.username.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()
    val loading by loginViewModel.loading.collectAsState()
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    val userProfile by loginViewModel.userProfile.collectAsState()

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        val usernameFill = autoFillRequestHandler(autofillTypes = listOf(
            AutofillType.Username
        ), onFill = {
            loginViewModel.onUsernameChange(it)
        })
        val passwordFill = autoFillRequestHandler(autofillTypes = listOf(
            AutofillType.Password
        ), onFill = {
            loginViewModel.onPasswordChange(it)
        })
        Text(
            text = stringResource(R.string.welcome),
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = stringResource(R.string.login_please),
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = username,
            onValueChange = {
                loginViewModel.onUsernameChange(it)
                if (it.isEmpty()) usernameFill.requestVerifyManual()
            },
            label = {
                Text(
                    stringResource(R.string.username)
                )
            },
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false
            ),
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .connectNode(handler = usernameFill)
                .defaultFocusChangeAutoFill(handler = usernameFill)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = password,
            onValueChange = {
                loginViewModel.onPasswordChange(it)
                if (it.isEmpty()) passwordFill.requestVerifyManual()
            },
            label = {
                Text(
                    stringResource(R.string.password)
                )
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .connectNode(handler = passwordFill)
                .defaultFocusChangeAutoFill(handler = passwordFill),
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false, keyboardType = KeyboardType.Password
            ),
            maxLines = 1,
            visualTransformation = if (passwordVisibility) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        imageVector = if (passwordVisibility) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                        contentDescription = stringResource(R.string.password_visibilty)
                    )
                }
            })
        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            ShowErrorMessage(errorMessage)
        }

        if (loading) {
            CircularProgressIndicator()
        } else {
            Button(enabled = username.isNotEmpty() && password.isNotEmpty(), onClick = {
                coroutineScope.launch { loginViewModel.login() }
            }) {
                Text(
                    text = stringResource(R.string.login)
                )
            }
        }
        if (userProfile != null) {
            LaunchedEffect(userProfile) {
                userRepository.saveUserProfile(userProfile!!)
                navController.navigate(APP_ROUTE) {
                    popUpTo(LOGIN_ROUTE) { inclusive = true }
                }
            }
        }
    }

}
