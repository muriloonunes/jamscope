package com.mno.jamscope.features.settings.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mno.jamscope.ui.theme.JamscopePreviewTheme


/**
 * A row used in dialogs to select an option, such as theme selection.
 */
@Composable
fun SettingDialogRow(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
                enabled = true
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(start = 8.dp),
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
private fun SettingDialogRowPreview() {
    JamscopePreviewTheme(true) {
        SettingDialogRow(
            text = "Option 1",
            selected = true,
            onClick = {}
        )
    }
}