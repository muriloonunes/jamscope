package com.mno.jamscope.features.login.webauth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.mno.jamscope.features.login.webauth.ui.components.LoginHeaderWeb
import com.mno.jamscope.features.login.webauth.ui.components.LoginWebWelcomeAnimation
import com.mno.jamscope.ui.theme.JamscopePreviewTheme

@Composable
fun LoginVerticalScreenWeb(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        LoginHeaderWeb(
            modifier = Modifier
                .fillMaxWidth(),
            alignment = Alignment.CenterHorizontally
        )
        LoginWebWelcomeAnimation(
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginVerticalScreenWebPreview() {
    JamscopePreviewTheme {
        LoginVerticalScreenWeb(
            modifier = Modifier.fillMaxWidth()
        )
    }
}