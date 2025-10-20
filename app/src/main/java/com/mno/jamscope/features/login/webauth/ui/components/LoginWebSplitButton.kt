package com.mno.jamscope.features.login.webauth.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonDefaults.leadingButtonContentPaddingFor
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mno.jamscope.ui.theme.JamscopePreviewTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginWebSplitButton(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit,
) {
    val size = SplitButtonDefaults.MediumContainerHeight
    SplitButtonLayout(
        leadingButton = {
            val leadingButtonShapes = SplitButtonDefaults.leadingButtonShapesFor(size)
            val colors = ButtonDefaults.buttonColors()
            val contentPadding =
                leadingButtonContentPaddingFor(size)
            Surface(
                modifier = modifier.semantics { role = Role.Button },
                shape = leadingButtonShapes.shape,
                color = colors.containerColor,
                contentColor = colors.contentColor
            ) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = 48.dp,
                            minHeight = 40.0.dp,
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Login")
                }
            }
        },
        trailingButton = {
            SplitButtonDefaults.TrailingButton(
                onClick = { onLoginClick() },
                shapes = SplitButtonDefaults.trailingButtonShapesFor(size),
                contentPadding = SplitButtonDefaults.trailingButtonContentPaddingFor(size)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "",
                    modifier = Modifier.size(
                        SplitButtonDefaults.MediumTrailingButtonIconSize
                    )
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginWebSplitButtonPreview() {
    JamscopePreviewTheme {
        LoginWebSplitButton(
            onLoginClick = {}
        )
    }
}