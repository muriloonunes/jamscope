package com.mno.jamscope.features.settings.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R

@Composable
fun SettingSwitch(
    icon: ImageVector,
    @StringRes iconDesc: Int,
    @StringRes name: Int,
    state: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        content = {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(id = iconDesc),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(id = name),
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Start,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = state,
                        onCheckedChange = { onClick() }
                    )
                }
            }
        }
    )
}

@Composable
fun SettingsClickableComp(
    icon: ImageVector,
    @StringRes iconDesc: Int,
    @StringRes name: Int,
    showIcon: Boolean,
    @StringRes subtitle: Int?,
    onClick: () -> Unit
) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp),
        onClick = { onClick() },
    ) {
        val context = LocalContext.current
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

        val appName = stringResource(id = R.string.app_name)
        val versionName = packageInfo.versionName
        val textString = if (name == R.string.app_name) {
            "$appName v$versionName"
        } else {
            stringResource(id = name)
        }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(id = iconDesc),
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Column {
                        Text(
                            text = textString,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier
                                .padding(8.dp),
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (subtitle != null) {
                            Text(
                                text = stringResource(id = subtitle),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier
                                    .padding(start = 8.dp),
                                textAlign = TextAlign.Start,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    if (showIcon) {
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = stringResource(id = R.string.open_setting_tile)
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun SettingsHorizontalMenu(
    modifier: Modifier = Modifier,
    tiles: List<Int>,
    selected: Int,
    onTileSelected: (Int) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        for (tile in tiles) {
            item {
                SettingMenuItem(
                    name = tile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    selected = tile == selected,
                    onClick = { onTileSelected(tile) }
                )
            }
        }
    }
}

@Composable
fun SettingSectionTitle(
    @StringRes name: Int
) {
    Text(
        text = stringResource(id = name),
        style = MaterialTheme.typography.titleLarge.copy(
            color = MaterialTheme.colorScheme.surfaceTint
        )
    )
}

@Composable
fun SettingMenuItem(
    @StringRes name: Int,
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected)
        MaterialTheme.colorScheme.surfaceContainerHigh
    else
        MaterialTheme.colorScheme.surfaceContainerLow

    val textColor = if (selected)
        MaterialTheme.colorScheme.onSurface
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Text(
        text = stringResource(id = name),
        style = MaterialTheme.typography.titleLarge.copy(color = textColor),
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}