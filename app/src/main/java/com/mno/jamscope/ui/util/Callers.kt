package com.mno.jamscope.ui.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowWidthSizeClass
import com.mno.jamscope.features.friends.ui.FriendsTela
import com.mno.jamscope.features.friends.viewmodel.FriendsViewModel
import com.mno.jamscope.features.profile.ui.ProfileTela
import com.mno.jamscope.features.profile.viewmodel.ProfileViewModel
import com.mno.jamscope.features.settings.ui.SettingsTela
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@Composable
fun ProfileScreenCaller(
    listState: LazyListState = rememberLazyListState(),
) {
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val refreshing by profileViewModel.isRefreshing.collectAsStateWithLifecycle()
    val userProfile by profileViewModel.userProfile.collectAsStateWithLifecycle()
    val errorMessage by profileViewModel.errorMessage.collectAsStateWithLifecycle()
    val userRecentTracks by profileViewModel.recentTracks.collectAsStateWithLifecycle()
    val playingAnimationEnabled by profileViewModel.playingAnimationToggle.collectAsState()
    ProfileTela(
        isRefreshing = refreshing,
        userProfile = userProfile,
        errorMessage = errorMessage,
        recentTracks = userRecentTracks,
        playingAnimationEnabled = playingAnimationEnabled,
        windowSizeClass = LocalWindowSizeClass.current,
        listState = listState,
        onRefresh = { profileViewModel.onRefresh() },
        onSeeMoreClick = { context, profile ->
            profileViewModel.seeMore(context, profile)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreenCaller(
    windowSizeClass: WindowSizeClass,
    listState: LazyListState = rememberLazyListState(),
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val friendsViewModel: FriendsViewModel = hiltViewModel()
    val sortingType by friendsViewModel.sortingType.collectAsStateWithLifecycle()
    val refreshing by friendsViewModel.isRefreshing.collectAsStateWithLifecycle()
    val errorMessage by friendsViewModel.errorMessage.collectAsStateWithLifecycle()
    val recentTracksMap by friendsViewModel.recentTracksMap.collectAsStateWithLifecycle()
    val friends by friendsViewModel.friends.collectAsStateWithLifecycle()
    val cardBackgroundColorEnabled by friendsViewModel.cardBackgroundColorToggle.collectAsState()
    val playingAnimationEnabled by friendsViewModel.playingAnimationToggle.collectAsState()
    val friendToScrollTo by friendsViewModel.friendToScroll.collectAsStateWithLifecycle()
    val friendToOpen by friendsViewModel.friendToOpen.collectAsStateWithLifecycle()

    LaunchedEffect(sortingType) {
        listState.animateScrollToItem(0)
    }

    LaunchedEffect(friendToScrollTo, friends) {
        friendToScrollTo?.let { name ->
            if (friends.isNotEmpty()) {
                val index = friends.indexOfFirst { it.name == name }
                if (index != -1) {
                    try {
                        if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
                            listState.animateScrollToItem(index)
                        } else {
                            gridState.animateScrollToItem(index)
                        }
                    } finally {
                        //caso animateScroll lance um erro, improvável, mas mais seguro
                        friendsViewModel.onScrolledToFriend()
                    }
                } else {
                    friendsViewModel.onScrolledToFriend()
                    friendsViewModel.onFriendOpened()
                }
            }
        }
    }

    FriendsTela(
        sortingType = sortingType,
        isRefreshing = refreshing,
        errorMessage = errorMessage,
        recentTracks = recentTracksMap,
        friends = friends,
        cardBackgroundColorEnabled = cardBackgroundColorEnabled,
        playingAnimationEnabled = playingAnimationEnabled,
        friendToExtend = friendToOpen,
        onExtendedHandled = { friendsViewModel.onFriendOpened() },
        onRefresh = { friendsViewModel.onRefresh() },
        onSettingIconClick = { friendsViewModel.navigateToSettings() },
        onSortingTypeChange = {
            friendsViewModel.onSortingTypeChanged(it)
        },
        colorProvider = { name, isDark ->
            friendsViewModel.getSecondaryContainerColor(name, isDark)
        },
        listState = listState,
        gridState = gridState,
        windowSizeClass = windowSizeClass
    )
}

@Composable
fun SettingsScreenCaller(showTopAppBar: Boolean) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val state by settingsViewModel.uiState.collectAsState()
    SettingsTela(
        uiState = state,
        showTopAppBar = showTopAppBar,
        onTileSelected = { settingsViewModel.onTileSelect(it) },
        onNavigateBack = { settingsViewModel.navigateBack() },
        onSelectThemeClick = { settingsViewModel.showThemeDialog() },
        onLogOutClick = { settingsViewModel.showLogOutDialog() },
        hideThemeDialog = { settingsViewModel.hideThemeDialog() },
        setThemePreference = { settingsViewModel.setThemePreference(it) },
        hideLogOutDialog = { settingsViewModel.hideLogOutDialog() },
        onLogoutDialogClick = {
            settingsViewModel.logOutUser()
            settingsViewModel.hideLogOutDialog()
        },
        onSwitchClick = { settingsViewModel.toggleSwitch(it) },
        onDeleteAccountClick = { settingsViewModel.openDeleteAccount(it) },
        onBuyMeACoffeeClick = { settingsViewModel.openBuyMeACoffee(it) },
        onBugReportClick = { settingsViewModel.sendBugReportMail(it) },
        onSuggestFeatureClick = { settingsViewModel.navigateToWebView() },
        onShowLibrariesClick = { settingsViewModel.navigateToLibrariesLicenseScreen("libraries") },
        onAboutClick = { settingsViewModel.navigateToAboutScreen() }
    )
}