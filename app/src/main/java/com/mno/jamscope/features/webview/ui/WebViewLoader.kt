package com.mno.jamscope.features.webview.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.mno.jamscope.R

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewLoader(
    url: String,
    title: String,
    allowedHost: String? = null,
    enableJavaScript: Boolean = true,
    callbackScheme: String? = null,
    onCallbackDetected: ((String) -> Unit)? = null,
    onNavigateBack: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = title,
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                }
            },
        )
    }) { innerPadding ->
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    this.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    settings.javaScriptEnabled = enableJavaScript

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest,
                        ): Boolean {
                            val urlStr = request.url.toString()
                            if (callbackScheme != null && urlStr.startsWith(callbackScheme)) {
                                onCallbackDetected?.invoke(urlStr)
                                return true // bloqueia o WebView de tentar abrir
                            }

                            if (allowedHost != null && !urlStr.contains(allowedHost)) {
                                return true // bloqueia navegação
                            }

                            return false
                        }

                        override fun onReceivedError(
                            view: WebView,
                            request: WebResourceRequest,
                            error: WebResourceError,
                        ) {
                            if (request.isForMainFrame) {
                                val safeMsg = error.description
                                    ?.toString()
                                    ?.replace("<", "&lt;")
                                    ?.replace(">", "&gt;")
                                    ?: "Erro desconhecido"
                                view.loadDataWithBaseURL(
                                    "about:blank",
                                    createErrorString(safeMsg),
                                    "text/html",
                                    "utf-8",
                                    null
                                )
                            }
                        }

                        override fun onReceivedHttpError(
                            view: WebView,
                            request: WebResourceRequest,
                            errorResponse: WebResourceResponse,
                        ) {
                            if (request.isForMainFrame) {
                                view.loadDataWithBaseURL(
                                    "about:blank",
                                    createErrorString("HTTP ${errorResponse.statusCode}"),
                                    "text/html",
                                    "utf-8",
                                    null
                                )
                            }
                        }
                    }
                }
            }, update = {
                it.loadUrl(url)
            }, modifier = Modifier.padding(innerPadding)
        )
    }
}

private fun createErrorString(errorMessage: String): String {
    return "<html><body><div align=\"center\">$errorMessage</div></body></html>"
}