package com.mno.jamscope.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
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

@ExperimentalMaterial3Api
@Composable
fun SortingBottomSheet(
    sortingType: SortingType,
    onSortingTypeChanged: (SortingType) -> Unit,
    onDismissRequest: () -> Unit
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

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                items(SortingType.entries.toTypedArray()) { sortType ->
                    SortFilterChip(
                        sortType = sortType,
                        currentSortingType = sortingType,
                        onSortingTypeChanged = onSortingTypeChanged
                    )
                }
            }
        }
    }
}

//@ExperimentalMaterial3Api
//@Composable
//fun RateAppBottomSheet(
//    onDismissRequest: () -> Unit,
//) {
//    val viewModel: RateAppViewModel = hiltViewModel()
//    val context = LocalContext.current
//    ModalBottomSheet(
//        onDismissRequest = onDismissRequest,
//        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
//    ) {
//        Column(Modifier
//            .padding(4.dp)
//            .align(Alignment.CenterHorizontally)
//        ) {
//            Text(
//                text = stringResource(R.string.liking_app_text),
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//            )
//            Text(
//                text = stringResource(R.string.liking_app_expanded_text),
//                style = MaterialTheme.typography.titleMedium,
//                modifier = Modifier.align(Alignment.CenterHorizontally)
//
//            )
//            Spacer(Modifier.height(8.dp))
//            Row(
//                modifier = Modifier
//                    .align(Alignment.End)
//            ) {
//                TextButton(
//                    onClick = { onDismissRequest() }
//                ) { (Text(stringResource(R.string.no_not_today_review))) }
//                Button(
//                    onClick = {
//                        viewModel.openPlayStore(context)
//                        onDismissRequest()
//                    }
//                ) { (Text(stringResource(R.string.ok))) }
//            }
//        }
//    }
//}