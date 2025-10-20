package com.mno.jamscope.features.login.webauth.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.mno.jamscope.R
import com.mno.jamscope.features.webview.ui.WebViewLoader
import com.mno.jamscope.util.Stuff

@Composable
fun WebLoginWrapper(
    url: String,
    onNavigateBack: () -> Unit,
    onCallbackDetected: (String) -> Unit,
) {
    WebViewLoader(
        onNavigateBack = { onNavigateBack() },
        title = stringResource(R.string.login),
        url = url,
        allowedHost = "last.fm",
        callbackScheme = "${Stuff.DEEPLINK_PROTOCOL_NAME}://auth",
        onCallbackDetected = {
            onCallbackDetected(it)
        },
    )
}