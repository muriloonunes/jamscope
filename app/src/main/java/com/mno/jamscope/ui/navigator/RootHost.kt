package com.mno.jamscope.ui.navigator

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
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
import com.mno.jamscope.ui.components.LoadLibrariesScreen
import com.mno.jamscope.ui.components.WebViewLoader
import com.mno.jamscope.ui.screen.HomePager
import com.mno.jamscope.ui.screen.ProfileTela
import com.mno.jamscope.ui.screen.SettingsTela
import com.mno.jamscope.ui.theme.LocalWindowSizeClass
import com.mno.jamscope.ui.viewmodel.SettingsViewModel

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
                HomePager(friendsViewModel)
            }
            composable<Destination.SettingsScreen> {
                val settingsViewModel: SettingsViewModel = hiltViewModel()
                SettingsTela(
                    settingsViewModel = settingsViewModel
                )
            }
            composable<Destination.ProfileScreen> {
                ProfileTela(
                    listState = rememberLazyListState(),
                    windowSizeClass = LocalWindowSizeClass.current
                )
            }
            composable<Destination.WebViewScreen> {
                WebViewLoader()
            }
            composable<Destination.LibrariesScreen> {
                LoadLibrariesScreen()
            }
        }
    }
}