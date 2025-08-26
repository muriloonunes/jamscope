package com.mno.jamscope.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.util.SortingType

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
        Column(Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
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
    currentSortingType: SortingType,
    onSortingTypeChange: (SortingType) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
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
