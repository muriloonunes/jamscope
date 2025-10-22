package com.mno.jamscope.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.ToggleButtonShapes
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.features.friends.ui.SortingType

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SortToggleButton(
    sortType: SortingType,
    currentSortingType: SortingType,
    onSortingTypeChanged: (SortingType) -> Unit,
    shapes: ToggleButtonShapes,
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@ExperimentalMaterial3Api
@Composable
fun SortingBottomSheet(
    currentSortingType: SortingType,
    onSortingTypeChange: (SortingType) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(Modifier.height(8.dp))

            SortingLazyRow(
                currentSortingType = currentSortingType,
                onSortingTypeChange = onSortingTypeChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SortingLazyRow(
    modifier: Modifier = Modifier,
    currentSortingType: SortingType,
    onSortingTypeChange: (SortingType) -> Unit,
) {
    LazyRow(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        state = rememberLazyListState()
    ) {
        itemsIndexed(
            items = SortingType.entries.toTypedArray(),
            key = { i, item -> item.name }) { index, sortingType ->
            val count = SortingType.entries.size
            val shapes = when (index) {
                0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                count - 1 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            }
            SortToggleButton(
                sortType = sortingType,
                currentSortingType = currentSortingType,
                onSortingTypeChanged = onSortingTypeChange,
                shapes = shapes
            )
        }
    }
}
