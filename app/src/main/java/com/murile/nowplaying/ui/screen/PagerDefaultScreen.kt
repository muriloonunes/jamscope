package com.murile.nowplaying.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.murile.nowplaying.data.session.UserSessionManager
import com.murile.nowplaying.ui.components.BottomNavigationItem
import com.murile.nowplaying.ui.components.FRIENDS_SCREEN
import com.murile.nowplaying.ui.components.PROFILE_SCREEN
import com.murile.nowplaying.ui.components.SEARCH_SCREEN
import com.murile.nowplaying.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun HomePager(
    navController: NavController,
    userSessionManager: UserSessionManager,
    profileViewModel: ProfileViewModel
) {
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) } //TODO: talvez colocar isso aqui em um viewmodel?
    val itensBarList = listOf(
        BottomNavigationItem(
            "Friends",
            Icons.Filled.Group,
            Icons.Default.Group,
            FRIENDS_SCREEN
        ),
        BottomNavigationItem(
            "Search",
            Icons.Filled.Search,
            Icons.Default.Search,
            SEARCH_SCREEN
        ),
        BottomNavigationItem(
            "Profile",
            Icons.Filled.Person,
            Icons.Default.Person,
            PROFILE_SCREEN
        )
    )
    val pagerState = rememberPagerState(initialPage = 0) { 3 }
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
                0 -> Text("Friends")
                1 -> SearchTela()
                2 -> ProfileTela(
                    userSessionManager = userSessionManager,
                    navController = navController,
                    profileViewModel = profileViewModel
                )
            }
        }
    }
}