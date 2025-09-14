package com.mno.jamscope.features.friends.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mno.jamscope.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreenTopAppBar(
    onSettingIconClick: () -> Unit,
    onSortIconClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            FilledIconButton (
                onClick = onSettingIconClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.8f
                    )
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.open_settings_screen),
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        },
        actions = {
            FilledIconButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = onSortIconClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.8f
                    )
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = stringResource(R.string.sort_button),
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}