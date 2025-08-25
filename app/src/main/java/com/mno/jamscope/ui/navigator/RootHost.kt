package com.mno.jamscope.ui.navigator

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mno.jamscope.features.friends.viewmodel.FriendsViewModel
import com.mno.jamscope.features.login.ui.LoginScreen
import com.mno.jamscope.features.login.viewmodel.LoginViewModel
import com.mno.jamscope.features.profile.ui.ProfileTela
import com.mno.jamscope.features.profile.viewmodel.ProfileViewModel
import com.mno.jamscope.features.settings.ui.SettingsTela
import com.mno.jamscope.features.settings.viewmodel.SettingsViewModel
import com.mno.jamscope.ui.components.LoadLibrariesScreen
import com.mno.jamscope.ui.components.WebViewLoader
import com.mno.jamscope.ui.screen.JamHomePager
import com.mno.jamscope.ui.theme.LocalWindowSizeClass

@Composable
fun RootHost(
    navController: NavHostController,
    startDestination: Destination,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
            )
        },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = {
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 250, easing = LinearOutSlowInEasing)
            )
        }
    ) {
        navigation<Destination.LoginRoute>(
            startDestination = Destination.LoginScreen,
        ) {
            composable<Destination.LoginScreen> {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val state by loginViewModel.state.collectAsStateWithLifecycle()
                LoginScreen(
                    state = state,
                    onLoginClick = { loginViewModel.login() },
                    onPasswordChange = { loginViewModel.onPasswordChange(it) },
                    onUsernameChange = { loginViewModel.onUsernameChange(it) },
                    openCreateAccount = { loginViewModel.openCreateAccount(it) },
                    onPasswordVisibilityChange = { loginViewModel.onPasswordVisibilityChange() }
                )
            }
        }
        navigation<Destination.AppRoute>(
            startDestination = Destination.FriendsScreen
        ) {
            composable<Destination.FriendsScreen> {
                val friendsViewModel: FriendsViewModel = hiltViewModel()
                JamHomePager(friendsViewModel)
            }
            composable<Destination.SettingsScreen> {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                val state by settingsViewModel.uiState.collectAsState()
                SettingsTela(
                    uiState = state,
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
                    onShowLibrariesClick = { settingsViewModel.navigateToLibraries() },
                    onGithubProjectClick = { settingsViewModel.openGithubProject(it) }
                )
            }
            composable<Destination.ProfileScreen> {
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
                    onRefresh = { profileViewModel.onRefresh() },
                    onSeeMoreClick = { context, profile ->
                        profileViewModel.seeMore(context, profile)
                    }
                )
            }
            composable<Destination.WebViewScreen> {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                WebViewLoader(
                    onNavigateBack = { settingsViewModel.navigateBack() }
                )
            }
            composable<Destination.LibrariesScreen> {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                LoadLibrariesScreen(
                    onNavigateBack = { settingsViewModel.navigateBack() }
                )
            }
        }
    }
}