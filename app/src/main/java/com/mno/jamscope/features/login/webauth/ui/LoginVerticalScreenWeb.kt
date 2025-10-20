package com.mno.jamscope.features.login.webauth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mno.jamscope.features.login.webauth.ui.components.LoginHeaderWeb
import com.mno.jamscope.features.login.webauth.ui.components.LoginWebSplitButton
import com.mno.jamscope.ui.theme.JamscopePreviewTheme

@Composable
fun LoginVerticalScreenWeb(
    modifier: Modifier = Modifier,
    topSpacerHeight: Dp = 0.dp,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(topSpacerHeight))
        LoginHeaderWeb(
            modifier = Modifier
                .fillMaxWidth(),
            alignment = Alignment.CenterHorizontally
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            LoginWebSplitButton(
                onLoginClick = {
                    onLoginClick()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginVerticalScreenWebPreview() {
    JamscopePreviewTheme {
        LoginVerticalScreenWeb(
            modifier = Modifier.fillMaxWidth(),
            topSpacerHeight = 32.dp,
            onLoginClick = {}
        )
    }
}