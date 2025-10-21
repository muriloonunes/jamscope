package com.mno.jamscope.features.login.webauth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mno.jamscope.features.login.webauth.ui.components.LoginHeaderWeb
import com.mno.jamscope.features.login.webauth.ui.components.LoginWebFab
import com.mno.jamscope.features.login.webauth.ui.components.LoginWebWelcomeAnimation

@Composable
fun LoginHorizontalScreenWeb(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            LoginHeaderWeb(
                modifier = Modifier.fillMaxWidth(),
                alignment = Alignment.Start,
                textAlign = TextAlign.Start
            )
            Spacer(Modifier.height(32.dp))
            LoginWebFab(
                onLoginClick = onLoginClick
            )
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            LoginWebWelcomeAnimation(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}