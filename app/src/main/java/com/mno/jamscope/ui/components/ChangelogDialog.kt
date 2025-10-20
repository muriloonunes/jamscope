package com.mno.jamscope.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import com.mno.jamscope.BuildConfig
import com.mno.jamscope.R
import com.mno.jamscope.util.Stuff.GITHUB_RELEASES_LINK
import com.mno.jamscope.domain.readRawFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogDialog(onDismissRequest: () -> Unit) {
    val context = LocalContext.current
    val changelogText = remember {
        context.readRawFile(fileRes = R.raw.changelog, expectedFileName = "changelog")
    }
    val scrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = stringResource(R.string.changelog),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                Text(
                    text = changelogText,
                    style = MaterialTheme.typography.bodyMedium
                )
                FullChangelogText()
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onDismissRequest() }
            ) { (Text(stringResource(R.string.dismiss))) }
        }
    )
}

@Composable
fun FullChangelogText() {
    val url = "$GITHUB_RELEASES_LINK${BuildConfig.VERSION_NAME}"
    val githubRelease = buildAnnotatedString {
        withLink(LinkAnnotation.Url(url = url)) {
            withStyle(
                style = MaterialTheme.typography.bodyMedium.toSpanStyle().copy(
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append(stringResource(R.string.full_changelog))
            }
        }
    }
    Text(githubRelease)
}