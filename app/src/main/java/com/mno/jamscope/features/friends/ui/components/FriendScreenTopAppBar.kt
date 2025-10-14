package com.mno.jamscope.features.friends.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row // Added import
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendScreenTopAppBar(
    onSettingIconClick: () -> Unit,
    onSortIconClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    showingSortingRow: Boolean,
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (showingSortingRow) 180f else 0f,
        label = "Sort icon rotation"
    )

    TopAppBar(
        title = { Text("") },
        navigationIcon = {
            FilledIconButton(
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.sort),
                        contentDescription = stringResource(R.string.sort_button),
                        tint = MaterialTheme.colorScheme.surface
                    )
                    Icon(
                        painter = painterResource(R.drawable.arrow),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .rotate(rotationAngle)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

var topBarHeight = 0.dp
@Composable
fun FriendsTopRowBar(
    onSettingIconClick: () -> Unit = {},
    onSortIconClick: () -> Unit = {},
    showingSortingRow: Boolean = false,
) {
    val rotationAngle by animateFloatAsState(
        targetValue = if (showingSortingRow) 180f else 0f,
        label = "Sort icon rotation"
    )
    val density = LocalDensity.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .onSizeChanged {
                topBarHeight = with (density) { it.height.toDp() }
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            onClick = { onSettingIconClick() },
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
        FilledIconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = { onSortIconClick() },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.onSurface.copy(
                    alpha = 0.8f
                )
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.sort),
                    contentDescription = stringResource(R.string.sort_button),
                    tint = MaterialTheme.colorScheme.surface
                )
                Icon(
                    painter = painterResource(R.drawable.arrow),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .rotate(rotationAngle)
                )
            }
        }
    }
}