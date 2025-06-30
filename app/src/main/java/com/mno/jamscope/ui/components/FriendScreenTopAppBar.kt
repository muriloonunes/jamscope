package com.mno.jamscope.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mno.jamscope.R

@Composable
fun FriendScreenTopAppBar(
    onSettingIconClick: () -> Unit,
    onSortIconClick: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        IconButton(
            onClick = onSettingIconClick,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(R.string.open_settings_screen)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = onSortIconClick,
            modifier = Modifier.align(Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Sort,
                contentDescription = stringResource(R.string.sort_button)
            )
        }
    }
}