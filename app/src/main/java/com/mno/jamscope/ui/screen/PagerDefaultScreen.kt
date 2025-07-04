package com.mno.jamscope.ui.screen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.R
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.viewmodel.FriendsViewModel
import com.mno.jamscope.util.Stuff
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePager(
    friendsViewModel: FriendsViewModel
) {
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    val itensBarList = listOf(
        Stuff.BottomNavigationItem(
            stringResource(R.string.friends),
            Icons.Filled.Group,
            Icons.Outlined.Group,
            Destination.FriendsScreen
        ),
        Stuff.BottomNavigationItem(
            stringResource(R.string.profile),
            Icons.Filled.Person,
            Icons.Outlined.Person,
            Destination.ProfileScreen
        )
    )
    val listState = remember { List(itensBarList.size) { LazyListState() } }
    val gridState = remember { List(itensBarList.size) { LazyGridState() } }
    val pagerState = rememberPagerState(initialPage = 0) { itensBarList.size }
    val coroutineScope = rememberCoroutineScope()
    val windowSizeClass = LocalWindowSizeClass.current
    val windowsWidth = windowSizeClass.windowWidthSizeClass
    val isCompact = windowsWidth == WindowWidthSizeClass.COMPACT

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedItemIndex = page
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
        NavigationBar {
            itensBarList.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedItemIndex,
                    onClick = {
                        selectedItemIndex = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                            if (isCompact) {
                                listState[index].animateScrollToItem(0)
                            } else {
                                gridState[index].animateScrollToItem(0)
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    alwaysShowLabel = true
                )
            }
        }
    }) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> {
                    if (isCompact) {
                        FriendsTela(
                            friendsViewModel = friendsViewModel,
                            listState = listState[page],
                            windowSizeClass = windowSizeClass
                        )
                    } else {
                        FriendsTela(
                            friendsViewModel = friendsViewModel,
                            gridState = gridState[page],
                            windowSizeClass = windowSizeClass
                        )
                    }
                }
                1 -> ProfileTela(
                    listState = listState[page],
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}