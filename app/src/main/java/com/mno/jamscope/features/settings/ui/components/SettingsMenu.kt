package com.mno.jamscope.features.settings.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun SettingsHorizontalMenu(
    modifier: Modifier = Modifier,
    tiles: List<Int>,
    selected: Int,
    onTileSelected: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(2.dp),
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

/**
 * Menu item used in the horizontal settings menu.
 */
@Composable
fun SettingMenuItem(
    @StringRes name: Int,
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)

    val backgroundColor = if (selected)
        MaterialTheme.colorScheme.secondaryContainer
    else
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)

    val textColor = if (selected)
        MaterialTheme.colorScheme.onSecondaryContainer
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        color = backgroundColor,
        shape = shape,
        tonalElevation = if (selected) 2.dp else 0.dp,
        modifier = modifier
            .clip(shape)
            .clickable { onClick() }
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = stringResource(id = name),
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth()
        )
    }
}