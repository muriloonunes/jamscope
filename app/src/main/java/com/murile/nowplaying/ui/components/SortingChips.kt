package com.murile.nowplaying.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.murile.nowplaying.R
import com.murile.nowplaying.util.SortingType

@Composable
fun SortFilterChip(
    sortType: SortingType,
    currentSortingType: SortingType,
    onSortingTypeChanged: (SortingType) -> Unit
) {
    FilterChip(
        selected = currentSortingType == sortType,
        onClick = { onSortingTypeChanged(sortType) },
        label = {
            Text(
                text = when (sortType) {
                    SortingType.RECENTLY_PLAYED -> stringResource(R.string.recently_played)
                    SortingType.ALPHABETICAL -> stringResource(R.string.alphabetically)
                    SortingType.DEFAULT -> stringResource(R.string.default_sort)
                }
            )
        },
        modifier = Modifier.padding(8.dp),
        leadingIcon = if (currentSortingType == sortType) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Selected",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        }
    )
}