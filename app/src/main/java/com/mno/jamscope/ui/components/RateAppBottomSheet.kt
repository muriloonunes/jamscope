package com.mno.jamscope.ui.components

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