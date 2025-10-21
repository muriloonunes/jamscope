package com.mno.jamscope.features.login.webauth.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.ui.theme.JamscopePreviewTheme
import com.mno.jamscope.ui.theme.LoginTypography

@Composable
fun LoginHeaderWeb(
    modifier: Modifier = Modifier,
    alignment: Alignment.Horizontal,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        Text(
            text = stringResource(R.string.welcome_to_jamscope),
            style = LoginTypography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.login_please),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginHeaderWebPreview() {
    JamscopePreviewTheme {
        LoginHeaderWeb(
            modifier = Modifier,
            alignment = Alignment.CenterHorizontally
        )
    }
}