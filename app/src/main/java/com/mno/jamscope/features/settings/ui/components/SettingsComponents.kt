package com.mno.jamscope.features.settings.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.ui.theme.NowPlayingTheme

@Composable
private fun SettingsTile(
    icon: ImageVector,
    iconDesc: String,
    name: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable () -> Unit,
) {
    val tileModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    val maxLines = if (subtitle != null) 1 else 2

    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        modifier = tileModifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .heightIn(min = 66.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = iconDesc,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = maxLines,
                    )
                    subtitle?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
            trailingContent()
        }
    }
}

@Composable
fun SettingSwitch(
    icon: ImageVector,
    @StringRes iconDesc: Int,
    @StringRes name: Int,
    state: Boolean,
    onClick: () -> Unit,
) {
    Spacer(Modifier.height(4.dp))
    SettingsTile(
        icon = icon,
        iconDesc = stringResource(id = iconDesc),
        name = stringResource(id = name),
        onClick = onClick,
        trailingContent = {
            Switch(
                checked = state,
                onCheckedChange = { onClick() }
            )
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
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)

    val appName = stringResource(id = R.string.app_name)
    val versionName =
        packageInfo?.versionName ?: stringResource(id = R.string.unknown)
    val textString = if (name == R.string.app_name) {
        "$appName v$versionName"
    } else {
        stringResource(id = name)
    }

    val subtitleString = subtitle?.let { stringResource(id = it) }

    SettingsTile(
        icon = icon,
        iconDesc = stringResource(iconDesc),
        name = textString,
        subtitle = subtitleString,
        onClick = onClick,
        trailingContent = {
            if (showIcon) {
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = stringResource(id = R.string.open_setting_tile)
                )
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsCompPreview() {
    NowPlayingTheme(2) {
        SettingsTile(
            icon = Icons.AutoMirrored.Outlined.Logout,
            iconDesc = "Settings icon",
            name = "Setting Name",
            subtitle = "Setting Subtitle",
            onClick = {},
            trailingContent = {
                Icon(
                    Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = stringResource(id = R.string.open_setting_tile)
                )
            }
        )
    }
}

@Preview
@Composable
private fun SettingsSwitchPreview() {
    NowPlayingTheme(2) {
        SettingsTile(
            icon = Icons.AutoMirrored.Outlined.Logout,
            iconDesc = "Settings icon",
            name = "Mostrar animação de reprodução",
            onClick = {},
            trailingContent = {
                Switch(
                    checked = true,
                    onCheckedChange = {}
                )
            }
        )
    }
}
