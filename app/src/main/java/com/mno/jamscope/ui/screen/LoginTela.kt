package com.mno.jamscope.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mno.jamscope.R
import com.mno.jamscope.ui.components.ShowErrorMessage
import com.mno.jamscope.ui.viewmodel.LoginViewModel
import com.mno.jamscope.util.autoFillRequestHandler
import com.mno.jamscope.util.connectNode
import com.mno.jamscope.util.defaultFocusChangeAutoFill

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    val username by loginViewModel.username.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val errorMessage by loginViewModel.errorMessage.collectAsState()
    val loading by loginViewModel.loading.collectAsState()
    var passwordVisibility by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
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
            var showOptionsMenu by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 8.dp),
            ) {
                IconButton(
                    onClick = { showOptionsMenu = !showOptionsMenu },
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = stringResource(R.string.open_more_menu)
                    )
                }
                    DropdownMenu(
                    expanded = showOptionsMenu,
                    onDismissRequest = { showOptionsMenu = false }
                ) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.BugReport,
                                contentDescription = stringResource(R.string.report_bug_setting)
                            )
                        },
                        text = { Text(stringResource(R.string.report_bug_setting)) },
                        onClick = { loginViewModel.openBugReport(context) }
                    )
                }
            }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { loginViewModel.openCreateAccount(context) }
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.create_account),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.surfaceTint,
                            platformStyle = PlatformTextStyle(includeFontPadding = false)
                        ),
                        textDecoration = TextDecoration.Underline,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = stringResource(R.string.open_create_account),
                        modifier = Modifier
                            .size(18.dp)
                    )
                }
                Button(enabled = username.isNotEmpty() && password.isNotEmpty(), onClick = {
                    loginViewModel.login()
                }) {
                    Text(
                        text = stringResource(R.string.login)
                    )
                }
            }
        }
    }
}