package com.mno.jamscope.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.mno.jamscope.R
import com.mno.jamscope.util.SortingType

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SortToggleButton(
    sortType: SortingType,
    currentSortingType: SortingType,
    onSortingTypeChanged: (SortingType) -> Unit,
    shapes: ToggleButtonShapes = ToggleButtonDefaults.shapesFor(ButtonDefaults.MinHeight),
) {
    OutlinedToggleButton(
        modifier = Modifier.semantics { role = Role.RadioButton }
            .padding(ButtonGroupDefaults.ConnectedSpaceBetween),
        checked = currentSortingType == sortType,
        onCheckedChange = { onSortingTypeChanged(sortType) },
        shapes = shapes
    ) {
        if (currentSortingType == sortType) {
            Icon(
                imageVector = Icons.Filled.Done,
                contentDescription = "Selected",
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        } else {
            null
        }
        Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
        Text(
            text = when (sortType) {
                SortingType.RECENTLY_PLAYED -> stringResource(R.string.recently_played)
                SortingType.ALPHABETICAL -> stringResource(R.string.alphabetically)
                SortingType.DEFAULT -> stringResource(R.string.default_sort)
            }
        )
    }
}