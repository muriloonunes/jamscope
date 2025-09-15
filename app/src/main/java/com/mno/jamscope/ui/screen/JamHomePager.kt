package com.mno.jamscope.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mno.jamscope.R
import com.mno.jamscope.features.friends.ui.components.BottomNavigationItem
import com.mno.jamscope.ui.components.JamFloatingBottomBar
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.util.FriendsScreenCaller
import com.mno.jamscope.ui.util.ProfileScreenCaller
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun JamHomePager() {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val itemsBarList = listOf(
        BottomNavigationItem(
            stringResource(R.string.friends),
            Icons.Filled.Group,
            Icons.Outlined.Group,
            Destination.FriendsScreen
        ),
        BottomNavigationItem(
            stringResource(R.string.profile),
            Icons.Filled.Person,
            Icons.Outlined.Person,
            Destination.ProfileScreen
        )
    )
    val listState = remember { List(itemsBarList.size) { LazyListState() } }
    val pagerState = rememberPagerState(initialPage = 0) { itemsBarList.size }
    val coroutineScope = rememberCoroutineScope()
    val windowSizeClass = LocalWindowSizeClass.current

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            selectedItemIndex = page
        }
    }

    var topBarContent by remember {
        mutableStateOf<(@Composable () -> Unit)?>(
            null
        )
    }

    Scaffold(
        topBar = { topBarContent?.invoke() },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> {
                        FriendsScreenCaller(
                            windowSizeClass = windowSizeClass,
                            setTopBar = { topBarContent = it },
                            listState = listState[page]
                        )
                    }

                    1 -> {
                        ProfileScreenCaller(
                            listState = listState[page],
                            setTopBar = { topBarContent = it })
                    }
                }
            }
            JamFloatingBottomBar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp),
                buttons = itemsBarList,
                selectedItemIndex = selectedItemIndex,
                onItemSelected = { index ->
                    selectedItemIndex = index
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                        listState[index].animateScrollToItem(0)
                    }
                }
            )
        }
    }
}