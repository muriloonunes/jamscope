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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.R
import com.mno.jamscope.features.friends.ui.FriendsTela
import com.mno.jamscope.features.friends.ui.components.BottomNavigationItem
import com.mno.jamscope.features.friends.viewmodel.FriendsViewModel
import com.mno.jamscope.features.profile.ui.ProfileTela
import com.mno.jamscope.features.profile.viewmodel.ProfileViewModel
import com.mno.jamscope.ui.navigator.Destination
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JamHomePager(
    friendsViewModel: FriendsViewModel,
) {
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
    val gridState = remember { List(itemsBarList.size) { LazyGridState() } }
    val pagerState = rememberPagerState(initialPage = 0) { itemsBarList.size }
    val coroutineScope = rememberCoroutineScope()
    val windowSizeClass = LocalWindowSizeClass.current
    val windowsWidth = windowSizeClass.windowWidthSizeClass
    val isCompact = windowsWidth == WindowWidthSizeClass.COMPACT

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
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
        bottomBar = {
            NavigationBar {
                itemsBarList.forEachIndexed { index, item ->
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
                    val state by friendsViewModel.uiState.collectAsStateWithLifecycle()
                    if (isCompact) {
                        FriendsTela(
                            state = state,
                            onRefresh = { friendsViewModel.onRefresh() },
                            onSettingIconClick = { friendsViewModel.navigateToSettings() },
                            onSortIconClick = { friendsViewModel.showSortingSheet() },
                            onSortingTypeChange = {
                                friendsViewModel.onSortingTypeChanged(it)
                                friendsViewModel.hideSortingSheet()
                            },
                            onHideSortingSheet = { friendsViewModel.hideSortingSheet() },
                            colorProvider = { name, isDark ->
                                friendsViewModel.getSecondaryContainerColor(name, isDark)
                            },
                            listState = listState[page],
                            windowSizeClass = windowSizeClass,
                            setTopBar = { topBarContent = it }
                        )
                    } else {
                        FriendsTela(
                            state = state,
                            onRefresh = { friendsViewModel.onRefresh() },
                            onSettingIconClick = { friendsViewModel.navigateToSettings() },
                            onSortIconClick = { friendsViewModel.showSortingSheet() },
                            onSortingTypeChange = {
                                friendsViewModel.onSortingTypeChanged(it)
                                friendsViewModel.hideSortingSheet()
                            },
                            onHideSortingSheet = { friendsViewModel.hideSortingSheet() },
                            colorProvider = { name, isDark ->
                                friendsViewModel.getSecondaryContainerColor(name, isDark)
                            },
                            gridState = gridState[page],
                            windowSizeClass = windowSizeClass,
                            setTopBar = { topBarContent = it }
                        )
                    }
                }

                1 -> {
                    val profileViewModel: ProfileViewModel = hiltViewModel()
                    val state by profileViewModel.uiState.collectAsStateWithLifecycle()
                    topBarContent = null
                    ProfileTela(
                        listState = listState[page],
                        windowSizeClass = windowSizeClass,
                        state = state,
                        onRefresh = { profileViewModel.onRefresh() },
                        onSeeMoreClick = { context, profile ->
                            profileViewModel.seeMore(context, profile)
                        }
                    )
                }
            }
        }
    }
}