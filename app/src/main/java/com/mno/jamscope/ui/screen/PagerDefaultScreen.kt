package com.mno.jamscope.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
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
import com.mno.jamscope.R
import com.mno.jamscope.ui.navigator.Destination
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
    val listStates = remember { List(itensBarList.size) { LazyListState() } }
    val pagerState = rememberPagerState(initialPage = 0) { itensBarList.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            selectedItemIndex = page
        }
    }

    Scaffold(bottomBar = {
        NavigationBar {
            itensBarList.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedItemIndex,
                    onClick = {
                        selectedItemIndex = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                            listStates[index].animateScrollToItem(0)
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
                0 -> FriendsTela(
                    friendsViewModel = friendsViewModel,
                    listStates = listStates[page]
                )
                1 -> ProfileTela(
                    listState = listStates[page]
                )
            }
        }
    }
}